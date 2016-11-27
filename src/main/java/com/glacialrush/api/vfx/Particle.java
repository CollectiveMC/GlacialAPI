package com.glacialrush.api.vfx;

import org.bukkit.Location;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.xapi.Area;

public enum Particle
{
	DEATH(ParticleEffect.SMOKE_NORMAL, 2.4, 32.0, 16, 4);
	
	private ParticleEffect effect;
	private Double area;
	private Double range;
	private Integer ammount;
	private Integer particlesPerTick;
	
	private Particle(ParticleEffect effect, Double area, Double range, Integer ammount, Integer particlesPerTick)
	{
		this.effect = effect;
		this.area = area;
		this.range = range;
		this.ammount = ammount;
		this.particlesPerTick = particlesPerTick;
	}
	
	public void play(Location location)
	{
		int rm = ammount;
		final int[] mx = new int[1];
		final Area a = new Area(location, area);
				
		mx[0] = GlacialPlugin.instance().scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				for(int i = 0; i < particlesPerTick; i++)
				{
					if(rm <= 0)
					{
						GlacialPlugin.instance().cancelTask(mx[0]);
						return;
					}
					
					ammount--;
					effect.display(0f, 0f, 0f, 1f, 1, a.random(), range);
				}
			}
		});
	}
	
	public ParticleEffect getEffect()
	{
		return effect;
	}

	public Double getArea()
	{
		return area;
	}

	public Double getRange()
	{
		return range;
	}

	public Integer getAmmount()
	{
		return ammount;
	}

	public Integer getParticlesPerTick()
	{
		return particlesPerTick;
	}
}
