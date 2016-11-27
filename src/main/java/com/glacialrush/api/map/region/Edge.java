package com.glacialrush.api.map.region;

import com.glacialrush.api.game.Game;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;

public class Edge extends Region
{
	public Edge(Map map, String name, Game game)
	{
		super(map, name, RegionType.EDGE, game);
	}
}
