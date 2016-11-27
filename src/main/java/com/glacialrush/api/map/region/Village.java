package com.glacialrush.api.map.region;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.object.Influenced;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.object.GMap;

public class Village extends LinkedRegion implements Influenced
{
	private GMap<Player, Integer> influence;
	
	public Village(Map map, String name, Game game)
	{
		super(map, name, RegionType.VILLAGE, game);
		
		influence = new GMap<Player, Integer>();
	}
	
	@Override
	public GMap<Player, Integer> getInfluenceMap()
	{
		return influence;
	}
	
	@Override
	public void resetInfluenceMap()
	{
		influence.clear();
	}
	
	@Override
	public void influence(Player p, Integer i)
	{
		influence.put(p, (influence.containsKey(p) ? influence.get(p) : 0) + i);
	}
	
	@Override
	public double getInfluence(Player p)
	{
		int ti = 0;
		
		for(Player i : influence.keySet())
		{
			ti += influence.get(i);
		}
		
		if(!influence.containsKey(p))
		{
			return 0.0;
		}
		
		return (double) ((double) influence.get(p) / (double) ti);
	}
	
	@Override
	public void injectInfluence(GMap<Player, Integer> influence)
	{
		for(Player p : influence.keySet())
		{
			influence(p, influence.get(p));
		}
	}
	
	@Override
	public GMap<Player, Integer> popInfluenceMap()
	{
		for(Region i : getBorders())
		{
			if(i.getType().equals(RegionType.TERRITORY))
			{
				injectInfluence(((Territory)i).popInfluenceMap());
			}
		}
		
		GMap<Player, Integer> ic = influence.copy();
		resetInfluenceMap();
		return ic;
	}
	
	public void pullInfluenceMap()
	{
		for(Region i : getBorders())
		{
			if(i.getType().equals(RegionType.TERRITORY))
			{
				injectInfluence(((Territory)i).popInfluenceMap());
			}
		}
	}
}
