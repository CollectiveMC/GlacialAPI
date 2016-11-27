package com.glacialrush.api.game.handler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.event.CombatEvent;

public class SecurityHandler extends GlacialHandler
{	
	public SecurityHandler(Game game)
	{
		super(game);
	}
	
	@EventHandler
	public void onAntiKB(CombatEvent e)
	{
		Location l = e.getPlayer().getLocation().clone();
		Vector v = e.getDamager().getLocation().getDirection().clone().add(new Vector(0, 0.6, 0));
		
		game.pl().scheduleSyncTask(5, new Runnable()
		{
			@Override
			public void run()
			{
				if(l.add(0, 2, 0).getBlock().getType().equals(Material.AIR) && l.distance(e.getPlayer().getLocation()) < 0.3)
				{
					e.getPlayer().teleport(l.add(v));
				}
			}
		});
	}
	
	public void tick(GameState state)
	{
		
	}
}
