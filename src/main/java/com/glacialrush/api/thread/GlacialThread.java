package com.glacialrush.api.thread;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.InnerDispatcher;
import org.bukkit.ChatColor;

public class GlacialThread implements Runnable
{
	private String name;
	private ThreadState state;
	private ThreadMonitor monitor;
	private long delay;
	private InnerDispatcher d;
	
	public GlacialThread(String name)
	{
		this.name = name;
		this.monitor = new ThreadMonitor();
		this.state = ThreadState.NEW;
		this.delay = 0;
		this.d = new InnerDispatcher(GlacialPlugin.instance(), ChatColor.RED + "THREAD " + ChatColor.YELLOW + getName());
	}
	
	public void tick()
	{
		delay = Math.abs(delay);
		
		if(delay > 0)
		{
			delay--;
			state = ThreadState.IDLE;
			return;
		}
		
		state = ThreadState.RUNNING;
		monitor.preRun();
		run();
		monitor.postRun();
	}
	
	public void stop()
	{
		state = ThreadState.FINISHED;
	}
	
	public void delay(long ticks)
	{
		delay += Math.abs(ticks);
	}
	
	@Override
	public void run()
	{
		
	}

	public String getName()
	{
		return name;
	}

	public ThreadState getState()
	{
		return state;
	}

	public ThreadMonitor getMonitor()
	{
		return monitor;
	}

	public long getDelay()
	{
		return delay;
	}
	
	public InnerDispatcher getDispatcher()
	{
		return d;
	}
	
	public void i(String... o)
	{
		d.info(o);
	}
	
	public void s(String... o)
	{
		d.success(o);
	}
	
	public void f(String... o)
	{
		d.failure(o);
	}
	
	public void w(String... o)
	{
		d.warning(o);
	}
	
	public void v(String... o)
	{
		d.verbose(o);
	}
	
	public void o(String... o)
	{
		d.overbose(o);
	}
	
	public void si(String... o)
	{
		d.sinfo(o);
	}
	
	public void ss(String... o)
	{
		d.ssuccess(o);
	}
	
	public void sf(String... o)
	{
		d.sfailure(o);
	}
	
	public void sw(String... o)
	{
		d.swarning(o);
	}
	
	public void sv(String... o)
	{
		d.sverbose(o);
	}
	
	public void so(String... o)
	{
		d.soverbose(o);
	}
}
