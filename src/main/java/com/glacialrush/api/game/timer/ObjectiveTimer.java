package com.glacialrush.api.game.timer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.object.GMap;
import com.glacialrush.xapi.FastMath;

public class ObjectiveTimer extends GameTimer
{
	private GMap<Player, Location> locations;
	
	public ObjectiveTimer(RegionedGame g)
	{
		super(g, 20, "ObjectiveTimer");
		
		locations = new GMap<Player, Location>();
	}
	
	public void tick()
	{
		g.getMapHandler().updateObjectives();
		
		for(Player i : g.players())
		{
			if(!locations.containsKey(i))
			{
				locations.put(i, i.getLocation());
			}
			
			if(i.getLocation().getWorld().equals(locations.get(i).getWorld()))
			{
				double dist = FastMath.distance2D(i.getLocation(), locations.get(i));
				Statistic.MOVEMENT_BLOCKS.add(i, dist);
				locations.put(i, i.getLocation());
			}
		}
	}
}
