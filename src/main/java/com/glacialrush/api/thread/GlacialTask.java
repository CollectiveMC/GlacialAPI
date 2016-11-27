package com.glacialrush.api.thread;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.InnerDispatcher;
import org.bukkit.ChatColor;

public class GlacialTask implements Runnable
{
	protected int pid;
	protected long delay;
	protected ThreadState state;
	protected long cycleTime;
	protected long cycles;
	protected long cmsa;
	protected String name;
	protected InnerDispatcher d;
	
	private static int npid = 0;
	
	public GlacialTask(String name)
	{
		this.delay = 0;
		this.name = name;
		this.state = ThreadState.NEW;
		this.pid = npid++;
		this.cycles = 0;
		this.cycleTime = 0;
		this.d = new InnerDispatcher(GlacialPlugin.instance(), getName());
		start();
	}
	
	public long getCmsa()
	{
		return cmsa;
	}
	
	public void setCmsa(long cmsa)
	{
		this.cmsa = cmsa;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setD(InnerDispatcher d)
	{
		this.d = d;
	}
	
	public static int getNpid()
	{
		return npid;
	}
	
	public static void setNpid(int npid)
	{
		GlacialTask.npid = npid;
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
	
	public void start()
	{
		this.state = ThreadState.RUNNING;
		s("Started Thread");
	}
	
	public void stop()
	{
		this.state = ThreadState.FINISHED;
		s("Stopped Thread " + ChatColor.LIGHT_PURPLE + cycleTime + "ms" + " " + cycles + "cy");
	}
	
	public void tick()
	{
		if(preRun())
		{
			run();
			postRun();
		}
	}
	
	@Override
	public void run()
	{
	
	}
	
	public boolean preRun()
	{
		if(delay > 0)
		{
			delay--;
			state = ThreadState.IDLE;
			return false;
		}
		
		else if(delay < 0)
		{
			delay = 0;
			return true;
		}
		
		else
		{
			state = ThreadState.RUNNING;
			cmsa = System.currentTimeMillis();
			return true;
		}
	}
	
	public void postRun()
	{
		if(state.equals(ThreadState.RUNNING))
		{
			cycleTime = System.currentTimeMillis() - cmsa;
			cycles++;
		}
	}
	
	public int getPid()
	{
		return pid;
	}
	
	public void setPid(int pid)
	{
		this.pid = pid;
	}
	
	public long getDelay()
	{
		return delay;
	}
	
	public void setDelay(long delay)
	{
		this.delay = delay;
	}
	
	public ThreadState getState()
	{
		return state;
	}
	
	public void setState(ThreadState state)
	{
		this.state = state;
	}
	
	public long getCycleTime()
	{
		return cycleTime;
	}
	
	public void setCycleTime(long cycleTime)
	{
		this.cycleTime = cycleTime;
	}
	
	public long getCycles()
	{
		return cycles;
	}
	
	public void setCycles(long cycles)
	{
		this.cycles = cycles;
	}
}
