package com.glacialrush.api.game;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.phantomapi.sync.TaskLater;
import org.phantomapi.util.C;
import com.glacialrush.Info;
import com.glacialrush.api.dispatch.InnerDispatcher;
import com.glacialrush.api.dispatch.notification.NotificationHandler;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.event.DeathEvent;
import com.glacialrush.api.game.event.GameJoinEvent;
import com.glacialrush.api.game.event.GameQuitEvent;
import com.glacialrush.api.game.experience.ExperienceBoost;
import com.glacialrush.api.game.handler.AbilityHandler;
import com.glacialrush.api.game.handler.BalanceHandler;
import com.glacialrush.api.game.handler.BountyHandler;
import com.glacialrush.api.game.handler.CombatHandler;
import com.glacialrush.api.game.handler.DropletHandler;
import com.glacialrush.api.game.handler.EffectHandler;
import com.glacialrush.api.game.handler.ExperienceHandler;
import com.glacialrush.api.game.handler.FactionHandler;
import com.glacialrush.api.game.handler.GameStateHandler;
import com.glacialrush.api.game.handler.PaladinHandler;
import com.glacialrush.api.game.handler.PlayerHandler;
import com.glacialrush.api.game.handler.RuneHandler;
import com.glacialrush.api.game.handler.SecurityHandler;
import com.glacialrush.api.game.handler.SquadHandler;
import com.glacialrush.api.game.handler.ToolHandler;
import com.glacialrush.api.game.handler.WorldHandler;
import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.Influenced;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.game.object.VillageBuff;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.map.region.LinkedRegion;
import com.glacialrush.api.map.region.Scenery;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.map.region.Village;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.teleport.TeleportOperation;
import com.glacialrush.api.teleport.TeleportTick;
import com.glacialrush.api.vfx.ParticleEffect;

public class RegionedGame extends Game implements Influenced
{
	private static int idx = 0;
	
	protected Map map;
	protected boolean running = false;
	protected FactionHandler factionHandler;
	public NotificationHandler notificationHandler;
	protected GList<Player> rspx;
	protected GMap<UUID, Double> boosting;
	protected GameStateHandler gameStateHandler;
	protected AbilityHandler abilityHandler;
	protected ExperienceHandler experienceHandler;
	protected SquadHandler squadHandler;
	protected BalanceHandler balanceHandler;
	protected PaladinHandler paladinHandler;
	protected PlayerHandler playerHandler;
	protected ToolHandler toolHandler;
	protected CombatHandler combatHandler;
	protected SecurityHandler securityHandler;
	protected EffectHandler effectHandler;
	protected BountyHandler bountyHandler;
	protected WorldHandler worldHandler;
	protected RuneHandler runeHandler;
	protected DropletHandler dropletHandler;
	protected GList<Player> deploying;
	protected GMap<Player, Integer> tospectating;
	protected GMap<Player, Integer> outsiders;
	protected GList<Player> spectating;
	protected GMap<VillageBuff, Faction> buffs;
	protected InnerDispatcher d;
	protected Integer id;
	protected Integer seconds;
	protected GMap<Player, Integer> iiCooldown;
	protected Boolean hasJoined;
	protected GMap<Player, Integer> influenceMap;
	private Boolean won;
	
	public RegionedGame(GameController gameController, final Map map)
	{
		super(gameController);
		
		this.id = idx++;
		this.map = map;
		setType(GameType.REGIONED);
		running = false;
		rspx = new GList<Player>();
		this.d = new InnerDispatcher(pl, "  RegionedGame<" + map.getName() + ">");
		d.overbose("Game Instance Created");
		deploying = new GList<Player>();
		factionHandler = new FactionHandler(this);
		gameStateHandler = new GameStateHandler(this);
		notificationHandler = new NotificationHandler(this);
		boosting = new GMap<UUID, Double>();
		experienceHandler = new ExperienceHandler(this);
		squadHandler = new SquadHandler(this);
		paladinHandler = new PaladinHandler(this);
		balanceHandler = new BalanceHandler(this);
		abilityHandler = new AbilityHandler(this);
		toolHandler = new ToolHandler(this);
		playerHandler = new PlayerHandler(this);
		effectHandler = new EffectHandler(this);
		securityHandler = new SecurityHandler(this);
		runeHandler = new RuneHandler(this);
		outsiders = new GMap<Player, Integer>();
		worldHandler = new WorldHandler(this);
		combatHandler = new CombatHandler(this);
		bountyHandler = new BountyHandler(this);
		dropletHandler = new DropletHandler(this);
		buffs = new GMap<VillageBuff, Faction>();
		tospectating = new GMap<Player, Integer>();
		spectating = new GList<Player>();
		iiCooldown = new GMap<Player, Integer>();
		hasJoined = false;
		influenceMap = new GMap<Player, Integer>();
		seconds = 300 * 60;
		won = false;
		this.map.setGame(this);
	}
	
