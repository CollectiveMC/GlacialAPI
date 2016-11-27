package com.glacialrush.api.game.handler;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.xapi.Area;
import com.glacialrush.xapi.FastMath;

public class PaladinHandler extends GlacialHandler
{
	private GMap<IronGolem, Faction> paladins;
	private GMap<IronGolem, Guardian> guardians;
	private Random r;
	
	public PaladinHandler(Game game)
	{
		super(game);
		
		paladins = new GMap<IronGolem, Faction>();
		guardians = new GMap<IronGolem, Guardian>();
		r = new Random();
	}
	
	public void releasePaladin(Faction f, Territory t)
	{
		Entity e = t.getMap().getWorld().spawnEntity(t.getSpawns().pickRandom(), EntityType.IRON_GOLEM);
		
		if(e != null)
		{
			Guardian g = (Guardian) t.getMap().getWorld().spawnEntity(e.getLocation(), EntityType.GUARDIAN);
			IronGolem paladin = (IronGolem) e;
			paladin.setPassenger(g);
			paladins.put(paladin, f);
			guardians.put(paladin, g);
			
			for(Player i : game.players())
			{
				((RegionedGame)game).getPlayerHandler().getEh().showEntity(i, paladin);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void tick(GameState state)
	{
		for(IronGolem i : new GList<IronGolem>(paladins.keySet()))
		{
			Faction f = paladins.get(i);
			LivingEntity l = i.getTarget();
			Guardian gg = guardians.get(i);
			
			gg.setMaxHealth(300);
			gg.setHealth(gg.getMaxHealth());
			gg.getLocation().setDirection(i.getLocation().getDirection());
			
			if(!i.getLocation().getBlock().getType().equals(Material.AIR) || !i.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR))
			{
				i.teleport(i.getLocation().add(0, 1, 0));
			}
			
			if(i.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR))
			{
				ParticleEffect.FIREWORKS_SPARK.display(1f, 1f, 1f, 0.1f, 1, i.getLocation(), 32.0);
			}
			
			if(!i.getEyeLocation().getBlock().getType().equals(Material.AIR))
			{
				i.teleport(i.getLocation().add(0, 3, 0));
			}
			
			if(l != null && r.nextBoolean() && r.nextBoolean() && r.nextBoolean())
			{
				Vector v = i.getTarget().getLocation().subtract(i.getLocation()).toVector().normalize();
				
				if(i.getLocation().distance(l.getLocation()) > 6)
				{
					i.setVelocity(v.multiply(2));
					i.getLocation().setDirection(v);
					
					ParticleEffect.LAVA.display(0f, 0f, 0f, 1f, 30, i.getLocation(), 32.0);
					
					if(i.getLocation().distance(l.getLocation()) < 40 && r.nextBoolean() && r.nextBoolean() && r.nextBoolean() && r.nextBoolean())
					{
						Location trg = i.getLocation().clone().add(v.multiply(10));
						particleLOS(ParticleEffect.SMOKE_LARGE, i.getLocation(), trg);
						i.teleport(trg);
						Audio.ABILITY_TELEPORT.playGlobal(trg);
					}
				}
				
				else
				{
					if(i.getLocation().distance(l.getLocation()) < 8)
					{
						if(r.nextBoolean() && r.nextBoolean() && r.nextBoolean() && r.nextBoolean())
						{
							ParticleEffect.REDSTONE.display(0f, 1f, 0f, 1f, 30, i.getLocation(), 32.0);
							Audio.MONSTER.playGlobal(i.getLocation());
							
							try
							{
								((RegionedGame)game).getToolHandler().cooldown((Player) i.getTarget());
							}
							
							catch(Exception e)
							{
								
							}
						}
						
						if(r.nextBoolean() && r.nextBoolean() && r.nextBoolean() && r.nextBoolean())
						{
							ParticleEffect.LAVA.display(0, 1, 0, 1.9f, 64, i.getLocation(), 48);
							ParticleEffect.FLAME.display(0, 1, 0, 2.9f, 64, i.getLocation(), 48);
							ParticleEffect.SMOKE_LARGE.display(0, 1, 0, 1.3f, 64, i.getLocation(), 48);
							Audio.ABILITY_TELEPORT.playGlobal(i.getLocation());
							Area a = new Area(i.getLocation(), 6.3);
							
							for(Player j : a.getNearbyPlayers())
							{
								if(game.players().contains(j) && !((RegionedGame)game).getFactionHandler().getFaction(j).equals(paladins.get(i)))
								{
									if(j.isOnGround())
									{
										j.setAllowFlight(true);
										j.setFlying(true);
										Vector vx = j.getLocation().clone().subtract(i.getLocation()).toVector();
										vx.subtract(new Vector(0, 10, 0));
										j.setVelocity(vx.multiply(4));
										j.setFlying(false);
										j.setAllowFlight(false);
									}
									
									else
									{
										j.setAllowFlight(true);
										j.setFlying(true);
										j.setVelocity(j.getVelocity().add(new Vector(0, 10, 0)).normalize());
										j.setFlying(false);
										j.setAllowFlight(false);
									}
								}
							}
						}
					}
				}
			}
			
			if(l == null || !l.getType().equals(EntityType.PLAYER))
			{
				retarget(i);
				continue;
			}
			
			Player p = (Player) l;
			Faction pf = ((RegionedGame) game).getFactionHandler().getFaction(p);
			
			if(!pf.equals(f))
			{
				retarget(i);
				continue;
			}
		}
	}
	
	public void particleLOS(ParticleEffect e, Location a, Location b)
	{
		Vector v = a.clone().subtract(b).toVector().clone().normalize();
		Location c = a.clone();
		
		for(int i = 0; i < a.distance(b); i++)
		{
			e.display(0f, 0f, 0f, 0.5f, 6, c, 48);
			e.display(0f, 0f, 0f, 0.1f, 6, c, 48);
			e.display(0f, 0f, 0f, 0.3f, 6, c, 48);
			c.subtract(v);
		}
	}
	
	public GMap<IronGolem, Faction> getPaladins()
	{
		return paladins;
	}

	public void setPaladins(GMap<IronGolem, Faction> paladins)
	{
		this.paladins = paladins;
	}

	public void retarget(IronGolem i)
	{
		Faction f = paladins.get(i);
		Player t = null;
		Double d = Double.MAX_VALUE;
		
		for(Player p : game.players())
		{
			double dd = FastMath.distance2D(i.getLocation(), p.getLocation());
			
			if(dd < d)
			{
				Faction pf = ((RegionedGame) game).getFactionHandler().getFaction(p);
				
				if(!pf.equals(f) && p.getGameMode().equals(GameMode.ADVENTURE))
				{
					d = dd;
					t = p;
				}
			}
		}
		
		if(t != null)
		{
			i.setTarget(t);
			
			if(t.getLocation().distance(i.getLocation()) < 30)
			{
				guardians.get(i).setTarget(t);
			}
			
			else
			{
				guardians.get(i).setTarget(null);
			}
		}
		
		else
		{
			i.setTarget(null);
			guardians.get(i).setTarget(null);
		}
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void entityDamage(EntityDamageByEntityEvent e)
	{
		if(e.getDamager().getType().equals(EntityType.IRON_GOLEM))
		{
			if(e.getEntity().getType().equals(EntityType.PLAYER))
			{
				Player p = (Player) e.getEntity();
				IronGolem i = (IronGolem) e.getDamager();
				
				for(IronGolem j : new GList<IronGolem>(paladins.keySet()))
				{
					if(i.getEntityId() == j.getEntityId())
					{
						Faction fi = paladins.get(j);
						Faction fp = ((RegionedGame) game).getFactionHandler().getFaction(p);
						
						if(fi.equals(fp))
						{
							e.setCancelled(true);
							
							break;
						}
						
						else
						{
							e.setCancelled(false);
							e.setDamage(3.0);
							e.setDamage(gameController.gpo(p).damage(e.getDamage()));
							break;
						}
					}
				}
			}
		}
		
		if(e.getDamager().getType().equals(EntityType.PLAYER))
		{
			if(e.getEntity().getType().equals(EntityType.IRON_GOLEM))
			{
				Player p = (Player) e.getDamager();
				IronGolem i = (IronGolem) e.getEntity();
				
				for(IronGolem j : new GList<IronGolem>(paladins.keySet()))
				{
					if(i.getEntityId() == j.getEntityId())
					{
						Faction fi = paladins.get(j);
						Faction fp = ((RegionedGame) game).getFactionHandler().getFaction(p);
						
						if(fi.equals(fp))
						{
							e.setCancelled(true);
							break;
						}
						
						else
						{
							e.setCancelled(false);
							break;
						}
					}
				}
			}
		}
	}
	
	public void remove(Faction f)
	{
		for(IronGolem i : new GList<IronGolem>(paladins.keySet()))
		{
			if(paladins.get(i).equals(f))
			{
				guardians.get(i).remove();
				guardians.remove(i);
				i.getLocation().getWorld().createExplosion(i.getLocation(), 0f);
				paladins.remove(i);
				i.remove();
			}
		}
	}
	
	public void stop()
	{
		for(IronGolem i : new GList<IronGolem>(paladins.keySet()))
		{
			guardians.get(i).remove();
			guardians.remove(i);
			i.remove();
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void entityDamage(EntityDamageEvent e)
	{
		if(e.getEntity().getType().equals(EntityType.GUARDIAN))
		{
			for(IronGolem i : guardians.keySet())
			{
				if(guardians.get(i).getEntityId() == e.getEntity().getEntityId())
				{
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void entityDeath(EntityDeathEvent e)
	{
		if(e.getEntityType().equals(EntityType.IRON_GOLEM))
		{
			for(IronGolem i : new GList<IronGolem>(paladins.keySet()))
			{
				if(i.getEntityId() == e.getEntity().getEntityId())
				{
					paladins.remove(i);
					guardians.get(i).remove();
					guardians.remove(i);
					
					if(e.getEntity().getKiller() == null)
					{
						return;
					}
					
					if(e.getEntity().getKiller().getType().equals(EntityType.PLAYER))
					{
						((RegionedGame)game).getExperienceHandler().giveXp(((Player)e.getEntity().getKiller()), 1000l, Experience.PALADIN_KILL);
					}
				}
			}
		}
	}
}
