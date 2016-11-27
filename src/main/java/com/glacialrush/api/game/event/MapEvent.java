package com.glacialrush.api.game.event;

import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.map.Map;

public class MapEvent extends GameEvent
{
	private final Map map;
	
	public MapEvent(Map map)
	{
		super((RegionedGame) map.getGame());
		
		this.map = map;
	}

	public Map getMap()
	{
		return map;
	}
}
