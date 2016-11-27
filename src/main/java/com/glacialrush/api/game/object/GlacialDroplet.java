package com.glacialrush.api.game.object;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.ParticleEffect;

public class GlacialDroplet implements Droplet
{
	private Entity entity;
	private Long xp;
	private Experience rr;
	
	public GlacialDroplet(Entity entity, long xp, Experience rr)
	{
		this.rr = rr;
		this.entity = entity;
		this.xp = xp;
	}
	
	@Override
	public void setLocation(Location l)
	{
		entity.teleport(l);
	}

	@Override
	public Location getLocation()
	{
		return entity.getLocation();
	}

	@Override
	public void reward(Player p)
	{
		Audio.EXPERIENCE_EARN.playGlobal(p.getLocation());
		((RegionedGame)GlacialPlugin.instance().getGameControl().getGame(p)).getExperienceHandler().giveXp(p, xp, rr);
	}

	@Override
	public void animate()
	{
		ParticleEffect.FIREWORKS_SPARK.display(0.1f, 0.1f, 0.1f, 0.1f, 1, getLocation(), 16.0);
	}

	@Override
	public Entity getBoundEntity()
	{
		return entity;
	}
}
