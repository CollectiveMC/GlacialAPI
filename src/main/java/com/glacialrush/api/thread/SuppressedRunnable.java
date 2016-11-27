package com.glacialrush.api.thread;

import com.glacialrush.api.GPlugin;

public class SuppressedRunnable<T> implements Runnable
{
	private T t;
	private int percent;
	private GPlugin pl;
	
	public GPlugin pl()
	{
		return pl;
	}
	
	public T next()
	{
		return t;
	}
	
	public int percent()
	{
		return percent;
	}
	
	public void run(GPlugin pl, T t, int percent)
	{
		this.pl = pl;
		this.t = t;
		this.percent = percent;
		run();
	}
	
	@Override
	public void run()
	{
		
	}
}
