package com.glacialrush.api.game.handler;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.thread.GlacialThread;
import com.glacialrush.api.thread.ThreadMonitor;
import com.glacialrush.api.thread.ThreadState;

public class ThreadHandler extends GlacialHandler
{
	private GList<GlacialThread> threads;
	private ThreadMonitor monitor;
	private int task;
	
	public ThreadHandler(Game game)
	{
		super(game);
		
		threads = new GList<GlacialThread>();
		monitor = new ThreadMonitor();
		
		task = GlacialPlugin.instance().scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				monitor.preRun();
				
				GList<GlacialThread> thr = threads.copy();
				
				for(GlacialThread t : thr)
				{
					if(t.getState().equals(ThreadState.NEW) || t.getState().equals(ThreadState.RUNNING) || t.getState().equals(ThreadState.IDLE))
					{
						t.tick();
					}
					
					else
					{
						threads.remove(t);
					}
				}
				
				monitor.postRun();
			}
		});
	}
	
	public void stop()
	{
		GlacialPlugin.instance().cancelTask(task);
	}
	
	public void newThread(GlacialThread t)
	{
		threads.add(t);
	}
	
	public GList<GlacialThread> getThreads()
	{
		return threads;
	}
	
	public void setThreads(GList<GlacialThread> threads)
	{
		this.threads = threads;
	}
	
	public ThreadMonitor getMonitor()
	{
		return monitor;
	}
	
	public void setMonitor(ThreadMonitor monitor)
	{
		this.monitor = monitor;
	}
}
