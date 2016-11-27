package com.glacialrush.api.game.object;

import java.util.Iterator;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.phantomapi.world.PhantomWorldQueue;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.thread.GlacialThread;

public class Job
{
	private String name;
	private GList<Operation> operations;
	private Game game;
	
	public Job(String name, Game game)
	{
		this.name = name;
		this.operations = new GList<Operation>();
		this.game = game;
	}
	
	public void queue(Operation o)
	{
		operations.add(o);
	}
	
	public void queue(Location location, DyeColor color)
	{
		queue(new Operation(location, color));
	}
	
	public void queue(Location location, Material material)
	{
		queue(new Operation(location, material));
	}
	
	public void queue(Location location, Material material, Player player)
	{
		queue(new Operation(location, material, player));
	}
	
	public void queue(Location location, Material material, Byte data)
	{
		queue(new Operation(location, material, data));
	}
	
	public void queue(Location location, Material material, Byte data, Player player)
	{
		queue(new Operation(location, material, data, player));
	}
	
	public void flush()
	{
		flush(null);
	}
	
	public void flush(final Runnable whenComplete)
	{
		PhantomWorldQueue queue = new PhantomWorldQueue();
		if(game.getGameController().isBlocking())
		{
			for(Operation o : operations)
			{
				o.execute(queue);
			}
						
			operations.clear();
			queue.flush();

			if(whenComplete != null)
			{
				whenComplete.run();
			}
			
			return;
		}
		
		final Iterator<Operation> it = operations.iterator();
		final int[] prog = new int[]{0, 0};
		prog[0] = operations.size();
		
		game.getThreadHandler().newThread(new GlacialThread("Job: " + name)
		{
			@Override
			public void run()
			{
				long ms = System.currentTimeMillis();
				long tms = 0;
				
				while(it.hasNext() && tms < 30)
				{
					for(int i = 0; i < 32; i++)
					{
						if(it.hasNext())
						{
							it.next().execute(queue);
							prog[1]++;
							int pr = (int) (100 * (double) ((double) prog[1] / (double) prog[0]));
							game.getGameController().getBuildGame().updateJobStatus(Job.this, pr);
							
							it.remove();
						}
						
						else
						{
							break;
						}
					}
					
					tms += System.currentTimeMillis() - ms;
				}
				
				if(!it.hasNext())
				{
					stop();
					game.getGameController().getBuildGame().finishJob(Job.this);
					queue.flush();
					
					if(whenComplete != null)
					{
						whenComplete.run();
					}
				}
			}
		});
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public GList<Operation> getOperations()
	{
		return operations;
	}
	
	public void setOperations(GList<Operation> operations)
	{
		this.operations = operations;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public void setGame(Game game)
	{
		this.game = game;
	}
}
