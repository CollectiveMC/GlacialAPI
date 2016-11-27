package com.glacialrush.api.teleport;

public class TeleportTick implements Runnable
{
	private int ticks;
	
	public void run(int ticks)
	{
		this.ticks = ticks;
		run();
	}

	@Override
	public void run()
	{
		
	}

	public int getTicks()
	{
		return ticks;
	}
}
