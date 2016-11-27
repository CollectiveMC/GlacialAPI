package com.glacialrush.api.component;

import org.bukkit.Bukkit;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.thread.GlacialTask;
import com.glacialrush.api.thread.TPS;
import com.glacialrush.api.thread.ThreadState;

public class ThreadComponent extends Controller
{
	private GList<GlacialTask> tasks;
	private Integer tid;
	private long cycles;
	private long cycleTime;
	
	public ThreadComponent(GlacialPlugin pl)
	{
		super(pl);
		
		tasks = new GList<GlacialTask>();
	}
	
	public void postEnable()
	{
	    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new TPS(), 100L, 1L);
		
		tid = pl.scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				long msa = System.currentTimeMillis();
				
				for(GlacialTask i : tasks.toArray(new GlacialTask[tasks.size()]))
				{
					if(i.getState().equals(ThreadState.RUNNING))
					{
						long cycleTimez = System.currentTimeMillis();
						i.run();
						i.setCycleTime(i.getCycleTime() + (System.currentTimeMillis() - cycleTimez));
						i.setCycles(i.getCycles() + 1);
					}
				}
				
				cycleTime = System.currentTimeMillis() - msa;
				cycles++;
			}
		});
	}
	
	public void preDisable()
	{
		for(GlacialTask i : tasks)
		{
			pl.v("Stopping Task: " + i.getPid());
			i.stop();
		}
	}
	
	public void postDisable()
	{
		pl.cancelTask(tid);
	}

	public GList<GlacialTask> getTasks()
	{
		return tasks;
	}

	public void setTasks(GList<GlacialTask> tasks)
	{
		this.tasks = tasks;
	}
	
	public void addTask(GlacialTask runnable)
	{
		tasks.add(runnable);
	}

	public Integer getTid()
	{
		return tid;
	}

	public void setTid(Integer tid)
	{
		this.tid = tid;
	}

	public long getCycles()
	{
		return cycles;
	}

	public void setCycles(long cycles)
	{
		this.cycles = cycles;
	}

	public long getCycleTime()
	{
		return cycleTime;
	}

	public void setCycleTime(long cycleTime)
	{
		this.cycleTime = cycleTime;
	}
}
