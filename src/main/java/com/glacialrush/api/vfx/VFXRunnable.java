package com.glacialrush.api.vfx;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.object.GList;

public class VFXRunnable implements Runnable
{
	private Location location;
	private ParticleEffect effect;
	private GlacialPlugin pl;
	
	public void run(GlacialPlugin pl, Location location, ParticleEffect effect)
	{
		this.pl = pl;
		this.location = location;
		this.effect = effect;
	}
	
	@Override
	public void run()
	{
		
	}

	public void draw(Location location)
	{
		GList<Player> players = new GList<Player>();
		
		for(Player i : pl.onlinePlayers())
		{
			if(pl.gpd(i).getParticles())
			{
				players.add(i);
			}
		}
		
		effect.display(0, 0, 0, 0, 1, location, players);
	}
	
	public GlacialPlugin pl()
	{
		return pl;
	}
	
	public Location getLocation()
	{
		return location;
	}

	public ParticleEffect getEffect()
	{
		return effect;
	}
}
