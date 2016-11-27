package com.glacialrush.api.game.object;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.sfx.Audio;

public class Grapple
{
	private Silverfish hook;
	private Player player;
	private Location hooked;
	private Location target;
	private GlacialPlugin pl;
	private Boolean unhooked;
	private Boolean hooking;
	
	public Grapple(Player player)
	{
		this.player = player;
		this.pl = GlacialPlugin.instance();
		this.target = pl.target(player);
		this.hook = (Silverfish) player.getLocation().getWorld().spawnEntity(player.getLocation().add(0, 2, 0), EntityType.SILVERFISH);
		this.hooked = null;
		this.unhooked = false;
		this.hooking = false;
		
		hook.setLeashHolder(player);
		hook.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000, 4));
		hook.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000, 0));
		hook.setMaxHealth(400);
		hook.setHealth(400);
		hook.teleport(target);
		Audio.GRAPPLE.playGlobal(player.getLocation());
		
		final int[] task = new int[] {0, 0};
		
		task[0] = pl.scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				if(!hook.getLocation().getBlock().getType().equals(Material.AIR) || !hook.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.AIR) || !hook.getLocation().add(0, 1, 0).getBlock().getType().equals(Material.AIR) || !hook.getLocation().add(1, 0, 0).getBlock().getType().equals(Material.AIR) || !hook.getLocation().add(-1, 0, 0).getBlock().getType().equals(Material.AIR) || !hook.getLocation().add(0, 0, 1).getBlock().getType().equals(Material.AIR) || !hook.getLocation().add(0, 0, -1).getBlock().getType().equals(Material.AIR))
				{
					hooked = hook.getLocation().clone();
					hooking = true;
					Audio.COMBAT_BLOCK.playGlobal(hooked);
					pl.cancelTask(task[0]);
				}
				
				else
				{
					target = target.subtract(0, 0.3, 0);
					hook.setVelocity(target.subtract(hook.getLocation()).toVector().normalize().multiply(hook.getLocation().distance(target) / 8));
				}
			}
		});
		
		task[1] = pl.scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				if(!hooking)
				{
					return;
				}
				
				if(unhooked)
				{
					pl.cancelTask(task[1]);
				}
				
				if(hook != null)
				{
					hook.teleport(hooked);
					
					Grapple.this.player.setVelocity(hook.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(player.getLocation().distance(hook.getLocation()) / 8));
				}
			}
		});
	}
	
	public void unhook()
	{
		unhooked = true;
		hook.setLeashHolder(null);
		hook.remove();
	}
}
