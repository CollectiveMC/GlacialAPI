package com.glacialrush.api.game.event;

import com.glacialrush.api.map.Region;

public class RegionEvent extends MapEvent
{
	private final Region region;
	
	public RegionEvent(Region region)
	{
		super(region.getMap());
		
		this.region = region;
	}
	
	public Region getRegion()
	{
		return region;
	}
}
