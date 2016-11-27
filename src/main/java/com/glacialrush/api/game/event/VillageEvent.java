package com.glacialrush.api.game.event;

import com.glacialrush.api.map.region.Village;

public class VillageEvent extends RegionEvent
{
	private final Village village;
	
	public VillageEvent(Village village)
	{
		super(village);
		
		this.village = village;
	}

	public Village getVillage()
	{
		return village;
	}
}
