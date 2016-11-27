package com.glacialrush.api.game.object;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.xapi.Area;

public class EMP
{
	public EMP(Player p)
	{
		Item i = p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.BOOK));
		i.setVelocity(p.getLocation().getDirection().multiply(2.4));
		Audio.THROWABLE.playGlobal(i.getLocation());
		
		GlacialPlugin.instance().scheduleSyncTask(60, new Runnable()
		{
			@Override
			public void run()
			{
				Audio.EMP.playGlobal(i.getLocation());
				
				ParticleEffect.CRIT_MAGIC.display(2, 2, 2, 1, 250, i.getLocation(), 128);
				Area a = new Area(i.getLocation(), 16.0);
				
				for(Player ip : a.getNearbyPlayers())
				{
					GlacialPlugin.instance().gameControl.gpo(ip).breakSheild();
					((RegionedGame)GlacialPlugin.instance().gameControl.getGame(ip)).getAbilityHandler().red(ip);
					particleLOS(ParticleEffect.FIREWORKS_SPARK, ip.getLocation(), i.getLocation());
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
				
				ParticleEffect.CRIT_MAGIC.display(0.4f, 0.4f, 0.4f, 0.1f, 5, i.getLocation(), 128);
				ParticleEffect.FIREWORKS_SPARK.display(0.4f, 0.4f, 0.4f, 0.1f, 5, i.getLocation(), 128);
			}
		});
	}
	
	public void particleLOS(ParticleEffect e, Location a, Location b)
	{
		Vector v = a.clone().subtract(b).toVector().clone().normalize();
		Location c = a.clone();
		
		for(int i = 0; i < a.distance(b); i++)
		{
			e.display(0f, 0f, 0f, i * 0.1f, 6, c, 48);
			c.subtract(v);
		}
	}
}
