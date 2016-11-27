package com.glacialrush.api.game;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.phantomapi.sync.Task;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.component.Controller;
import com.glacialrush.api.component.PlayerDataComponent;
import com.glacialrush.api.dispatch.InnerDispatcher;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.game.event.ChatEvent;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.loadout.Loadout;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.PlayerObject;
import com.glacialrush.api.game.object.ResourceLoader;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.game.obtainable.ObtainableBank;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.rank.Rank;
import com.glacialrush.api.text.RawText;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.packet.NCP;
import com.glacialrush.packet.NMS;
import com.glacialrush.packet.VoteListener;
import com.glacialrush.xapi.Area;

@SuppressWarnings("deprecation")
public class GameController extends Controller
{
	public static GameController instance;
	
	protected final GList<Game> games;
	protected final GMap<Player, PlayerObject> playerObjects;
	protected final GList<Map> maps;
	protected final BuildGame buildGame;
	protected final PlayerDataComponent pdc;
	protected final ObtainableBank obtainableBank;
	protected Location spawn;
	protected boolean blocking;
	protected boolean newGame;
	public static boolean buildMode;
	
	public GameController(final GlacialPlugin pl, PlayerDataComponent pdc, Boolean bm)
	{
		super(pl);
		
		buildMode = bm;
		instance = this;
		pl.getServerDataComponent().load();
		this.pdc = pdc;
		this.playerObjects = new GMap<Player, PlayerObject>();
		obtainableBank = new ObtainableBank(pl);
		games = new GList<Game>();
		maps = new GList<Map>();
		blocking = false;
		this.d = new InnerDispatcher(pl, "GameController");
		d.overbose("GameController Started");
		this.newGame = false;
		
		buildGame = new BuildGame(this);
		
		pl.scheduleSyncRepeatingTask(100, 100, new Runnable()
		{
			@Override
			public void run()
			{
				if(buildMode)
				{
					return;
				}
				
				if(newGame)
				{
					newGame = false;
					
					safeAddGame();
				}
				
				else if(games.isEmpty())
				{
					safeAddGame();
				}
			}
		});
		
		pl.scheduleSyncTask(1, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player i : pl.onlinePlayers())
				{
					PlayerObject po = new PlayerObject(i, GameController.this);
					po.setUsingPack(true);
					getPlayerObjects().put(i, po);
				}
				
				for(World i : pl.getServer().getWorlds())
				{
					for(Guardian j : i.getEntitiesByClass(Guardian.class))
					{
						j.remove();
					}
					
					for(IronGolem j : i.getEntitiesByClass(IronGolem.class))
					{
						j.remove();
					}
				}
			}
		});
		
		pl.scheduleSyncRepeatingTask(10, 0, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player i : playerObjects.keySet())
				{
					playerObjects.get(i).tickArrows();
				}
			}
		});
		
		new Task(20)
		{
			@Override
			public void run()
			{
				for(Player i : pl.onlinePlayers())
				{
					try
					{
						if(getGame(i) == null && gpd(i).getResourcePackAccepted() && gpo(i).getUsingPack())
						{
							join(getRegionedGames().get(0), i);
						}
					}
					
					catch(Exception e)
					{
						
					}
				}
			}
		};
		
		pl.scheduleSyncRepeatingTask(1, 0, new Runnable()
		{
			
			@Override
			public void run()
			{
				for(Player i : playerObjects.keySet())
				{
					if(playerObjects.get(i).getDisabled())
					{
						i.setVelocity(new Vector(0, 0, 0));
					}
				}
			}
		});
		
		pl.scheduleSyncRepeatingTask(0, 10, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player i : pl.onlinePlayers())
				{
					String pow = "";
					
					if(getGame(i) != null && getGame(i).getType().equals(GameType.REGIONED))
					{
						RegionedGame rgg = (RegionedGame) getGame(i);
						
						for(Faction j : Faction.all())
						{
							if(!rgg.isEliminated(j))
							{
								int n = rgg.map.factionTerritoryCount(j) + rgg.map.factionCaptureCount(j) + rgg.factionHandler.getPlayers(j).size();
								
								pow = pow + j.getColor() + n + " ";
							}
						}
					}
					
					NMS.sendTabTitle(i, ChatColor.AQUA + "Glacial Rush", ChatColor.GREEN + "Your Ping: " + NMS.ping(i) + "ms\n" + pow);
					
					if(getGame(i) == null)
					{
						if(!GlacialPlugin.instance().getServerDataComponent().isProduction())
						{
							return;
						}
						
						if(i.getLocation().getBlockY() > 78)
						{
							i.teleport(pl.getServerDataComponent().getHub());
						}
					}
				}
			}
		});
		
		new VoteListener(pl)
		{
			public void run()
			{
				final Player player = pl.findPlayer(getName());
				
				if(player != null)
				{
					Game game = getGame(player);
					Statistic.SUPPORT_VOTES.add(player);
					
					if(game != null && game.getType().equals(GameType.REGIONED))
					{
						RegionedGame rg = ((RegionedGame) game);
						rg.getNotificationHandler().queue(player, NotificationPreset.VOTED.format(null, null, null));
						rg.getExperienceHandler().giveXpRaw(player, 12345l, Experience.VOTE);
					}
					
					else
					{
						NotificationPreset.VOTED.format(null, null, null).show(player);
						
						pl.scheduleSyncTask(50, new Runnable()
						{
							@Override
							public void run()
							{
								gpd(player).setExperience(gpd(player).getExperience() + 12345l);
								long tsk = gpd(player).getSkillNext() + 12345l;
								
								if(tsk >= 500)
								{
									long sk = 0;
									
									while(tsk >= 500)
									{
										sk++;
										tsk -= 500;
									}
									
									gpd(player).setSkill(gpd(player).getSkill() + sk);
								}
								
								gpd(player).setSkillNext(tsk);
								NotificationPreset.VOTE_EXPERIENCE.format(null, new Object[] {12345 + ""}, null).show(player);
							}
						});
					}
					
					pl.scheduleSyncTask(80, new Runnable()
					{
						@Override
						public void run()
						{
							pl.getMarketController().chanceFragments(player, 50, 4);
						}
					});
				}
				
			}
		};
	}
	
	public void destroy(RegionedGame rg)
	{
		for(Game i : games.copy())
		{
			if(i.equals(rg))
			{
				i.getHandlers().clear();
				games.remove(i);
			}
		}
	}
	
	@EventHandler
	public void onDestroy(HangingBreakEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDestroy(HangingBreakByEntityEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = false)
	public void itemFrameItemRemoval(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof ItemFrame)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void disableFrameRotate(PlayerInteractEntityEvent e)
	{
		if(e.getRightClicked().getType().equals(EntityType.ITEM_FRAME))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerDamage(EntityDamageEvent e)
	{
		if(e.getEntityType().equals(EntityType.PLAYER))
		{
			if(getGame((Player) e.getEntity()) == null)
			{
				e.setCancelled(true);
			}
		}
	}
	
	public void launch(final Player p)
	{
		p.setAllowFlight(false);
		p.setFlying(false);
		
		NCP.exemptFly(p);
		
		if(p.isSneaking())
		{
			p.setVelocity(p.getLocation().getDirection().multiply(1.0).setY(2.1));
			p.getLocation().getWorld().createExplosion(p.getLocation(), 0.0f);
			Area a = new Area(p.getLocation(), 9.9);
			
			for(Entity i : a.getNearbyEntities())
			{
				if(i.getType().equals(EntityType.PLAYER) && p.equals((Player) i))
				{
					continue;
				}
				
				Vector v = i.getLocation().subtract(p.getLocation()).toVector();
				i.setVelocity(v.setY(1.0));
			}
		}
		
		else
		{
			p.setVelocity(p.getLocation().getDirection().multiply(1.5).setY(1));
		}
		
		p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0f, 1.6f);
		ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1, 40, p.getLocation(), 32);
		
		pl.scheduleSyncTask(30, new Runnable()
		{
			@Override
			public void run()
			{
				NCP.unExemptFly(p);
			}
		});
	}
	
	public void bolt(Player p)
	{
		p.setVelocity(p.getLocation().getDirection().multiply(3.5).setY(0.4));
		p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0f, 1.6f);
		ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1, 40, p.getLocation(), 32);
	}
	
	@EventHandler
	public void onSprint(PlayerToggleSprintEvent e)
	{
		if(getGame(e.getPlayer()) != null)
		{
			return;
		}
		
		if(e.isSprinting())
		{
			bolt(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onLaunch(PlayerInteractEvent e)
	{
		if(getGame(e.getPlayer()) != null)
		{
			return;
		}
		
		if(e.getAction().equals(Action.PHYSICAL))
		{
			if(e.getClickedBlock().getType() == Material.STONE_PLATE)
			{
				bolt(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e)
	{
		if(getGame(e.getPlayer()) != null)
		{
			return;
		}
		
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE)
		{
			return;
		}
		
		e.setCancelled(true);
		launch(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerMovement(PlayerMoveEvent e)
	{
		if(getGame(e.getPlayer()) != null)
		{
			return;
		}
		
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR && !e.getPlayer().isFlying())
		{
			e.getPlayer().setAllowFlight(true);
		}
	}
	
	public GlacialPlugin pl()
	{
		return pl;
	}
	
	public void register(Listener list)
	{
		pl.register(list);
	}
	
	public void addGame(RegionedGame game)
	{
		d.overbose(ChatColor.GREEN + "Injecting Game...");
		games.add(game);
		game.start();
		d.overbose(ChatColor.GREEN + "Game Injected");
	}
	
	@Override
	public void preEnable()
	{
		super.preEnable();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDmg(PlayerItemDamageEvent e)
	{
		e.setCancelled(true);
		e.getPlayer().updateInventory();
	}
	
	@Override
	public void postEnable()
	{
		super.postEnable();
		d.overbose("Starting Build Game");
		buildGame.start();
		
		for(Game i : games)
		{
			i.start();
		}
		
		d.overbose("FORCE BLOCKING DISABLED");
		setBlocking(false);
		
		pl.scheduleSyncTask(5, new Runnable()
		{
			@Override
			public void run()
			{
				if(pl.getServerDataComponent().isProduction())
				{
					s("---- PRODUCTION MODE ENABLED ----");
					safeAddGame();
				}
				
				else
				{
					s("---- PRODUCTION MODE DISABLED ----");
				}
				
				pl.calc();
			}
		});
	}
	
	public GList<RegionedGame> getRegionedGames()
	{
		GList<RegionedGame> rg = new GList<RegionedGame>();
		GList<Map> mps = new GList<Map>();
		
		for(Game i : games)
		{
			if(i.getType().equals(GameType.REGIONED))
			{
				if(((RegionedGame) i).isRunning())
				{
					if(mps.contains(((RegionedGame) i).getMap()))
					{
						continue;
					}
					
					mps.add(((RegionedGame) i).getMap());
					rg.add((RegionedGame) i);
				}
			}
		}
		
		return rg;
	}
	
	public void newGame(RegionedGame game)
	{
		newGame = true;
		int index = 0;
		boolean found = false;
		
		for(Game i : games)
		{
			if(i.getType().equals(GameType.REGIONED))
			{
				RegionedGame rg = (RegionedGame) i;
				if(game.id == rg.id)
				{
					found = true;
					break;
				}
			}
			
			index++;
		}
		
		if(found)
		{
			games.remove(index);
		}
	}
	
	public PlayerData gpd(Player p)
	{
		return pdc.get(p);
	}
	
	public RegionedGame safeAddGame()
	{
		if(pl.onlinePlayers().isEmpty())
		{
			return null;
		}
		
		if(buildMode)
		{
			return null;
		}
		
		GList<Map> sel = new GList<Map>();
		
		for(Map i : maps)
		{
			boolean used = false;
			
			for(Game j : games)
			{
				if(j.getType().equals(GameType.REGIONED))
				{
					RegionedGame g = (RegionedGame) j;
					
					if(g.getMap().equals(j))
					{
						used = true;
					}
				}
			}
			
			if(!used)
			{
				if(i.isLocked())
				{
					d.overbose(ChatColor.DARK_GREEN + "Found Game Slot: " + i.getName());
					sel.add(i);
				}
			}
		}
		
		d.overbose(ChatColor.LIGHT_PURPLE + "Rollin the dice.");
		
		if(!sel.isEmpty())
		{
			RegionedGame game = new RegionedGame(this, sel.pickRandom());
			games.add(game);
			game.start();
			return game;
		}
		
		d.sfailure("No Games Found!");
		return null;
	}
	
	@Override
	public void preDisable()
	{
		d.overbose("Stopping!");
		blocking = true;
		o("BLOCKING ENABLED");
		d.overbose(ChatColor.RED + "Stopping Build Game");
		buildGame.stop();
		
		for(Game i : games.copy())
		{
			d.overbose("Stopping Game: " + i.getType().toString());
			i.stop();
		}
		
		for(Map i : maps.copy())
		{
			d.overbose(ChatColor.RED + "Undrawing Map: " + ChatColor.YELLOW + i.getName());
			i.getMdm().undraw();
		}
		
		obtainableBank.stop();
		pl.getServerDataComponent().save();
		super.preDisable();
	}
	
	public Game getGame(Player p)
	{
		for(Game i : games)
		{
			if(i.players().contains(p))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public Location getSpawn()
	{
		return spawn;
	}
	
	public void setSpawn(Location spawn)
	{
		this.spawn = spawn;
	}
	
	public boolean isNewGame()
	{
		return newGame;
	}
	
	public void setNewGame(boolean newGame)
	{
		this.newGame = newGame;
	}
	
	public PlayerDataComponent getPdc()
	{
		return pdc;
	}
	
	public ObtainableBank getObtainableBank()
	{
		return obtainableBank;
	}
	
	public void left(Player p, Game game)
	{
		for(Game i : games)
		{
			if(game.equals(i))
			{
				continue;
			}
			
			i.leave(p);
		}
	}
	
	public void join(Game g, Player p)
	{
		if(buildGame.equals(g))
		{
			if(!buildGame.players().contains(p))
			{
				buildGame.join(p);
			}
			
			return;
		}
		
		if(getGame(p) != null)
		{
			if(getGame(p).equals(g))
			{
				return;
			}
		}
		
		if(!gpo(p).getUsingPack())
		{
			return;
		}
		
		d.overbose("Joining Game " + p.getName() + " <> " + g.getState().toString());
		
		if(buildGame.players().contains(p))
		{
			buildGame.leave(p);
		}
		
		for(Game i : games)
		{
			if(i.players().contains(p))
			{
				i.leave(p);
			}
		}
		
		g.join(p);
	}
	
	public void leave(Game g, Player p)
	{
		g.leave(p);
		d.overbose(p.getName() + " Left Grush Game");
	}
	
	@Override
	public void postDisable()
	{
		super.postDisable();
	}
	
	public GList<Game> getGames()
	{
		return games;
	}
	
	public GList<Map> getMaps()
	{
		return maps;
	}
	
	public BuildGame getBuildGame()
	{
		return buildGame;
	}
	
	public boolean isBlocking()
	{
		return blocking;
	}
	
	public void setBlocking(boolean blocking)
	{
		this.blocking = blocking;
		
		if(blocking)
		{
			o("BLOCKING ENABLED");
		}
	}
	
	public PlayerObject gpo(Player p)
	{
		return getPlayerObjects().get(p);
	}
	
	public void resetAllGames()
	{
		for(Game i : games)
		{
			i.stop();
		}
		
		games.clear();
		safeAddGame();
	}
	
	@EventHandler
	public void preCommand(PlayerCommandPreprocessEvent e)
	{
		if(!gpo(e.getPlayer()).getUsingPack())
		{
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent e)
	{
		e.setCancelled(true);
		
		if(!gpo(e.getPlayer()).getUsingPack())
		{
			return;
		}
		
		Player p = e.getPlayer();
		RegionedGame g = (RegionedGame) (getGame(p) != null ? getGame(p).type.equals(GameType.REGIONED) ? getGame(p) : null : null);
		String m = e.getMessage();
		Boolean gl = m.startsWith("!");
		Boolean ggl = m.startsWith("!!");
		
		if(ggl)
		{
			m = m.substring(2);
		}
		
		else if(gl)
		{
			m = m.substring(1);
		}
		
		ChatEvent c = new ChatEvent(g, p, m);
		pl.callEvent(c);
		
		if(c.isCancelled())
		{
			return;
		}
		
		m = c.getMessage();
		
		PlayerData pd = gpd(p);
		Rank r = Rank.best(pd.getRanks());
		Integer br = g != null ? g.getExperienceHandler().getBr(p) : pl.pdc.get(p).getBattleRank();
		Faction f = g == null ? Faction.neutral() : g.getFactionHandler().getFaction(p);
		String t = ChatColor.AQUA + "" + br + "" + f.getColor() + " [" + r.getName() + f.getColor() + "] " + p.getName() + ": " + r.getChatSuffix() + m;
		RawText rt = new RawText();
		
		if(ggl)
		{
			rt.addText("[!!] ", RawText.COLOR_RED);
		}
		
		else if(gl)
		{
			rt.addText("[!] ", RawText.COLOR_RED);
		}
		
		String fc = "";
		
		if(f.equals(Faction.enigma()))
		{
			fc = RawText.COLOR_RED;
		}
		
		else if(f.equals(Faction.omni()))
		{
			fc = RawText.COLOR_DARK_PURPLE;
		}
		
		else if(f.equals(Faction.cryptic()))
		{
			fc = RawText.COLOR_YELLOW;
		}
		
		else
		{
			fc = RawText.COLOR_AQUA;
		}
		
		rt.addTextWithHover(br + " ", RawText.COLOR_AQUA, p.getName() + "'s Battle rank is " + br, RawText.COLOR_AQUA);
		rt.addText("[", fc);
		rt.addTextWithHover(ChatColor.stripColor(r.getName()), fc, p.getName() + "'s rank is " + ChatColor.stripColor(r.getName()), RawText.COLOR_AQUA);
		rt.addText("] ", fc);
		
		if(f.equals(Faction.neutral()))
		{
			rt.addTextWithHover(p.getName() + ": ", fc, p.getName() + "' is not in game", RawText.COLOR_AQUA);
		}
		
		else
		{
			rt.addTextWithHover(p.getName() + ": ", fc, p.getName() + "' is ingame fighting as " + f.getName(), fc);
		}
		
		if(r.equals(Rank.OWNER))
		{
			rt.addText(m, RawText.COLOR_WHITE);
		}
		
		else
		{
			rt.addText(m, RawText.COLOR_GRAY);
		}
		
		if(ggl)
		{
			for(Player i : pl.onlinePlayers())
			{
				if(gpd(i).getRanks().contains(Rank.OWNER))
				{
					continue;
				}
				
				rt.tellRawTo(pl, i);
			}
		}
		
		else if(gl)
		{
			for(Player i : pl.onlinePlayers())
			{
				if(gpd(i).getRanks().contains(Rank.OWNER))
				{
					continue;
				}
				
				Game mg = getGame(i);
				
				if(g != null)
				{
					if(mg != null && mg.getType().equals(GameType.REGIONED) && ((RegionedGame) mg).equals(g))
					{
						rt.tellRawTo(pl, i);
					}
				}
				
				else
				{
					if(mg == null)
					{
						rt.tellRawTo(pl, i);
					}
				}
			}
		}
		
		else
		{
			for(Player i : pl.onlinePlayers())
			{
				if(gpd(i).getRanks().contains(Rank.OWNER))
				{
					continue;
				}
				
				Game mg = getGame(i);
				
				if(g != null)
				{
					if(mg != null && mg.getType().equals(GameType.REGIONED) && ((RegionedGame) mg).equals(g))
					{
						if(f.equals(((RegionedGame) mg).getFactionHandler().getFaction(i)))
						{
							rt.tellRawTo(pl, i);
						}
					}
				}
				
				else
				{
					if(mg == null)
					{
						rt.tellRawTo(pl, i);
					}
				}
			}
		}
		
		for(Player i : pl.onlinePlayers())
		{
			if(gpd(i).getRanks().contains(Rank.OWNER))
			{
				rt.tellRawTo(pl, i);
			}
		}
		
		o("Chat: " + t);
	}
	
	public void onJoin(Player p)
	{
		for(Player i : pl.onlinePlayers())
		{
			i.sendMessage(ChatColor.AQUA + "+ " + ChatColor.DARK_GRAY + p.getName());
		}
		
		pl.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "+ " + ChatColor.DARK_GRAY + p.getName());
		
		pl.scheduleSyncTask(4, new Runnable()
		{
			@Override
			public void run()
			{
				if(games.isEmpty())
				{
					safeAddGame();
				}
			}
		});
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(final PlayerJoinEvent e)
	{
		pl.scheduleSyncTask(1, new Runnable()
		{
			@Override
			public void run()
			{
				PlayerObject po = new PlayerObject(e.getPlayer(), GameController.this);
				PlayerData pd = pdc.get(e.getPlayer());
				getPlayerObjects().put(e.getPlayer(), po);
				po.disable();
				
				for(String i : pd.getOwned().copy())
				{
					if(!obtainableBank.getObtainableFilter().contains(i))
					{
						f(e.getPlayer().getUniqueId().toString() + " :: Invalid Item Data: " + i + " (should be removed)");
					}
				}
				
				for(String i : obtainableBank.getObtainableFilter().getStarters())
				{
					if(!pd.getOwned().contains(i))
					{
						pd.getOwned().add(i);
						s("Added Starter Item: " + i + " to " + pd.getName());
					}
				}
				
				Loadout loadout = pd.getLoadoutSet().getLoadout();
				
				if(loadout.getPrimaryWeapon().equals("none"))
				{
					for(String i : pd.getOwned())
					{
						if(obtainableBank.getObtainableFilter().isPrimaryWeapon(obtainableBank.resolve(i)))
						{
							s("Found default PrimaryWeapon for " + pd.getName());
							loadout.setPrimaryWeapon(i);
							break;
						}
					}
				}
				
				if(loadout.getShield().equals("none"))
				{
					for(String i : pd.getOwned())
					{
						if(obtainableBank.getObtainableFilter().isShield(obtainableBank.resolve(i)))
						{
							s("Found default Shield for " + pd.getName());
							loadout.setShield(i);
							break;
						}
					}
				}
				
				if(loadout.getSecondaryWeapon().equals("none"))
				{
					for(String i : pd.getOwned())
					{
						if(obtainableBank.getObtainableFilter().isSecondaryWeapon(obtainableBank.resolve(i)))
						{
							s("Found default SecondaryWeapon for " + pd.getName());
							loadout.setSecondaryWeapon(i);
							break;
						}
					}
				}
				
				if(loadout.getTertiaryWeapon().equals("none"))
				{
					for(String i : pd.getOwned())
					{
						if(obtainableBank.getObtainableFilter().isTertiaryWeapon(obtainableBank.resolve(i)))
						{
							s("Found default TertiaryWeapon for " + pd.getName());
							loadout.setTertiaryWeapon(i);
							break;
						}
					}
				}
				
				if(loadout.getProjectile().equals("none"))
				{
					for(String i : pd.getOwned())
					{
						if(obtainableBank.getObtainableFilter().isProjectile(obtainableBank.resolve(i)))
						{
							s("Found default Projectile for " + pd.getName());
							loadout.setProjectile(i);
							break;
						}
					}
				}
				
				if(loadout.getAbility().equals("none"))
				{
					for(String i : pd.getOwned())
					{
						if(obtainableBank.getObtainableFilter().isAbility(obtainableBank.resolve(i)))
						{
							s("Found default Ability for " + pd.getName());
							loadout.setAbility(i);
							break;
						}
					}
				}
				
				new ResourceLoader(e.getPlayer(), GameController.this);
			}
		});
	}
	
	@EventHandler
	public void onDrop(PlayerPickupItemEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		e.setCancelled(true);
		
		if(getGame(e.getPlayer()) == null)
		{
			Location l = pl.target(e.getPlayer());
			Vector v = e.getPlayer().getLocation().getDirection().clone();
			
			if(!l.getBlock().getType().equals(Material.AIR))
			{
				e.getPlayer().teleport(l.add(0, 1.0, 0).setDirection(v));
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 4));
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 20));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLastJoin(PlayerJoinEvent e)
	{
		e.setJoinMessage(null);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		if(gpo(e.getPlayer()).getUsingPack())
		{
			e.setQuitMessage(ChatColor.GOLD + "- " + ChatColor.DARK_GRAY + e.getPlayer().getName());
			pl.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "- " + ChatColor.DARK_GRAY + e.getPlayer().getName());
		}
		
		else
		{
			e.setQuitMessage(null);
		}
		
		for(Game i : games)
		{
			if(i.getType().equals(GameType.REGIONED))
			{
				RegionedGame r = (RegionedGame) i;
				
				if(r.contains(e.getPlayer()))
				{
					r.leave(e.getPlayer());
				}
			}
		}
		
		getPlayerObjects().remove(e.getPlayer());
		
		pl.scheduleSyncTask(10, new Runnable()
		{
			@Override
			public void run()
			{
				if(pl.onlinePlayers().isEmpty())
				{
					for(Game i : games.copy())
					{
						if(i.getType().equals(GameType.REGIONED))
						{
							((RegionedGame) i).qqq();
						}
					}
				}
			}
		});
	}
	
	public GMap<Player, PlayerObject> getPlayerObjects()
	{
		return playerObjects;
	}
}
