package com.glacialrush.api.game.handler;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.CombatEvent;
import com.glacialrush.api.game.event.DeathEvent;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.PlayerObject;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.game.obtainable.Obtainable;
import com.glacialrush.api.game.obtainable.item.AbilityEffect;
import com.glacialrush.api.game.timer.CaptureTimer;
import com.glacialrush.api.game.timer.EdgeTimer;
import com.glacialrush.api.game.timer.GameTimer;
import com.glacialrush.api.game.timer.MapTimer;
import com.glacialrush.api.game.timer.ObjectiveTimer;
import com.glacialrush.api.game.timer.PlayerTimer;
import com.glacialrush.api.game.timer.Timer;
import com.glacialrush.api.map.region.LinkedRegion;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.teleport.TeleportOperation;
import com.glacialrush.api.teleport.TeleportTick;

public class GameStateHandler extends GlacialHandler implements Listener
{
	private RegionedGame rg;
	private GList<Timer> gameTimers;
	private GMap<Timer, Long> timeLeft;
	private CaptureTimer captureTimer;
	private MapTimer mapTimer;
	private PlayerTimer playerTimer;
	private ObjectiveTimer objectiveTimer;
	private EdgeTimer edgeTimer;
	private GMap<String, Long> tmx;
	private GMap<Player, LinkedRegion> dead;
	private GMap<Player, Player> lastKiller;
	private GMap<Player, GMap<Player, Integer>> assists;
	private GMap<Player, Integer> kills;
	private GList<Player> died;
	
	public GameStateHandler(Game game)
	{
		super(game);
		
		this.gameTimers = new GList<Timer>();
		this.timeLeft = new GMap<Timer, Long>();
		this.dead = new GMap<Player, LinkedRegion>();
		this.assists = new GMap<Player, GMap<Player, Integer>>();
		this.kills = new GMap<Player, Integer>();
		this.tmx = new GMap<String, Long>();
		this.lastKiller = new GMap<Player, Player>();
		this.died = new GList<Player>();
	}
	
	public GMap<String, Long> getTmx()
	{
		return tmx;
	}
	
	public GMap<Player, LinkedRegion> getDead()
	{
		return dead;
	}
	
	public GMap<Player, GMap<Player, Integer>> getAssists()
	{
		return assists;
	}
	
	public GMap<Player, Integer> getKills()
	{
		return kills;
	}
	
	public void registerGameTimer(GameTimer timer)
	{
		gameTimers.add(timer);
	}
	
	public void start(GameState state)
	{
		if(game.getType().equals(GameType.REGIONED))
		{
			game.pl().register(this);
			rg = (RegionedGame) game;
			
			captureTimer = new CaptureTimer((RegionedGame) game);
			mapTimer = new MapTimer((RegionedGame) game);
			objectiveTimer = new ObjectiveTimer((RegionedGame) game);
			playerTimer = new PlayerTimer((RegionedGame) game);
			edgeTimer = new EdgeTimer((RegionedGame) game);
		}
	}
	
