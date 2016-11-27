package com.glacialrush.api.game.event;

import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.map.region.Territory;

public class TerritoryCaptureEvent extends TerritoryEvent
{
	private final Faction faction;
	
	public TerritoryCaptureEvent(Territory territory, Faction faction)
	{
		super(territory);
		
		this.faction = faction;
	}

	public Faction getFaction()
	{
		return faction;
	}
}
