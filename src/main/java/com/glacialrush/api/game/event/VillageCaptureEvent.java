package com.glacialrush.api.game.event;

import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.map.region.Village;

public class VillageCaptureEvent extends VillageEvent
{
	private final Faction faction;
	
	public VillageCaptureEvent(Village village, Faction faction)
	{
		super(village);
		
		this.faction = faction;
	}

	public Faction getFaction()
	{
		return faction;
	}
}