	public void calculateDeathXp(Player p, Player k, GMap<Player, Integer> map)
	{
		int power = 0;
		int xp = 100;
		int axp = 50;
		
		if(game.getGameController().getGame(p) == null || !game.getGameController().getGame(p).getType().equals(GameType.REGIONED))
		{
			return;
		}
		
		if(!kills.containsKey(k))
		{
			kills.put(k, 0);
		}
		
		power = kills.get(p) != null ? kills.get(p) : 1;
		xp += (power * 6);
		axp += (power * 3);
		kills.remove(p);
		
		if(!p.equals(k))
		{
			kills.put(k, kills.get(k) + 1);
			Statistic.COMBAT_KILLS.add(k);
			Statistic.COMBAT_DEATHS.add(p);
			
			if(Statistic.COMBAT_DEATHS.get(k) <= 0)
			{
				Statistic.COMBAT_KDR.set(k, 0);
			}
			
			else
			{
				Statistic.COMBAT_KDR.set(k, Statistic.COMBAT_KILLS.get(k) / Statistic.COMBAT_DEATHS.get(k));
			}
			
			Statistic.COMBAT_KDR.set(p, Statistic.COMBAT_KILLS.get(p) / Statistic.COMBAT_DEATHS.get(p));
			
			game.pl().getMarketController().chanceShard(k);
			
			((RegionedGame) game.getGameController().getGame(p)).getExperienceHandler().giveXp(k, (long) xp, Experience.KILL);
			
			if(power > 5)
			{
				((RegionedGame) game.getGameController().getGame(p)).getExperienceHandler().giveXp(k, (long) power * 4, Experience.KILL_BONUS);
			}
			
			if(power > 5)
			{
				for(Player i : ((RegionedGame) game).players())
				{
					((RegionedGame) game).getNotificationHandler().queue(i, NotificationPreset.COMBAT_RELENTLESS.format(null, null, new Object[] {((RegionedGame) game).getFactionHandler().getFaction(p).getColor() + p.getName() + "" + ChatColor.BLUE, ChatColor.RED + "" + power}));
				}
			}
		}
		
		else
		{
			Statistic.COMBAT_DEATHS.add(p);
			
			Statistic.COMBAT_KDR.set(p, Statistic.COMBAT_KILLS.get(p) / Statistic.COMBAT_DEATHS.get(p));
			
		}
		
		if(assists.containsKey(p))
		{
			assists.get(p).remove(k);
			
			for(Player i : assists.get(p).keySet())
			{
				if(i.equals(k))
				{
					continue;
				}
				
				Statistic.COMBAT_KILLASSISTS.add(i);
				game.pl().getMarketController().chanceShard(i);
				
				((RegionedGame) game.getGameController().getGame(p)).getExperienceHandler().giveXp(i, (long) axp, Experience.KILL_ASSIST);
				
				if(power > 5)
				{
					((RegionedGame) game.getGameController().getGame(p)).getExperienceHandler().giveXp(i, (long) power * 2, Experience.KILL_ASSIST_BONUS);
				}
			}
			
			assists.remove(p);
		}
	}
	
	public EdgeTimer getEdgeTimer()
	{
		return edgeTimer;
	}
	
	public GMap<Player, Player> getLastKiller()
	{
		return lastKiller;
	}
	
	public PlayerTimer getPlayerTimer()
	{
		return playerTimer;
	}
	
	public void stop(GameState state)
	{
		if(game.getType().equals(GameType.REGIONED))
		{
			game.pl().unRegister(this);
		}
		
		gameTimers.clear();
	}
	
	public void tick(GameState state)
	{
		for(Player i : state.getPlayers())
		{
			if(i.getLocation().getY() < 0)
			{
				if(state.getGame().getType().equals(GameType.REGIONED))
				{
					i.teleport(((RegionedGame) state.getGame()).getWarpGate(((RegionedGame) state.getGame()).getFactionHandler().getFaction(i)).getSpawns().pickRandom());
				}
				
				else
				{
					i.teleport(state.getGame().pl().getServerDataComponent().getHub());
					i.setHealth(i.getMaxHealth());
				}
			}
		}
		
		try
		{
			for(Player i : gameController.pl().onlinePlayers())
			{
				if(gameController.getGame(i) == null && game.getState().getMap().contains(i))
				{
					i.teleport(state.getGame().pl().getServerDataComponent().getHub());
					i.setGameMode(GameMode.ADVENTURE);
					i.setHealth(20);
					i.setFoodLevel(20);
					gameController.pl().getUiController().moveShortcuts(i, false);
				}
			}
		}
		
		catch(Exception e)
		{
			
		}
		
		if(rg == null)
		{
			return;
		}
		
		for(Timer i : gameTimers)
		{
			if(timeLeft.containsKey(i))
			{
				timeLeft.put(i, timeLeft.get(i) - 1);
				
				if(timeLeft.get(i) < 0)
				{
					timeLeft.put(i, i.tickreval());
					long mms = System.currentTimeMillis();
					i.tick();
					tmx.put(i.getClass().getSimpleName(), System.currentTimeMillis() - mms);
				}
			}
			
			else
			{
				timeLeft.put(i, i.tickreval());
			}
		}
	}
	
	public boolean isDead(Player p)
	{
		return dead.containsKey(p);
	}
	
	public Player getKiller(Player p)
	{
		if(lastKiller.containsKey(p))
		{
			return lastKiller.get(p);
		}
		
		return null;
	}
	
