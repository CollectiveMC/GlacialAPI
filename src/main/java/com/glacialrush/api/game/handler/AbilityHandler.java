package com.glacialrush.api.game.handler;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.ActivateAbilityEvent;
import com.glacialrush.api.game.event.CombatEvent;
import com.glacialrush.api.game.event.FireAbilityEvent;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.PlayerObject;
import com.glacialrush.api.game.obtainable.Ability;
import com.glacialrush.api.game.obtainable.RuneType;
import com.glacialrush.api.game.obtainable.item.AbilityEffect;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.xapi.Area;
import com.glacialrush.xapi.FastMath;

public class AbilityHandler extends GlacialHandler
{
	private Random r;
	
	public AbilityHandler(Game game)
	{
		super(game);
		
		r = new Random();
	}
	
	public void reset(Player p)
	{
		setEnergy(p, 100);
	}
	
	public double getEnergy(Player p)
	{
		return gameController.gpo(p).getEnergy();
	}
	
	public void setEnergy(Player p, double eng)
	{
		gameController.gpo(p).setEnergy(eng);
	}
	
	public void drain(Player p)
	{
		setEnergy(p, 0);
		deactivate(p);
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		p.removePotionEffect(PotionEffectType.SLOW);
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20, 1));
	}
	
	public boolean isActive(Player p)
	{
		return gameController.gpo(p).getAbilityActive();
	}
	
	public void setActive(Player p, boolean active)
	{
		gameController.gpo(p).setAbilityActive(active);
	}
	
	public AbilityEffect getAbilityEffect(Player p)
	{
		if(game.getGameController().gpo(p).getAbility() != null)
		{
			return game.getGameController().gpo(p).getAbility().getAbilityEffect();
		}
		
		return null;
	}
	
	public Ability getAbility(Player p)
	{
		if(game.getGameController().gpo(p).getAbility() != null)
		{
			return game.getGameController().gpo(p).getAbility();
		}
		
		return null;
	}
	
	public void setLevel(Player p, double h)
	{
		p.setExp(new Double(h).floatValue());
	}
	
	public void buff(Player p)
	{
		setEnergy(p, getEnergy(p) * 2);
	}
	
	public void unc(Player p)
	{
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		drain(p);
		Audio.ABILITY_CLOAK_DIE.playGlobal(p.getLocation());
		ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 30, p.getLocation(), 48);
		ParticleEffect.CLOUD.display(0, 0.4f, 0, 0.1f, 30, p.getLocation(), 48);
		ParticleEffect.CLOUD.display(0, 0.7f, 0, 0.1f, 30, p.getLocation(), 48);
		ParticleEffect.CLOUD.display(0, 1, 0, 0.1f, 30, p.getLocation(), 48);
	}
	
	public void tick(GameState state)
	{
		for(Player i : state.getGame().pl().onlinePlayers())
		{
			try
			{
				getGameController().gpo(i).regenTick();
				i.setFoodLevel((int) (20 * (getGameController().gpo(i).getShields() / gameController.gpo(i).getShield().getMaxShields())));
				
				if(isActive(i))
				{
					setEnergy(i, getEnergy(i) - abc(i));
					
					if(getEnergy(i) < abc(i))
					{
						deactivate(i);
					}
					
					else
					{
						if(getAbilityEffect(i).equals(AbilityEffect.CLOAK) && isActive(i) && r.nextBoolean() && r.nextBoolean())
						{
							if(Math.random() < 0.2)
							{
								ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 1, i.getLocation(), 10);
							}
							
							i.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5, 0, true, false));
						}
						
						if(getAbilityEffect(i).equals(AbilityEffect.STRENGTH) && isActive(i))
						{
							ParticleEffect.VILLAGER_ANGRY.display(0.5f, 1, 0.5f, 0.1f, 1, i.getLocation().add(0, 2, 0), 48);
							i.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 0, true, false));
							i.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5, 1, true, false));
						}
						
						if(getAbilityEffect(i).equals(AbilityEffect.HEAL) && isActive(i))
						{
							Area ar = new Area(i.getLocation(), 7.0);
							Faction f = ((RegionedGame) game).getFactionHandler().getFaction(i);
							
							for(Player jj : ar.getNearbyPlayers())
							{
								if(jj.equals(i))
								{
									continue;
								}
								
								Faction fx = ((RegionedGame) game).getFactionHandler().getFaction(jj);
								
								if(game.players().contains(jj) && f.equals(fx))
								{
									if(Math.random() < 0.3)
									{
										gparticleLOS(ParticleEffect.VILLAGER_HAPPY, i.getLocation().add(0, 1, 0), jj.getLocation().add(0, 1, 0));
									}
									
									jj.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1));
								}
							}
							
							for(Entity jj : ar.getNearbyEntities())
							{
								if(jj.getType().equals(EntityType.IRON_GOLEM))
								{
									Faction fx = ((RegionedGame) game).getPaladinHandler().getPaladins().get((IronGolem) jj);
									
									if(fx != null && fx.equals(f))
									{
										if(Math.random() < 0.3)
										{
											gparticleLOS(ParticleEffect.VILLAGER_HAPPY, i.getLocation().add(0, 1, 0), jj.getLocation().add(0, 1, 0));
										}
										
										((IronGolem) jj).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 3));
									}
								}
							}
							
							particleSPHERE(ParticleEffect.VILLAGER_HAPPY, i.getLocation().add(0, 3, 0), 8);
						}
						
						if(getAbilityEffect(i).equals(AbilityEffect.SHIELD) && isActive(i))
						{
							ParticleEffect.LAVA.display(0, 1, 0, 1.9f, 1, i.getLocation().clone().subtract(0, 1, 0), 48);
						}
						
						if(getAbilityEffect(i).equals(AbilityEffect.OVERCHARGE) && isActive(i))
						{
							ParticleEffect.FIREWORKS_SPARK.display(0, 1, 0, 1.1f, 1, i.getLocation().clone().subtract(0, 1, 0), 48);
						}
						
						if(getAbilityEffect(i).equals(AbilityEffect.VELOCITY_BOOST) && isActive(i))
						{
							i.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5, 1, true, false));
							i.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 80, true, false));
							
							try
							{
								particleLOS(ParticleEffect.TOWN_AURA, getGame().pl().target(i), i.getLocation());
							}
							
							catch(Exception e)
							{
								
							}
						}
					}
				}
				
				else
				{
					setEnergy(i, getEnergy(i) + 0.1);
					
					if(gameController.gpo(i).getRune() != null)
					{
						if(gameController.gpo(i).getRune().getType().equals(RuneType.ENERGY))
						{
							setEnergy(i, getEnergy(i) + (0.005 * getEnergy(i)));
						}
					}
				}
			}
			
			catch(Exception e)
			{
				
			}
		}
	}
	
	@EventHandler
	public void activate(ActivateAbilityEvent e)
	{
		if(!game.contains(e.getPlayer()))
		{
			return;
		}
		
		if(getEnergy(e.getPlayer()) < 10 && isActive(e.getPlayer()))
		{
			e.setCancelled(true);
		}
	}
	
	public void deactivate(Player p)
	{
		PlayerObject o = game.getGameController().gpo(p);
		
		setActive(p, false);
		
		if(o.getAbility().getAbilityEffect().equals(AbilityEffect.SHIELD))
		{
			if(p.getHealth() >= 20)
			{
				p.setHealth(20);
			}
			
			p.setMaxHealth(20);
		}
		
		else if(o.getAbility().getAbilityEffect().equals(AbilityEffect.STRENGTH))
		{
			
		}
		
		else if(o.getAbility().getAbilityEffect().equals(AbilityEffect.REPELL))
		{
			
		}
		
		else if(o.getAbility().getAbilityEffect().equals(AbilityEffect.TELEPORT))
		{
			
		}
		
		else if(o.getAbility().getAbilityEffect().equals(AbilityEffect.CLOAK))
		{
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
			Audio.ABILITY_CLOAK_DIE.playGlobal(p.getLocation());
			ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 30, p.getLocation(), 48);
			ParticleEffect.CLOUD.display(0, 0.4f, 0, 0.1f, 30, p.getLocation(), 48);
			ParticleEffect.CLOUD.display(0, 0.7f, 0, 0.1f, 30, p.getLocation(), 48);
			ParticleEffect.CLOUD.display(0, 1, 0, 0.1f, 30, p.getLocation(), 48);
		}
	}
	
	public double abc(Player p)
	{
		if(getAbilityEffect(p).equals(AbilityEffect.TELEPORT))
		{
			return 50;
		}
		
		if(getAbilityEffect(p).equals(AbilityEffect.REPELL))
		{
			return 60;
		}
		
		if(getAbilityEffect(p).equals(AbilityEffect.ENDERBLINK))
		{
			return 60;
		}
		
		if(getAbilityEffect(p).equals(AbilityEffect.SHIELD))
		{
			return 0.15;
		}
		
		if(getAbilityEffect(p).equals(AbilityEffect.OVERCHARGE))
		{
			return 0.42;
		}
		
		return 0.3;
	}
	
	@EventHandler
	public void augmentedKnockback(EntityDamageByEntityEvent e)
	{
		final Entity damager = e.getDamager();
		final Entity defender = e.getEntity();
		
		if(defender instanceof Player && (damager instanceof Projectile))
		{
			game.pl().getServer().getScheduler().scheduleSyncDelayedTask(game.pl(), new Runnable()
			{
				@Override
				public void run()
				{
					Player p = (Player) defender;
					Projectile px = (Projectile) damager;
					ProjectileSource ex = px.getShooter();
					
					if(ex instanceof Player)
					{
						Player dm = (Player) ex;
						
						if(px.getType().equals(EntityType.ARROW))
						{
							if(isActive(dm))
							{
								if(getAbilityEffect(dm).equals(AbilityEffect.REPELL))
								{
									Vector knockback = defender.getVelocity().multiply(0.1f);
									p.setVelocity(knockback);
								}
							}
							
							if(!((Arrow) px).isCritical())
							{
								Vector knockback = defender.getVelocity().multiply(0.4f);
								p.setVelocity(knockback);
							}
						}
					}
				}
			}, 1L);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void activate(final FireAbilityEvent e)
	{
		if(!game.contains(e.getPlayer()))
		{
			return;
		}
		
		if(isActive(e.getPlayer()))
		{
			setActive(e.getPlayer(), false);
			Audio.ABILITY_DOWN.playGlobal(e.getPlayer().getLocation());
			e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
			e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
			e.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			e.getPlayer().setMaxHealth(20.0);
			deactivate(e.getPlayer());
		}
		
		else
		{
			Ability a = e.getAbility();
			
			if(getEnergy(e.getPlayer()) < abc(e.getPlayer()) || getEnergy(e.getPlayer()) < 16)
			{
				Audio.UI_FAIL.play(e.getPlayer());
				gameController.gpo(e.getPlayer()).signalEnergy(abc(e.getPlayer()));
				return;
			}
			
			a.getAbilityFiredSound().playGlobal(e.getPlayer().getLocation());
			setActive(e.getPlayer(), true);
			((RegionedGame) game).getToolHandler().cooldown(e.getPlayer());
			
			if(a.getAbilityEffect().equals(AbilityEffect.SHIELD))
			{
				
			}
			
			else if(a.getAbilityEffect().equals(AbilityEffect.STRENGTH))
			{
				
			}
			
			else if(a.getAbilityEffect().equals(AbilityEffect.ENDERBLINK))
			{
				enderBlinkChain(e.getPlayer(), 5);
				setActive(e.getPlayer(), false);
				setEnergy(e.getPlayer(), getEnergy(e.getPlayer()) - abc(e.getPlayer()));
			}
			
			else if(a.getAbilityEffect().equals(AbilityEffect.VELOCITY_BOOST))
			{
				
			}
			
			else if(a.getAbilityEffect().equals(AbilityEffect.HEAL))
			{
				
			}
			
			else if(a.getAbilityEffect().equals(AbilityEffect.TELEPORT))
			{
				Vector v = e.getPlayer().getLocation().getDirection();
				Area af = new Area(e.getPlayer().getLocation(), 7.6);
				setActive(e.getPlayer(), false);
				setEnergy(e.getPlayer(), getEnergy(e.getPlayer()) - abc(e.getPlayer()));
				
				for(Entity i : af.getNearbyEntities())
				{
					if(i.getType().equals(EntityType.PLAYER) && !((Player) i).equals(e.getPlayer()))
					{
						Player px = (Player) i;
						
						if(!((RegionedGame) game).getFactionHandler().getFaction(px).equals(((RegionedGame) game).getFactionHandler().getFaction(e.getPlayer())))
						{
							px.setVelocity(px.getVelocity().add(v).normalize());
						}
					}
					
					if(i.getType().equals(EntityType.IRON_GOLEM))
					{
						IronGolem gg = (IronGolem) i;
						
						if(((RegionedGame) game).getPaladinHandler().getPaladins().containsKey(gg) && !((RegionedGame) game).getPaladinHandler().getPaladins().get(gg).equals(((RegionedGame) game).getFactionHandler().getFaction(e.getPlayer())))
						{
							gg.setVelocity(gg.getVelocity().add(v).normalize());
						}
					}
				}
				
				Location ax = e.getPlayer().getLocation();
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 10, true, false));
				Block block = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 48);
				e.getPlayer().teleport(block.getLocation().add(0, 1, 0).setDirection(v));
				a.getAbilityFiredSound().playGlobal(e.getPlayer().getLocation());
				particleLOS(ParticleEffect.SMOKE_LARGE, ax, e.getPlayer().getLocation());
			}
			
			else if(a.getAbilityEffect().equals(AbilityEffect.REPELL))
			{
				e.getPlayer().setAllowFlight(true);
				e.getPlayer().setFlying(true);
				e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(4));
				e.getPlayer().setFlying(false);
				e.getPlayer().setAllowFlight(false);
				ParticleEffect.CLOUD.display(0, 1, 0, 1.9f, 64, e.getPlayer().getLocation(), 48);
				ParticleEffect.LAVA.display(0, 1, 0, 1.9f, 8, e.getPlayer().getLocation(), 48);
				ParticleEffect.FIREWORKS_SPARK.display(0, 1, 0, 2.9f, 64, e.getPlayer().getLocation(), 48);
				ParticleEffect.SPELL_INSTANT.display(0, 1, 0, 1.3f, 64, e.getPlayer().getLocation(), 48);
				setActive(e.getPlayer(), false);
				setEnergy(e.getPlayer(), getEnergy(e.getPlayer()) - abc(e.getPlayer()));
				
				for(final Player i : game.players())
				{
					if(FastMath.isInRadius(i.getLocation(), e.getPlayer().getLocation(), 5.6f))
					{
						Vector v = i.getLocation().clone().subtract(e.getPlayer().getLocation()).toVector();
						v.normalize();
						e.getPlayer().getWorld().createExplosion(i.getLocation().clone().add(0, 3, 0), 0.0f);
						v.multiply(new Vector(9.5, 3.8, 9.5));
						i.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1, 10));
						i.getPlayer().setVelocity(v);
					}
				}
				
				Area ax = new Area(e.getPlayer().getLocation(), 6.5);
				
				for(Entity i : ax.getNearbyEntities())
				{
					Vector v = i.getLocation().clone().subtract(e.getPlayer().getLocation()).toVector();
					i.setVelocity(v.multiply(1.8));
				}
			}
			
			else if(a.getAbilityEffect().equals(AbilityEffect.CLOAK))
			{
				ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 30, e.getPlayer().getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 0.4f, 0, 0.1f, 30, e.getPlayer().getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 0.7f, 0, 0.1f, 30, e.getPlayer().getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 1, 0, 0.1f, 30, e.getPlayer().getLocation(), 48);
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, e.getAbility().getDuration() * 20, 1));
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void bowShoot(EntityShootBowEvent e)
	{
		if(e.getEntity().getType().equals(EntityType.PLAYER))
		{
			Player p = (Player) e.getEntity();
			
			if(getAbilityEffect(p).equals(AbilityEffect.CLOAK))
			{
				if(isActive(p))
				{
					drain(p);
					Audio.ABILITY_CLOAK_DIE.playGlobal(p.getLocation());
					((RegionedGame) game).getPlayerHandler().getInvisible().remove(p);
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					Audio.ABILITY_CLOAK_DIE.playGlobal(p.getLocation());
					ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 30, p.getLocation(), 48);
					ParticleEffect.CLOUD.display(0, 0.4f, 0, 0.1f, 30, p.getLocation(), 48);
					ParticleEffect.CLOUD.display(0, 0.7f, 0, 0.1f, 30, p.getLocation(), 48);
					ParticleEffect.CLOUD.display(0, 1, 0, 0.1f, 30, p.getLocation(), 48);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onCombat(CombatEvent e)
	{
		if(getAbilityEffect(e.getPlayer()).equals(AbilityEffect.CLOAK))
		{
			if(isActive(e.getPlayer()))
			{
				Player p = e.getPlayer();
				
				drain(e.getPlayer());
				Audio.ABILITY_CLOAK_DIE.playGlobal(e.getPlayer().getLocation());
				((RegionedGame) game).getPlayerHandler().getInvisible().remove(p);
				p.removePotionEffect(PotionEffectType.INVISIBILITY);
				Audio.ABILITY_CLOAK_DIE.playGlobal(p.getLocation());
				ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 30, p.getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 0.4f, 0, 0.1f, 30, p.getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 0.7f, 0, 0.1f, 30, p.getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 1, 0, 0.1f, 30, p.getLocation(), 48);
			}
		}
		
		if(getAbilityEffect(e.getDamager()).equals(AbilityEffect.CLOAK))
		{
			if(isActive(e.getDamager()))
			{
				Player p = e.getDamager();
				
				drain(e.getDamager());
				Audio.ABILITY_CLOAK_DIE.playGlobal(e.getDamager().getLocation());
				((RegionedGame) game).getPlayerHandler().getInvisible().remove(p);
				p.removePotionEffect(PotionEffectType.INVISIBILITY);
				Audio.ABILITY_CLOAK_DIE.playGlobal(p.getLocation());
				ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 30, p.getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 0.4f, 0, 0.1f, 30, p.getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 0.7f, 0, 0.1f, 30, p.getLocation(), 48);
				ParticleEffect.CLOUD.display(0, 1, 0, 0.1f, 30, p.getLocation(), 48);
			}
		}
	}
	
	public void enderBlinkChain(final Player p, int size)
	{
		final int[] tt = new int[] { 0, 0, size };
		final GList<Integer> ids = new GList<Integer>();
		final Location l = p.getLocation().clone();
		
		tt[0] = game.pl().scheduleSyncRepeatingTask(0, 10, new Runnable()
		{
			@Override
			public void run()
			{
				if(p.getGameMode().equals(GameMode.SPECTATOR))
				{
					game.pl().cancelTask(tt[0]);
				}
				
				if(tt[1] >= tt[2])
				{
					game.pl().cancelTask(tt[0]);
					particleLOS(ParticleEffect.SPELL_WITCH, p.getLocation(), l);
					p.teleport(l);
				}
				
				Integer x = enderBlink(p, ids);
				
				if(x == null)
				{
					game.pl().cancelTask(tt[0]);
					particleLOS(ParticleEffect.SPELL_WITCH, p.getLocation(), l);
					p.teleport(l);
				}
				
				else
				{
					ids.add(x);
				}
				
				tt[1]++;
			}
		});
	}
	
	public Integer enderBlink(Player p, GList<Integer> ids)
	{
		Faction f = ((RegionedGame) game).getFactionHandler().getFaction(p);
		Area a = new Area(p.getLocation(), 12.6);
		Double d = Double.MAX_VALUE;
		Player t = null;
		Integer imx = null;
		Location l = p.getLocation().clone();
		IronGolem g = null;
		
		for(Player i : a.getNearbyPlayers())
		{
			Faction pf = ((RegionedGame) game).getFactionHandler().getFaction(i);
			
			if(!pf.equals(f) && !ids.contains(i.getEntityId()))
			{
				Double id = i.getLocation().distance(p.getLocation());
				
				if(id < d)
				{
					d = id;
					t = i;
				}
			}
			
			if(t != null)
			{
				((RegionedGame) game).getToolHandler().cooldown(t);
			}
		}
		
		if(t != null)
		{
			ids.add(t.getEntityId());
			imx = t.getEntityId();
			particleLOS(ParticleEffect.SPELL_WITCH, p.getLocation(), t.getLocation());
			p.teleport(t.getLocation());
			GlacialPlugin.instance().getGameControl().gpo(t).breakSheild();
			t.getLocation().getWorld().createExplosion(t.getLocation(), 0f);
			
			game.pl().scheduleSyncTask(10, new Runnable()
			{
				@Override
				public void run()
				{
					p.teleport(l);
				}
			});
		}
		
		else
		{
			Double dx = Double.MAX_VALUE;
			
			for(Entity i : a.getNearbyEntities())
			{
				if(i.getType().equals(EntityType.IRON_GOLEM) && !ids.contains(i.getEntityId()))
				{
					IronGolem gg = (IronGolem) i;
					
					if(((RegionedGame) game).getPaladinHandler().getPaladins().containsKey(gg))
					{
						if(!f.equals(((RegionedGame) game).getPaladinHandler().getPaladins().get(gg)))
						{
							Double id = i.getLocation().distance(p.getLocation());
							
							if(id < dx)
							{
								dx = id;
								g = gg;
							}
						}
					}
				}
			}
			
			if(g != null)
			{
				ids.add(g.getEntityId());
				imx = g.getEntityId();
				
				particleLOS(ParticleEffect.SPELL_WITCH, p.getLocation(), g.getLocation());
				p.teleport(g.getLocation());
				g.damage(27.1);
				g.getLocation().getWorld().createExplosion(g.getLocation(), 0f);
			}
		}
		
		return imx;
	}
	
	public void particleSPHERE(ParticleEffect e, Location c, double rad)
	{
		Area a = new Area(c, rad);
		
		e.display(0f, 0f, 0f, 0.5f, 1 + (int) rad / 8, a.random(), 48);
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
	
	public void gparticleLOS(ParticleEffect e, Location a, Location b)
	{
		Vector v = a.clone().subtract(b).toVector().clone().normalize();
		Location c = a.clone();
		
		for(int i = 0; i < a.distance(b); i++)
		{
			if(Math.random() < 0.3)
			{
				e.display(0f, 0f, 0f, 0.1f, 1, c, 48);
			}
			
			c.subtract(v);
		}
	}
	
	public void red(Player ip)
	{
		drain(ip);
	}
}
