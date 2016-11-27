package com.glacialrush.api.game.event;

import com.glacialrush.api.map.region.Territory;

public class TerritoryEvent extends RegionEvent
{
	private final Territory territory;
	
	public TerritoryEvent(Territory territory)
	{
		super(territory);
		
		this.territory = territory;
	}

	public Territory getTerritory()
	{
		return territory;
	}
}