	@EventHandler
	public void combatHandler(final DeathEvent e)
	{
		if(game.getGameController().getGame(e.getPlayer()) == null || !game.getGameController().getGame(e.getPlayer()).getType().equals(GameType.REGIONED))
		{
			e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
			return;
		}
		
		if(!game.players().contains(e.getPlayer()))
		{
			return;
		}
		
		LinkedRegion t = e.getGame().getMap().getCloseLinkedRegion(e.getPlayer());
		
		if(t == null)
		{
			game.leave(e.getPlayer());
			return;
		}
		
		if(died.contains(e.getPlayer()))
		{
			return;
		}
		
		else
		{
			died.add(e.getPlayer());
			
			game.pl().scheduleSyncTask(1, new Runnable()
			{
				@Override
				public void run()
				{
					died.remove(e.getPlayer());
				}
			});
		}
		
		if(game.getType().equals(GameType.REGIONED))
		{
			RegionedGame rg = (RegionedGame) game;
			String k = rg.getFactionHandler().getFaction(e.getDamager()).getColor() + e.getDamager().getName();
			String d = rg.getFactionHandler().getFaction(e.getPlayer()).getColor() + e.getPlayer().getName();
			Obtainable o = gameController.gpo(e.getDamager()).getCurrent();
			String w = ChatColor.RED + "Raw Power";
			
			rg.resetAbility(e.getPlayer());
			
			if(o != null)
			{
				w = ChatColor.GREEN + o.getName();
			}
			
			Notification n = NotificationPreset.COMBAT_KILLED.format(null, null, new Object[] {k, d, w});
			
			for(Player i : rg.players())
			{
				rg.getNotificationHandler().queue(i, n);
			}
		}
		
		int secondsAlive = game.getGameController().gpo(e.getPlayer()).getTicksAlive() / 20;
		Audio.COMBAT_KILLED.playGlobal(e.getPlayer().getLocation());
		dead.put(e.getPlayer(), t);
		lastKiller.put(e.getPlayer(), e.getDamager());
		e.getPlayer().setAllowFlight(true);
		e.getPlayer().setFlying(true);
		e.getPlayer().setGameMode(GameMode.SPECTATOR);
		game.getGameController().gpo(e.getPlayer()).spectator();
		
		int tx[] = new int[] {4};
		
		for(Player i : e.getRegion().getPlayers())
		{
			if(!Faction.get(i).equals(Faction.get(e.getPlayer())))
			{
				tx[0] += 3;
				
				break;
			}
		}
		
		if(secondsAlive < 60)
		{
			tx[0] += 3;
		}
		
		TeleportOperation tptp = new TeleportOperation(e.getPlayer(), tx[0] * 20, dead.get(e.getPlayer()).getSpawns().pickRandom());
		tptp.tickAudio(Audio.UI_CLICK).onTeleportTick(new TeleportTick()
		{
			@Override
			public void run()
			{
				if(!game.players().contains(e.getPlayer()))
				{
					tptp.cancel();
					return;
				}
				
				String kb = e.getGame().getFactionHandler().getFaction(e.getDamager()).getColor() + e.getDamager().getName();
				String ks = "";
				
				int hs = Integer.MIN_VALUE;
				Player kk = e.getDamager();
				
				if(assists.containsKey(e.getPlayer()))
				{
					assists.get(e.getPlayer()).remove(e.getDamager());
					
					for(Player i : assists.get(e.getPlayer()).keySet())
					{
						if(assists.get(e.getPlayer()).get(i) > hs)
						{
							hs = assists.get(e.getPlayer()).get(i);
							kk = i;
						}
					}
					
					ks = e.getGame().getFactionHandler().getFaction(kk).getColor() + kk.getName() + ChatColor.DARK_RED + " and " + (assists.get(e.getPlayer()).size()) + " others.";
				}
				
				else
				{
					ks = "No One";
				}
				
				if(kk.equals(e.getPlayer()))
				{
					ks = "◣_◢";
					kb = "◕︵◕";
				}
				
				if(getTicks() < (tx[0] - 1) * 20)
				{
					if(getTicks() % 20 == 0)
					{
						e.getGame().getNotificationHandler().queue(e.getPlayer(), NotificationPreset.RESPAWN_COOLDOWN.format(null, null, new Object[] {String.valueOf(getTicks() / 20)}));
					}
				}
				
				else
				{
					e.getGame().getNotificationHandler().queue(e.getPlayer(), NotificationPreset.RESPAWN_DEATH_COOLDOWN.format(new Object[] {"Killed", kb}, new Object[] {ks}, new Object[] {String.valueOf(getTicks() / 20)}));
				}
			}
		}).onTeleportCancelled(new Runnable()
		{
			@Override
			public void run()
			{
				e.getGame().getNotificationHandler().resetOngoing(e.getPlayer());
				e.getGame().getNotificationHandler().queue(e.getPlayer(), NotificationPreset.TELEPORT_CANCELLED_COMBAT.format(null, null, null));
			}
		}).onTeleported(new Runnable()
		{
			@Override
			public void run()
			{
				e.getGame().getNotificationHandler().resetOngoing(e.getPlayer());
				e.getGame().getNotificationHandler().queue(e.getPlayer(), NotificationPreset.TELEPORT_COMPLETED.format(null, null, null));
				dead.remove(e.getPlayer());
				lastKiller.remove(e.getPlayer());
				e.getPlayer().setGameMode(GameMode.ADVENTURE);
				e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
				e.getPlayer().setFoodLevel(20);
				e.getGame().pl().getUiController().moveShortcuts(e.getPlayer(), true);
				gameController.getPlayerObjects().get(e.getPlayer()).buildItems();
			}
		});
		
		tptp.commit();
	}
	
