package com.glacialrush.api.util;

import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;

public class FactionComparator
{
	public static Faction getMin(GMap<Faction, Integer> map)
	{
		int min = Integer.MAX_VALUE;
		Faction m = null;
		
		for(Faction i : map.keySet())
		{
			if(map.get(i) < min)
			{
				min = map.get(i);
				m = i;
			}
		}
		
		return m;
	}
	
	public static Faction getMax(GMap<Faction, Integer> map)
	{
		int max = Integer.MIN_VALUE;
		Faction m = null;
		
		for(Faction i : map.keySet())
		{
			if(map.get(i) > max)
			{
				max = map.get(i);
				m = i;
			}
		}
		
		return m;
	}
	
	public static Faction getMid(GMap<Faction, Integer> map3)
	{
		if(map3.size() != 3)
		{
			return null;
		}
		
		GMap<Faction, Integer> map = map3.copy();
		
		map.remove(getMin(map3));
		map.remove(getMax(map3));
		
		return new GList<Faction>(map.keySet()).get(0);
	}
}
