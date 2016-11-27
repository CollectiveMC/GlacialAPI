package com.glacialrush.api.game.handler;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.object.Droplet;
import com.glacialrush.api.game.object.GlacialDroplet;
import com.glacialrush.api.object.GList;

public class DropletHandler extends GlacialHandler
{
	private GList<Droplet> droplets;
	private int dropped;
	
	public DropletHandler(Game game)
	{
		super(game);
		
		dropped = 0;
		droplets = new GList<Droplet>();
	}
	
	public void dropSomething(Location l, long xp, Experience rr)
	{
		if(dropped > 8)
		{
			return;
		}
		
		ItemStack isx = new ItemStack(Material.NETHER_STAR);
		ItemMeta im = isx.getItemMeta();
		im.setDisplayName(UUID.randomUUID().toString());
		isx.setItemMeta(im);
		Entity e = l.getWorld().dropItem(l, isx);
		e.setVelocity(new Vector(Math.random() - 0.5, Math.random(), Math.random() - 0.5));
		GlacialDroplet g = new GlacialDroplet(e, xp, rr);
		droplets.add(g);
	}
	
	public void dropSomethings(Location l, long xp, Experience rr, int k)
	{
		if(k > 4)
		{
			k = 4;
		}
		
		for(int i = 0; i < k; i++)
		{
			dropSomething(l, 1 + (long) (Math.random() * xp), rr);
		}
	}
	
	public void start(GameState state)
	{
		
	}
	
	public void stop(GameState state)
	{
		for(Droplet i : droplets)
		{
			i.getBoundEntity().remove();
		}
		
		droplets.clear();
	}
	
	public void tick(GameState state)
	{
		for(Droplet i : droplets.copy())
		{
			if(i.getBoundEntity().isDead())
			{
				droplets.remove(i);
			}
			
			if(i.getBoundEntity().getTicksLived() > 170)
			{
				i.getBoundEntity().remove();
			}
		}
		
		dropped = 0;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void on(PlayerPickupItemEvent e)
	{
		boolean remmed = false;
		
		for(Droplet i : droplets.copy())
		{
			if(i.getBoundEntity().getEntityId() == e.getItem().getEntityId())
			{
				i.reward(e.getPlayer());
				e.getItem().remove();
				droplets.remove(i);
				remmed = true;
			}
		}
		
		if(!remmed)
		{
			e.getItem().remove();
		}
	}
}
