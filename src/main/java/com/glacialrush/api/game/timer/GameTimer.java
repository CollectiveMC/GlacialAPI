package com.glacialrush.api.game.timer;

import com.glacialrush.api.dispatch.InnerDispatcher;
import com.glacialrush.api.game.RegionedGame;

public class GameTimer implements Timer
{
	protected final long tickreval;
	protected final InnerDispatcher d;
	protected final RegionedGame g;
	
	public GameTimer(RegionedGame g, long tickreval, String name)
	{
		this.tickreval = tickreval;
		this.g = g;
		this.d = new InnerDispatcher(g.pl(), name);
		
		g.getGameStateHandler().registerGameTimer(this);
	}
	
	public void tick()
	{
		
	}
	
	public long tickreval()
	{
		return tickreval;
	}
}
