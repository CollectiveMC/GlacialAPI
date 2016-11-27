package com.glacialrush.api.game.event;

import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.Faction;

public class CapturePointEvent extends TerritoryCaptureEvent
{
	private final Capture capture;
	
	public CapturePointEvent(Capture capture, Faction faction)
	{
		super(capture.getTerritory(), faction);
		
		this.capture = capture;
	}

	public Capture getCapture()
	{
		return capture;
	}
}
