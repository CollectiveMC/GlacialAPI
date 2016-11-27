package com.glacialrush.api.vfx;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import com.glacialrush.api.GlacialPlugin;

public enum VFX
{
	PULSE_UP(new VFXRunnable()
	{
		public void run()
		{
			final int[] t = new int[]{0, 0};
			final Location[] l = new Location[]{getLocation().clone()};
			
			t[0] = pl().scheduleSyncRepeatingTask(0, 0, new Runnable()
			{
				public void run()
				{
					t[1]++;
					
					Random r = new Random();
					Vector v = new Vector(r.nextDouble() - r.nextDouble(), 0.7, r.nextDouble() - r.nextDouble());
					
					l[0] = l[0].clone().add(v);
					
					draw(l[0]);
					
					if(t[1] > 64)
					{
						pl().cancelTask(t[0]);
					}
				}
			});
		}
	});
	
	private VFXRunnable runnable;
	
	private VFX(VFXRunnable runnable)
	{
		this.runnable = runnable;
	}
	
	public void play(GlacialPlugin pl, Location location, ParticleEffect effect)
	{
		getRunnable().run(pl, location, effect);
	}
	
	public VFXRunnable getRunnable()
	{
		return runnable;
	}
}
