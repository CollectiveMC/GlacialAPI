package com.glacialrush.api.thread;

import java.util.Iterator;
import com.glacialrush.api.GPlugin;
import com.glacialrush.api.object.GList;

public class SuppressedTask<T>
{
	private final long limit;
	private final int[] task;
	private final Iterator<T> iterator;
	private final SuppressedRunnable<T> runnable;
	private final SuppressedFinish finish;
	private final GPlugin pl;
	private final Integer size;
	
	private int cycles;
	private long tms;
	
	public SuppressedTask(GPlugin pl, GList<T> set, long limit, SuppressedRunnable<T> runnable, SuppressedFinish finish)
	{
		this.iterator = set.iterator();
		this.limit = Math.abs(limit);
		this.runnable = runnable;
		this.size = set.size();
		this.finish = finish;
		this.pl = pl;
		this.task = new int[] {0};
		
		this.cycles = 0;
		this.tms = 0l;
	}
	
	public void start()
	{
		task[0] = pl.scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				long ms = System.currentTimeMillis();
				long cms = 0l;
				
				while(cms < limit)
				{
					if(iterator.hasNext())
					{
						runnable.run(pl, iterator.next(), (int) (100 * ((double) cycles / (double) size)));
						cms = System.currentTimeMillis() - ms;
						cycles++;
					}
					
					else
					{
						pl.cancelTask(task[0]);
						tms += cms;
						onStop();
						return;
					}
				}
				
				if(!iterator.hasNext())
				{
					pl.cancelTask(task[0]);
					tms += cms;
					onStop();
					return;
				}
				
				else
				{
					tms += cms;
				}
			}
		});
	}
	
	public void onStop()
	{
		if(finish != null)
		{
			finish.run(pl, cycles, tms);
		}
	}
}
