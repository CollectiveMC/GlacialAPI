package com.glacialrush.api.thread;

public class ThreadMonitor
{
	private long ticks;
	private long active;
	private long activeTime;
	private long lastTick;
	private double tps;
	
	public ThreadMonitor()
	{
		ticks = 0;
		active = 0;
		activeTime = 0;
		lastTick = 0;
		tps = 0.0;
	}
	
	public void preRun()
	{
		lastTick = System.currentTimeMillis();
	}
	
	public void postRun()
	{
		long cms = System.currentTimeMillis();
		activeTime = cms - lastTick;
		active += activeTime;
		ticks++;
		tps = ((20 * 50) - (active / (ticks * 50))) / 50;
	}

	public long getTicks()
	{
		return ticks;
	}

	public void setTicks(long ticks)
	{
		this.ticks = ticks;
	}

	public long getActive()
	{
		return active;
	}

	public void setActive(long active)
	{
		this.active = active;
	}

	public long getActiveTime()
	{
		return activeTime;
	}

	public void setActiveTime(long activeTime)
	{
		this.activeTime = activeTime;
	}

	public long getLastTick()
	{
		return lastTick;
	}

	public void setLastTick(long lastTick)
	{
		this.lastTick = lastTick;
	}

	public double getTps()
	{
		return tps;
	}

	public void setTps(double tps)
	{
		this.tps = tps;
	}
}