	@EventHandler
	public void combatHandler(PlayerDeathEvent e)
	{
		if(e.getEntity().getGameMode().equals(GameMode.SPECTATOR))
		{
			e.getEntity().setHealth(e.getEntity().getMaxHealth());
			e.getEntity().teleport(new Location(e.getEntity().getLocation().getWorld(), e.getEntity().getLocation().getX(), 20, e.getEntity().getLocation().getZ()));
		}
		
		if(!dead.containsKey(e.getEntity()))
		{
			e.setDeathMessage(null);
			e.setDroppedExp(0);
			e.setKeepInventory(true);
			e.setKeepLevel(true);
			e.getEntity().setHealth(e.getEntity().getMaxHealth());
			DeathEvent de = null;
			
			if(e.getEntity().getKiller() != null)
			{
				de = new DeathEvent(rg, e.getEntity(), e.getEntity().getKiller(), 0.0);
				calculateDeathXp(e.getEntity(), e.getEntity().getKiller(), assists.get(e.getEntity()));
			}
			
			else
			{
				de = new DeathEvent(rg, e.getEntity(), e.getEntity(), 0.0);
				calculateDeathXp(e.getEntity(), e.getEntity(), assists.get(e.getEntity()));
			}
			
			rg.pl().callEvent(de);
		}
	}
	
	@EventHandler
	public void combatHandler(CombatEvent e)
	{
		if(e.getDamager().equals(e.getPlayer()))
		{
			return;
		}
		
		if(e.getPlayer().isBlocking())
		{
			if(e.getDamager().getInventory().getHeldItemSlot() < 3)
			{
				Audio.COMBAT_BLOCK.playGlobal(e.getPlayer().getLocation());
				e.setDamage(e.getDamage() / 1.5);
			}
		}
		
		if(rg.getAbilityHandler().isActive(e.getDamager()) && rg.getAbilityHandler().getAbility(e.getDamager()).getAbilityEffect().equals(AbilityEffect.OVERCHARGE))
		{
			PlayerObject po = gameController.gpo(e.getPlayer());
			PlayerObject pod = gameController.gpo(e.getDamager());
			
			e.setDamage(e.getDamage() + (e.getDamage() * 0.2));
			pod.setEnergy(pod.getEnergy() + (e.getDamage() * 3));
			po.setEnergy(po.getEnergy() - (e.getDamage() * 3));
			Audio.SHIELD_DOWN.playGlobal(e.getDamager().getLocation());
			pod.setWaitreg(pod.getWaitreg() - 1);
		}
		
		if(rg.getAbilityHandler().isActive(e.getPlayer()) && rg.getAbilityHandler().getAbility(e.getPlayer()).getAbilityEffect().equals(AbilityEffect.OVERCHARGE))
		{
			PlayerObject pod = gameController.gpo(e.getDamager());
			
			e.setDamage(e.getDamage() + (e.getDamage() * 0.2));
			pod.setEnergy(pod.getEnergy() - (e.getDamage() * 8));
			Audio.SHIELD_DOWN.playGlobal(e.getDamager().getLocation());
			pod.setWaitreg(pod.getWaitreg() + 1);
		}
		
		if(!assists.containsKey(e.getPlayer()))
		{
			assists.put(e.getPlayer(), new GMap<Player, Integer>());
		}
		
		if(!assists.get(e.getPlayer()).containsKey(e.getDamager()))
		{
			assists.get(e.getPlayer()).put(e.getDamager(), 0);
		}
		
		assists.get(e.getPlayer()).put(e.getDamager(), assists.get(e.getPlayer()).get(e.getDamager()) + 1);
	}
	
