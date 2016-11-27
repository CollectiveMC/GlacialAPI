package com.glacialrush.api.game.object;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.DeathEvent;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.xapi.Area;

public class Grenade
{
	public Grenade(Player p)
	{
		Item i = p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.BONE));
		i.setVelocity(p.getLocation().getDirection().multiply(2.4));
		Audio.THROWABLE.playGlobal(i.getLocation());
		
		GlacialPlugin.instance().scheduleSyncTask(60, new Runnable()
		{
			@Override
			public void run()
			{
				Audio.GRENADE_BOOM.playGlobal(i.getLocation());
				
				for(Player i : new Area(i.getLocation(), 512.0).getNearbyPlayers())
				{
					Audio.GRENADE_BOOM_DULL.play(i);
				}
				
				ParticleEffect.LAVA.display(2, 2, 2, 1, 250, i.getLocation(), 128);
				ParticleEffect.SMOKE_LARGE.display(2, 2, 2, 0.5f, 90, i.getLocation(), 128);
				ParticleEffect.CLOUD.display(2, 2, 2, 1, 60, i.getLocation(), 128);
				Area a = new Area(i.getLocation(), 16.0);
				
				for(Entity i : a.getNearbyEntities())
				{
					if(i.getType().equals(EntityType.IRON_GOLEM))
					{
						((IronGolem)i).setHealth(((IronGolem)i).getHealth() / 4);
						i.getWorld().createExplosion(i.getLocation(), 0f);
					}
				}
				
				for(Player i : a.getNearbyPlayers())
				{
					if(!i.getGameMode().equals(GameMode.ADVENTURE))
					{
						return;
					}
					
					Double dmgi = 50.0;
					
					try
					{
						if(Faction.get(i).equals(Faction.get(p)))
						{
							dmgi = 20.0;
						}
					}
					
					catch(Exception e)
					{
						
					}
					
					Double damage = dmgi * (1 - ((a.getLocation().distance(i.getLocation()) / a.getRadius())));
					Double dmgg = GlacialPlugin.instance().getGameControl().gpo(i).damage(damage);
					
					if(i.getHealth() - dmgg <= 0)
					{
						DeathEvent de = new DeathEvent((RegionedGame) GlacialPlugin.instance().getGameControl().getGame(i), i, p, dmgg);
						GlacialPlugin.instance().callEvent(de);
						
						if(Faction.get(i).equals(Faction.get(p)))
						{
							((RegionedGame) GlacialPlugin.instance().gameControl.getGame(p)).getNotificationHandler().queue(p, new Notification(null, null, ChatColor.RED + "Shields Disabled for 30s (friendly fire)"));
							GlacialPlugin.instance().getGameControl().gpo(p).breakSheild();
							GlacialPlugin.instance().getGameControl().gpo(p).destroyRecharger(30 * 20);
						}
						
						else
						{
							((RegionedGame) GlacialPlugin.instance().gameControl.getGame(p)).getExperienceHandler().giveXp(p, (long) 1000, Experience.KILL);
						}
					}
						
					else
					{
						i.damage(dmgg);
					}
				}
				
				i.remove();
			}
		});
					
		int[] tid = new int[] { 0 };
		tid[0] = GlacialPlugin.instance().scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				if(i.isDead())
				{
					GlacialPlugin.instance().cancelTask(tid[0]);
					return;
				}
				
				ParticleEffect.SMOKE_LARGE.display(0.4f, 0.4f, 0.4f, 0.1f, 5, i.getLocation(), 128);
			}
		});
	}
}