	public PlayerHandler getPlayerHandler()
	{
		return playerHandler;
	}
	
	public GMap<UUID, Double> getBoosting()
	{
		return boosting;
	}
	
	public PaladinHandler getPaladinHandler()
	{
		return paladinHandler;
	}
	
	public Integer getSeconds()
	{
		return seconds;
	}
	
	public GList<Player> getRspx()
	{
		return rspx;
	}
	
	public CombatHandler getCombatHandler()
	{
		return combatHandler;
	}
	
	public RuneHandler getRuneHandler()
	{
		return runeHandler;
	}
	
	public Boolean getHasJoined()
	{
		return hasJoined;
	}
	
	public SquadHandler getSquadHandler()
	{
		return squadHandler;
	}
	
	public void kill(Player i)
	{
		i.setHealth(i.getMaxHealth());
		i.teleport(getWarpGate(getFactionHandler().getFaction(i)).getSpawns().pickRandom());
		pl.callEvent(new DeathEvent(RegionedGame.this, i, i, (double) 2));
	}
	
	@Override
	public void start()
	{
		d.overbose("Starting Game");
		super.start();
		
		this.map.setGame(this);
		
		d.overbose("Accenting...");
		map.accentEvenley();
		
		d.overbose("Inserting Players...");
		for(Player i : players())
		{
			factionHandler.insert(i);
		}
		
		d.overbose("Deploying Players");
		for(Territory i : map.getWarpgates())
		{
			for(Player j : factionHandler.getPlayers(i.getFaction()))
			{
				deploy(j, i);
			}
		}
		
		running = true;
		final int[] tx = new int[] {0};
		tx[0] = pl.scheduleSyncRepeatingTask(0, 20, new Runnable()
		{
			@Override
			public void run()
			{
				if(players().isEmpty())
				{
					return;
				}
				
				for(Player i : new GList<Player>(iiCooldown.keySet()))
				{
					iiCooldown.put(i, iiCooldown.get(i) - 1);
					
					if(iiCooldown.get(i) < 1)
					{
						iiCooldown.remove(i);
					}
				}
				
				seconds--;
				
				if(seconds <= 0)
				{
					int el = -2;
					Faction f = null;
					
					for(Faction i : Faction.all())
					{
						if(map.factionTerritoryCount(i) > el)
						{
							el = map.factionTerritoryCount(i);
							f = i;
						}
					}
					
					if(f == null)
					{
						f = Faction.random();
					}
					
					for(Player i : players())
					{
						i.sendMessage(f.getColor() + f.getName() + " has the most regions! Game Ended.");
					}
					
					pl.cancelTask(tx[0]);
					win(f);
					
					return;
				}
			}
		});
	}
	
	@Override
	public void stop()
	{
		super.stop();
		d.overbose("Stopping Game");
		GMap<Player, Integer> inf = popInfluenceMap();
		
		for(Player i : players().copy())
		{
			if(boosting.containsKey(i.getUniqueId()))
			{
				gameController.gpd(i).getExperienceBoostMap().boost(gameController.gpd(i), ExperienceBoost.BOOST, 0.0);
				i.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "BOOST" + ChatColor.DARK_GRAY + "]: " + ChatColor.GREEN + "Boost expired. (End of game)");
			}
			
			boosting.clear();
			
			leave(i);
		}
		
		d.overbose("Resetting Factions");
		factionHandler.clear();
		d.overbose("Resetting Map");
		map.neutralize();
		running = false;
		map.setGame(gameController.getBuildGame());
		
		gameController.destroy(RegionedGame.this);
		
		inf.clear();
		