	@EventHandler
	public void playerDamage(EntityDamageEvent e)
	{
		if(e.getCause() != null && e.getCause().equals(DamageCause.FALL))
		{
			if(e.getEntity().getType().equals(EntityType.PLAYER))
			{
				e.setDamage(e.getDamage() / 3);
				e.setDamage(gameController.gpo((Player) e.getEntity()).damage(e.getDamage()));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void combatHandler(EntityDamageByEntityEvent e)
	{
		if(e.getEntity().getType().equals(EntityType.PLAYER))
		{
			if(((Player) e.getEntity()).getGameMode().equals(GameMode.SPECTATOR))
			{
				((Player) e.getEntity()).setHealth(((Player) e.getEntity()).getMaxHealth());
				((Player) e.getEntity()).teleport(new Location(((Player) e.getEntity()).getLocation().getWorld(), ((Player) e.getEntity()).getLocation().getX(), 20, ((Player) e.getEntity()).getLocation().getZ()));
			}
		}
		
		if(e.getEntity().getType().equals(EntityType.PLAYER) && e.getDamager().getType().equals(EntityType.PLAYER))
		{
			Player p = (Player) e.getEntity();
			Player d = (Player) e.getDamager();
			
			if(rg.getGameController().getGame(p) == null || rg.getGameController().getGame(d) == null)
			{
				e.setCancelled(true);
				return;
			}
			
			if(rg.getGameController().getGame(p).getType().equals(GameType.REGIONED) && rg.getGameController().getGame(d).getType().equals(GameType.REGIONED))
			{
				if(rg.getGameController().getGame(p).equals(rg.getGameController().getGame(d)))
				{
					if(rg.getGameController().getGame(p).equals(rg))
					{
						if(rg.contains(p) && rg.contains(d))
						{
							if(!rg.getFactionHandler().getFaction(p).equals(rg.getFactionHandler().getFaction(d)))
							{
								if(d.isBlocking())
								{
									Audio.COMBAT_BLOCK.playGlobal(d.getLocation());
								}
								
								if(d.getLocation().getDirection().distance(p.getLocation().getDirection()) < 0.3)
								{
									e.setDamage(e.getDamage() + (e.getDamage() * 0.2));
								}
								
								CombatEvent ce = new CombatEvent(rg, p, d, e.getDamage());
								rg.pl().callEvent(ce);
								
								if(ce.isCancelled())
								{
									e.setCancelled(true);
								}
								
								else
								{
									e.setDamage(ce.getDamage());
									e.setDamage(game.getGameController().gpo(p).adamage(e.getDamage()));
								}
							}
							
							else
							{
								e.setCancelled(true);
							}
						}
						
						else
						{
							e.setCancelled(true);
						}
					}
				}
				
				else
				{
					e.setCancelled(true);
				}
			}
			
			else
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayer(PlayerQuitEvent e)
	{
		if(dead.containsKey(e.getPlayer()))
		{
			if(lastKiller.containsKey(e.getPlayer()))
			{
				Player krq = lastKiller.get(e.getPlayer());
				
				if(krq != null)
				{
					rg.getExperienceHandler().giveXp(krq, 1000l, Experience.RAGE_QUIT);
					Statistic.COMBAT_RAGEQUIT.add(krq);
				}
			}
		}
		
		for(Player i : new GList<Player>(lastKiller.keySet()))
		{
			if(lastKiller.get(i).equals(e.getPlayer()))
			{
				lastKiller.remove(i);
			}
		}
		
		lastKiller.remove(e.getPlayer());
		dead.remove(e.getPlayer());
		assists.remove(e.getPlayer());
		
		for(Player i : assists.keySet())
		{
			assists.get(i).remove(e.getPlayer());
		}
	}
	
	public RegionedGame getRg()
	{
		return rg;
	}
	
	public GList<Timer> getGameTimers()
	{
		return gameTimers;
	}
	
	public GMap<Timer, Long> getTimeLeft()
	{
		return timeLeft;
	}
	
	public ObjectiveTimer getObjectiveTimer()
	{
		return objectiveTimer;
	}
	
	public CaptureTimer getCaptureTimer()
	{
		return captureTimer;
	}
	
	public MapTimer getMapTimer()
	{
		return mapTimer;
	}
}
