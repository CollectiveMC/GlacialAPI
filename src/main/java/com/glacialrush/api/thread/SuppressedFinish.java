package com.glacialrush.api.thread;

import com.glacialrush.api.GPlugin;

public class SuppressedFinish implements Runnable
{
	private GPlugin pl;
	private int cycles;
	private long ms;
	
	public void run(GPlugin pl, int cycles, long ms)
	{
		this.pl = pl;
		this.cycles = cycles;
		this.ms = ms;
		run();
	}
	
	public int getCycles()
	{
		return cycles;
	}
	
	public long getMs()
	{
		return ms;
	}
	
	public GPlugin pl()
	{
		return pl;
	}
	
	@Override
	public void run()
	{
	
	}
}