		try
		{
			Bukkit.getPluginManager().getPlugin("GlacialServer").getClass().getMethod("reset").invoke(Bukkit.getPluginManager().getPlugin("GlacialServer"));
		}
		
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
		}
		
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		
		catch(SecurityException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean equals(Object o)
	{
		if(o == null || (!(o instanceof RegionedGame)))
		{
			return false;
		}
		
		RegionedGame rg = (RegionedGame) o;
		
		if(this.uuid.equals(rg.uuid))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public void join(Player p)
	{
		if(gameController.gpo(p).getDisabled())
		{
			p.sendMessage(ChatColor.RED + "You must accept the resource pack to join a game.");
			
			return;
		}
		
		d.overbose(ChatColor.GREEN + "Joining " + p.getName());
		
		if(state.getPlayers().contains(p))
		{
			d.overbose(ChatColor.RED + "ALREADY IN GAME " + p.getName());
			notificationHandler.queue(p, NotificationPreset.GAME_ALREADY_PLAYING.format(null, null, null));
			
			return;
		}
		
		super.join(p);
		p.setGameMode(GameMode.SPECTATOR);
		p.teleport(gameController.pl().getServerDataComponent().getHub().clone().add((Math.random() - 0.5) * 30, 30, (Math.random() - 0.5) * 30));
		factionHandler.insert(p);
		p.setAllowFlight(false);
		hasJoined = true;
		p.getInventory().clear();
		pl.getUiController().moveShortcuts(p, true);
		
		if(boosting.containsKey(p.getUniqueId()))
		{
			gameController.gpd(p).getExperienceBoostMap().boost(gameController.gpd(p), ExperienceBoost.BOOST, boosting.get(p.getUniqueId()));
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "BOOST" + ChatColor.DARK_GRAY + "]: " + ChatColor.GREEN + "Boost re-activated for the rest of this game.");
		}
		
		if(Statistic.GAMES_LOST.get(p) <= 0)
		{
			Statistic.GAMES_WLR.set(p, 0);
		}
		
		else
		{
			Statistic.GAMES_WLR.set(p, 100.0 * (Statistic.GAMES_WON.get(p) / Statistic.GAMES_LOST.get(p)));
		}
		
		if(Statistic.GAMES_PLAYED.get(p) <= 0)
		{
			Statistic.GAMES_WINRATE.set(p, 0);
		}
		
		else
		{
			Statistic.GAMES_WINRATE.set(p, 100.0 * (Statistic.GAMES_WON.get(p) / Statistic.GAMES_PLAYED.get(p)));
		}
		
		deploy(p);
		playerHandler.reset(p);
		pl.callEvent(new GameJoinEvent(this, p));
	}
	
	@Override
	public void leave(Player p)
	{
		d.overbose(ChatColor.RED + "Quitting " + p.getName());
		
		if(state.getPlayers().contains(p))
		{
			for(Player i : players())
			{
				if(p.equals(i))
				{
					continue;
				}
				
				notificationHandler.queue(i, NotificationPreset.QUIT.format(null, null, new String[] {factionHandler.getFaction(p).getColor() + p.getName()}));
			}
			
			gameController.gpo(p).clear();
			notificationHandler.queue(p, NotificationPreset.GAME_QUIT.getNotification());
			p.getInventory().clear();
			pl.getUiController().moveShortcuts(p, false);
			state.getPlayers().remove(p);
			factionHandler.remove(p);
			tospectating.remove(p);
			spectating.remove(p);
			p.setGameMode(GameMode.ADVENTURE);
			
			Location k = pl.getServerDataComponent().getHub();
			
			if(k != null)
			{
				p.teleport(pl.getServerDataComponent().getHub());
			}
			
			notificationHandler.resetOngoing(p);
			
			if(boosting.containsKey(p.getUniqueId()))
			{
				gameController.gpd(p).getExperienceBoostMap().boost(gameController.gpd(p), ExperienceBoost.BOOST, 0.0);
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "BOOST" + ChatColor.DARK_GRAY + "]: " + ChatColor.GREEN + "You can still use your boost in this game if you rejoin.");
			}
			
			gameController.gpo(p).menable();
		}
		
		playerHandler.reset(p);
		pl.callEvent(new GameQuitEvent(this, p));
	}
	
	public void secureBoost(Player p, double a)
	{
		boosting.put(p.getUniqueId(), a);
		gameController.gpd(p).getExperienceBoostMap().boost(gameController.gpd(p), ExperienceBoost.BOOST, a);
	}
	
	public boolean canBoost(Player p)
	{
		return !boosting.containsKey(p.getUniqueId());
	}
	
	public boolean isEliminated(Faction f)
	{
		for(Territory i : map.getWarpgates())
		{
			if(i.getFaction().equals(f))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public GameStateHandler getGameStateHandler()
	{
		return gameStateHandler;
	}
	
	public GList<Player> getDeploying()
	{
		return deploying;
	}
	
	public static int getIdx()
	{
		return idx;
	}
	
	public WorldHandler getWorldHandler()
	{
		return worldHandler;
	}
	
	public Integer getId()
	{
		return id;
	}
	
	public Region getRegion(Player p)
	{
		for(Region i : map.getRegions())
		{
			if(i.contains(p))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void win(Faction w)
	{
		if(won)
		{
			return;
		}
		
		won = true;
		
		for(Player i : players())
		{
			notificationHandler.queue(i, NotificationPreset.FACTION_VICTORIOUS.format(new Object[] {w.getColor() + ""}, new Object[] {w.getColor() + "" + w.getName()}, null));
		}
		
		new TaskLater(78)
		{
			@Override
			public void run()
			{
				for(Player i : players())
				{
					if(factionHandler.getFaction(i).equals(w))
					{
						Statistic.GAMES_WON.add(i);
					}
					
					else
					{
						Statistic.GAMES_LOST.add(i);
					}
					
					Statistic.GAMES_PLAYED.add(i);
					
					if(Statistic.GAMES_LOST.get(i) <= 0)
					{
						Statistic.GAMES_WLR.set(i, 0);
					}
					
					else
					{
						Statistic.GAMES_WLR.set(i, 100.0 * (Statistic.GAMES_WON.get(i) / Statistic.GAMES_LOST.get(i)));
					}
					
					if(Statistic.GAMES_PLAYED.get(i) <= 0)
					{
						Statistic.GAMES_WINRATE.set(i, 0);
					}
					
					else
					{
						Statistic.GAMES_WINRATE.set(i, 100.0 * (Statistic.GAMES_WON.get(i) / Statistic.GAMES_PLAYED.get(i)));
					}
				}
				
				pl.scheduleSyncTask(100, new Runnable()
				{
					@Override
					public void run()
					{
						qqq();
					}
				});
			}
		};
	}
	
	public void qqq()
	{
		pl.scheduleSyncTask(0, new Runnable()
		{
			@Override
			public void run()
			{
				stop();
			}
		});
	}
	
	public void instantAction(Player p)
	{
		if(iiCooldown.containsKey(p))
		{
			notificationHandler.queue(p, NotificationPreset.NO_ACTION_COOLDOWN.format(null, null, new Object[] {iiCooldown.get(p) + " seconds"}));
			return;
		}
		
		LinkedRegion l = action(p);
		
		if(l == null)
		{
			notificationHandler.queue(p, NotificationPreset.NO_ACTION.format(null, null, null));
			return;
		}
		
		if(deploying.contains(p))
		{
			return;
		}
		
		if(isSpectating(p) || tospectating.containsKey(p))
		{
			return;
		}
		
		iiCooldown.put(p, 60);
		
		iiresp(p, l);
	}
	
	public LinkedRegion action(Player p)
	{
		Faction f = factionHandler.getFaction(p);
		LinkedRegion r = action();
		
		if(r == null)
		{
			return null;
		}
		
		if(r.getFaction().equals(f))
		{
			return r;
		}
		
		else
		{
			return map.getCloseLinkedRegion(r.centerSpawn(), f);
		}
	}
	
	public LinkedRegion action()
	{
		int mp = -1;
		LinkedRegion lr = null;
		
		for(LinkedRegion i : map.getLinkedRegions())
		{
			if(i.getPlayers().size() > mp)
			{
				mp = i.getPlayers().size();
				lr = i;
			}
		}
		
		// if(mp < 2)
		// {
		// return null;
		// }TODO
		
		return lr;
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public void setFactionHandler(FactionHandler factionHandler)
	{
		this.factionHandler = factionHandler;
	}
	
	public void setNotificationHandler(NotificationHandler notificationHandler)
	{
		this.notificationHandler = notificationHandler;
	}
	
	public void setGameStateHandler(GameStateHandler gameStateHandler)
	{
		this.gameStateHandler = gameStateHandler;
	}
	
	public void setDeploying(GList<Player> deploying)
	{
		this.deploying = deploying;
	}
	
	public void setD(InnerDispatcher d)
	{
		this.d = d;
	}
	
	public EffectHandler getEffectHandler()
	{
		return effectHandler;
	}
	
	public ToolHandler getToolHandler()
	{
		return toolHandler;
	}
	
	public ExperienceHandler getExperienceHandler()
	{
		return experienceHandler;
	}
	
	public boolean isSpectating(Player p)
	{
		return tospectating.contains(p) || spectating.contains(p);
	}
	
	public void spectate(Player p)
	{
		if(!isSpectating(p))
		{
			tospectating.put(p, 10);
		}
	}
	
	public void unSpectate(final Player p)
	{
		if(isSpectating(p))
		{
			tospectating.remove(p);
			spectating.remove(p);
			getNotificationHandler().resetOngoing(p);
			
			TeleportOperation tp = new TeleportOperation(p, 200, map.getCloseLinkedRegion(p).getSpawns().pickRandom());
			tp.cancelOnCombat();
			tp.onTeleportTick(new TeleportTick()
			{
				public void run()
				{
					getNotificationHandler().queue(p, NotificationPreset.RESPAWN_COOLDOWN.format(null, null, new Object[] {getTicks() / 20}));
				}
			});
			
			tp.onTeleported(new Runnable()
			{
				@Override
				public void run()
				{
					p.setGameMode(GameMode.ADVENTURE);
					gameController.gpo(p).buildItems();
					getNotificationHandler().resetOngoing(p);
				}
			});
			
			tp.tickAudio(Audio.UI_ACTION);
			tp.commit();
		}
	}
	
	public AbilityHandler getAbilityHandler()
	{
		return abilityHandler;
	}
	
	public GMap<Player, Integer> getTospectating()
	{
		return tospectating;
	}
	
	public GMap<Player, Integer> getOutsiders()
	{
		return outsiders;
	}
	
	public GList<Player> getSpectating()
	{
		return spectating;
	}
	
	public GMap<VillageBuff, Faction> getBuffs()
	{
		return buffs;
	}
	
	public void updateBoard(Player p)
	{
		boardController.remove(p);
		
		Region rx = getRegion(p);
		
		if(rx != null)
		{
			if(rx.getType().equals(RegionType.TERRITORY))
			{
				Territory r = (Territory) rx;
				boardController.put(p, ChatColor.GREEN + "T_", r.getFaction().getColor().toString() + C.BOLD + C.UNDERLINE + r.getName());
				int c = 0;
				String caps = "";
				
				for(Capture i : r.getCaptures())
				{
					ChatColor cx = i.getFaction().getColor();
					Boolean bol = true;
					
					if(i.getProgress() != 0 && i.getProgress() != 100)
					{
						bol = false;
						cx = new GList<ChatColor>().qadd(i.getOffense().getColor()).qadd(i.getFaction().getColor()).pickRandom();
					}
					
					caps = caps + cx + "[" + (bol ? "" : C.UNDERLINE) + Info.ALPHABET[c] + C.RESET + cx + "] ";
					
					c++;
				}
				
				boardController.put(p, ChatColor.AQUA + "Control_", caps);
				
				if(getGameStateHandler().getMapTimer().hasTimer(r))
				{
					boardController.put(p, ChatColor.RED + "Capture in", ChatColor.YELLOW + "" + C.BOLD + getGameStateHandler().getMapTimer().getTime(r));
				}
			}
			
			if(rx.getType().equals(RegionType.VILLAGE))
			{
				Village r = (Village) rx;
				boardController.put(p, ChatColor.GREEN + "V_", r.getFaction().getColor() + r.getName());
				int c = 0;
				
				for(Region i : r.getBorders())
				{
					if(i.getType().equals(RegionType.TERRITORY))
					{
						Territory t = (Territory) i;
						
						boardController.put(p, ChatColor.AQUA + "  " + Info.ALPHABET[c], t.getFaction().getColor() + t.getName());
						c++;
					}
				}
			}
			
			if(rx.getType().equals(RegionType.SCENERY))
			{
				Scenery r = (Scenery) rx;
				boardController.put(p, ChatColor.WHITE + "Nature_", ChatColor.AQUA + r.getName());
			}
			
			if(rx.getType().equals(RegionType.EDGE))
			{
				boardController.put(p, ChatColor.RED + "EDGE_", ChatColor.RED + "LEAVE THIS AREA");
			}
			
			if(!gameStateHandler.getMapTimer().getWarpgatePercentages().isEmpty())
			{
				int m = 0;
				
				for(String i : gameStateHandler.getMapTimer().getWarpgatePercentages())
				{
					boardController.put(p, ChatColor.GREEN + "W [" + Info.ALPHABET[m] + "]", i);
					m++;
				}
			}
		}
		
		else
		{
			boardController.put(p, ChatColor.RED + "" + C.BOLD + C.UNDERLINE + "DEPLOYING", (StringUtils.repeat(".", (int) (Math.random() * 6))));
		}
		
		boardController.update(p);
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public FactionHandler getFactionHandler()
	{
		return factionHandler;
	}
	
	public NotificationHandler getNotificationHandler()
	{
		return notificationHandler;
	}
	
	public InnerDispatcher getD()
	{
		return d;
	}
	
	public void deploy(Player p)
	{
		d.overbose(ChatColor.LIGHT_PURPLE + "Deploying " + p.getName());
		deploy(p, getWarpGate(factionHandler.getFaction(p)));
	}
	
	public void stopDeploying(Player p)
	{
		deploying.remove(p);
	}
	
	public void deploy(final Player p, LinkedRegion lr)
	{
		if(!deploying.contains(p))
		{
			deploying.add(p);
		}
		
		else
		{
			return;
		}
		
		gameController.gpo(p).mdisable();
		final TeleportOperation tptp = new TeleportOperation(p, 140, lr.getSpawns().pickRandom());
		tptp.tickAudio(Audio.UI_CLICK).onTeleportTick(new TeleportTick()
		{
			@Override
			public void run()
			{
				if(!deploying.contains(p))
				{
					notificationHandler.resetOngoing(p);
					tptp.destroy();
					tptp.cancel();
					d.overbose(ChatColor.RED + "DESTROYING TELEPORT TIMER");
					return;
				}
				
				ParticleEffect.CLOUD.display(1f, 1f, 1f, 0.1f, 100, p.getLocation().clone().add(0, 6, 0), 10);
				Location k = gameController.pl().getServerDataComponent().getHub().clone().add(0, 30, 0);
				k.setYaw(0f);
				k.setPitch(0f);
				p.setVelocity(new Vector(0, 0, 0));
				p.teleport(k);
				notificationHandler.queue(p, NotificationPreset.DEPLOY_COOLDOWN.format(null, new Object[] {map.getName()}, new Object[] {String.valueOf(getTicks() / 20)}));
			}
		}).onTeleportCancelled(new Runnable()
		{
			@Override
			public void run()
			{
				if(!deploying.contains(p))
				{
					notificationHandler.resetOngoing(p);
					return;
				}
				
				deploying.remove(p);
				p.setGameMode(GameMode.ADVENTURE);
				gameController.gpo(p).clearPotionEffects();
				
				tptp.destroy();
				
				notificationHandler.resetOngoing(p);
				notificationHandler.queue(p, NotificationPreset.TELEPORT_CANCELLED_COMBAT.format(null, null, null));
			}
		}).onTeleported(new Runnable()
		{
			@Override
			public void run()
			{
				if(!deploying.contains(p))
				{
					notificationHandler.resetOngoing(p);
					return;
				}
				
				deploying.remove(p);
				notificationHandler.resetOngoing(p);
				
				Game g = gameController.getGame(p);
				
				if(g != null && g.getType().equals(GameType.REGIONED))
				{
					((RegionedGame) g).resetAbility(p);
				}
				
				p.setGameMode(GameMode.ADVENTURE);
				
				try
				{
					gameController.gpo(p).clearPotionEffects();
				}
				
				catch(Exception e)
				{
					d.failure("Failed. to handle timer. Ignoring...");
				}
				
				notificationHandler.queue(p, NotificationPreset.GAME_YOU_FIGHT_WITH.format(null, new Object[] {factionHandler.getFaction(p).getColor() + " ", factionHandler.getFaction(p).getName()}, null));
				gameController.getPlayerObjects().get(p).buildItems();
				
				for(Player i : players())
				{
					if(p.equals(i))
					{
						continue;
					}
					
					notificationHandler.queue(i, NotificationPreset.JOIN.format(null, null, new Object[] {factionHandler.getFaction(p).getColor() + p.getName(), factionHandler.getFaction(p).getColor() + " " + factionHandler.getFaction(p).getName()}));
				}
				
				gameController.gpo(p).menable();
			}
		});
		tptp.commit();
	}
	
	public void iiresp(final Player p, LinkedRegion r)
	{
		if(r == null)
		{
			return;
		}
		
		if(rspx.contains(p))
		{
			return;
		}
		
		rspx.add(p);
		
		new TeleportOperation(p, 240, r.getSpawns().pickRandom()).cancelOnCombat().tickAudio(Audio.UI_CLICK).onTeleportTick(new TeleportTick()
		{
			@Override
			public void run()
			{
				notificationHandler.queue(p, NotificationPreset.ACTION_TIMER.format(null, new Object[] {r.getFaction().getColor() + r.getName()}, new Object[] {String.valueOf(getTicks() / 20)}));
			}
		}).onTeleportCancelled(new Runnable()
		{
			@Override
			public void run()
			{
				notificationHandler.resetOngoing(p);
				rspx.remove(p);
				notificationHandler.queue(p, NotificationPreset.TELEPORT_CANCELLED_COMBAT.format(null, null, null));
			}
		}).onTeleported(new Runnable()
		{
			@Override
			public void run()
			{
				rspx.remove(p);
				p.setHealth(p.getMaxHealth());
				
				Game g = gameController.getGame(p);
				
				if(g != null && g.getType().equals(GameType.REGIONED))
				{
					((RegionedGame) g).resetAbility(p);
				}
				
				notificationHandler.resetOngoing(p);
				notificationHandler.queue(p, NotificationPreset.TELEPORT_COMPLETED.format(null, null, null));
				gameController.getPlayerObjects().get(p).buildItems();
			}
		}).cancelOnCombat().commit();
	}
	
	public void namedResp(final Player p, LinkedRegion r, NotificationPreset nn)
	{
		if(r == null)
		{
			return;
		}
		
		if(rspx.contains(p))
		{
			return;
		}
		
		rspx.add(p);
		
		new TeleportOperation(p, 240, r.getSpawns().pickRandom()).cancelOnCombat().tickAudio(Audio.UI_CLICK).onTeleportTick(new TeleportTick()
		{
			@Override
			public void run()
			{
				notificationHandler.queue(p, nn.format(null, null, new Object[] {String.valueOf(getTicks() / 20)}));
			}
		}).onTeleportCancelled(new Runnable()
		{
			@Override
			public void run()
			{
				notificationHandler.resetOngoing(p);
				rspx.remove(p);
				notificationHandler.queue(p, NotificationPreset.TELEPORT_CANCELLED_COMBAT.format(null, null, null));
			}
		}).onTeleported(new Runnable()
		{
			@Override
			public void run()
			{
				rspx.remove(p);
				p.setHealth(p.getMaxHealth());
				
				Game g = gameController.getGame(p);
				
				if(g != null && g.getType().equals(GameType.REGIONED))
				{
					((RegionedGame) g).resetAbility(p);
				}
				
				notificationHandler.resetOngoing(p);
				notificationHandler.queue(p, NotificationPreset.TELEPORT_COMPLETED.format(null, null, null));
				gameController.getPlayerObjects().get(p).buildItems();
			}
		}).cancelOnCombat().commit();
	}
	
	public SecurityHandler getSecurityHandler()
	{
		return securityHandler;
	}
	
	public BountyHandler getBountyHandler()
	{
		return bountyHandler;
	}
	
	public GMap<Player, Integer> getIiCooldown()
	{
		return iiCooldown;
	}
	
	public void resp(final Player p, LinkedRegion r)
	{
		namedResp(p, r, NotificationPreset.RESPAWN_COOLDOWN);
	}
	
	public void resp(final Player p)
	{
		resp(p, getWarpGate(getFactionHandler().getFaction(p)));
	}
	
	public void resetAbility(Player p)
	{
		getAbilityHandler().reset(p);
	}
	
	public void respawn(final Player p)
	{
		d.overbose(factionHandler.getFaction(p).getName() + " >> " + getWarpGate(factionHandler.getFaction(p)).getName());
		
		new TeleportOperation(p, 200, getWarpGate(factionHandler.getFaction(p)).getSpawns().pickRandom()).cancelOnCombat().tickAudio(Audio.UI_CLICK).onTeleportTick(new TeleportTick()
		{
			@Override
			public void run()
			{
				notificationHandler.queue(p, NotificationPreset.RESPAWN_COOLDOWN.format(null, null, new Object[] {String.valueOf(getTicks() / 20)}));
			}
		}).onTeleportCancelled(new Runnable()
		{
			@Override
			public void run()
			{
				notificationHandler.resetOngoing(p);
				notificationHandler.queue(p, NotificationPreset.TELEPORT_CANCELLED_COMBAT.format(null, null, null));
			}
		}).onTeleported(new Runnable()
		{
			@Override
			public void run()
			{
				notificationHandler.resetOngoing(p);
				
				Game g = gameController.getGame(p);
				
				if(g != null && g.getType().equals(GameType.REGIONED))
				{
					((RegionedGame) g).resetAbility(p);
				}
				
				notificationHandler.queue(p, NotificationPreset.TELEPORT_COMPLETED.format(null, null, null));
				gameController.getPlayerObjects().get(p).buildItems();
			}
		}).commit();
	}
	
	public BalanceHandler getBalanceHandler()
	{
		return balanceHandler;
	}
	
	public void blockHit(Block block)
	{
		worldHandler.destroy(block.getLocation());
		worldHandler.destroy(block.getLocation().getBlock().getRelative(BlockFace.UP).getLocation());
		worldHandler.destroy(block.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation());
		worldHandler.destroy(block.getLocation().getBlock().getRelative(BlockFace.EAST).getLocation());
		worldHandler.destroy(block.getLocation().getBlock().getRelative(BlockFace.WEST).getLocation());
		worldHandler.destroy(block.getLocation().getBlock().getRelative(BlockFace.SOUTH).getLocation());
		worldHandler.destroy(block.getLocation().getBlock().getRelative(BlockFace.NORTH).getLocation());
	}
	
	public Territory getWarpGate(Faction f)
	{
		for(Territory i : map.getWarpgates())
		{
			if(i.getFaction().equals(f))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public Map getMap()
	{
		return map;
	}
	
	public void setMap(Map map)
	{
		this.map = map;
	}
	
	@Override
	public GMap<Player, Integer> getInfluenceMap()
	{
		return influenceMap;
	}
	
	@Override
	public GMap<Player, Integer> popInfluenceMap()
	{
		for(Territory i : map.getTerritories())
		{
			for(Capture j : i.getCaptures())
			{
				injectInfluence(j.popInfluenceMap());
				j.resetInfluenceMap();
			}
			
			injectInfluence(i.popInfluenceMap());
			i.resetInfluenceMap();
		}
		
		for(Village i : map.getVillages())
		{
			injectInfluence(i.popInfluenceMap());
			i.resetInfluenceMap();
		}
		
		injectInfluence(map.getInfluence());
		map.resetInfluenceMap();
		
		GMap<Player, Integer> in = influenceMap.copy();
		resetInfluenceMap();
		return in;
	}
	
	@Override
	public double getInfluence(Player p)
	{
		double inf = 1l;
		
		for(Territory i : map.getTerritories())
		{
			for(Capture j : i.getCaptures())
			{
				inf += j.getInfluence(p);
			}
			
			inf += i.getInfluence(p);
		}
		
		for(Village i : map.getVillages())
		{
			inf += i.getInfluence(p);
		}
		
		inf += map.getInfluence(p);
		
		if(influenceMap.containsKey(p))
		{
			inf += influenceMap.get(p);
		}
		
		return inf;
	}
	
	@Override
	public void resetInfluenceMap()
	{
		influenceMap.clear();
	}
	
	@Override
	public void injectInfluence(GMap<Player, Integer> influence)
	{
		for(Player i : influence.keySet())
		{
			influence(i, influence.get(i));
		}
	}
	
	@Override
	public void influence(Player p, Integer i)
	{
		if(!influenceMap.containsKey(p))
		{
			influenceMap.put(p, i);
		}
		
		else
		{
			influenceMap.put(p, influenceMap.get(p) + i);
		}
	}
	
	public DropletHandler getDropletHandler()
	{
		return dropletHandler;
	}
	
	public void setDropletHandler(DropletHandler dropletHandler)
	{
		this.dropletHandler = dropletHandler;
	}
	
	public static void setIdx(int idx)
	{
		RegionedGame.idx = idx;
	}
	
	public void setRspx(GList<Player> rspx)
	{
		this.rspx = rspx;
	}
	
	public void setBoosting(GMap<UUID, Double> boosting)
	{
		this.boosting = boosting;
	}
	
	public void setAbilityHandler(AbilityHandler abilityHandler)
	{
		this.abilityHandler = abilityHandler;
	}
	
	public void setExperienceHandler(ExperienceHandler experienceHandler)
	{
		this.experienceHandler = experienceHandler;
	}
	
	public void setSquadHandler(SquadHandler squadHandler)
	{
		this.squadHandler = squadHandler;
	}
	
	public void setBalanceHandler(BalanceHandler balanceHandler)
	{
		this.balanceHandler = balanceHandler;
	}
	
	public void setPaladinHandler(PaladinHandler paladinHandler)
	{
		this.paladinHandler = paladinHandler;
	}
	
	public void setPlayerHandler(PlayerHandler playerHandler)
	{
		this.playerHandler = playerHandler;
	}
	
	public void setToolHandler(ToolHandler toolHandler)
	{
		this.toolHandler = toolHandler;
	}
	
	public void setCombatHandler(CombatHandler combatHandler)
	{
		this.combatHandler = combatHandler;
	}
	
	public void setSecurityHandler(SecurityHandler securityHandler)
	{
		this.securityHandler = securityHandler;
	}
	
	public void setEffectHandler(EffectHandler effectHandler)
	{
		this.effectHandler = effectHandler;
	}
	
	public void setBountyHandler(BountyHandler bountyHandler)
	{
		this.bountyHandler = bountyHandler;
	}
	
	public void setWorldHandler(WorldHandler worldHandler)
	{
		this.worldHandler = worldHandler;
	}
	
	public void setRuneHandler(RuneHandler runeHandler)
	{
		this.runeHandler = runeHandler;
	}
	
	public void setTospectating(GMap<Player, Integer> tospectating)
	{
		this.tospectating = tospectating;
	}
	
	public void setOutsiders(GMap<Player, Integer> outsiders)
	{
		this.outsiders = outsiders;
	}
	
	public void setSpectating(GList<Player> spectating)
	{
		this.spectating = spectating;
	}
	
	public void setBuffs(GMap<VillageBuff, Faction> buffs)
	{
		this.buffs = buffs;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	public void setSeconds(Integer seconds)
	{
		this.seconds = seconds;
	}
	
	public void setIiCooldown(GMap<Player, Integer> iiCooldown)
	{
		this.iiCooldown = iiCooldown;
	}
	
	public void setHasJoined(Boolean hasJoined)
	{
		this.hasJoined = hasJoined;
	}
	
	public void setInfluenceMap(GMap<Player, Integer> influenceMap)
	{
		this.influenceMap = influenceMap;
	}
}
