package com.glacialrush.api.game.object;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.object.GList;

public class SpectatePath
{
	protected final Player p;
	protected final GList<Location> points;
	protected final GList<Location> targets;
	protected Integer pointed;
	protected Integer task;
	protected LivingEntity entity;
	protected World world;
	
	public SpectatePath(Player p)
	{
		this.p = p;
		this.points = new GList<Location>();
		this.targets = new GList<Location>();
		this.pointed = 0;
		this.world = p.getWorld();
	}
	
	public void addPoint(Location location)
	{
		points.add(location);
	}
	
	public void addTarget(Location location)
	{
		targets.add(location);
	}
	
	public void commit()
	{
		p.setGameMode(GameMode.SPECTATOR);
		p.teleport(points.get(0));
		entity = (LivingEntity) world.spawnEntity(p.getLocation(), EntityType.PIG);
		p.setSpectatorTarget(entity);
		
		task = GlacialPlugin.instance().scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			public void run()
			{
				if(pointed + 1 > points.size())
				{
					GlacialPlugin.instance().cancelTask(task);
					entity.remove();
					return;
				}
				
				Location c = entity.getLocation().clone();
				Location t = points.get((pointed + 2 > points.size()) ? pointed : (pointed + 1)).clone();
				Location l = closestTarget();
				Vector v = entity.getVelocity().clone().add(t.clone().subtract(c).toVector().normalize()).normalize();
				
				if(l != null)
				{
					Location tx = lookAt(entity.getLocation(), l);
					Location cx = entity.getLocation().clone();
					
					cx.setYaw(tx.getYaw());
					cx.setPitch(tx.getPitch());
					entity.teleport(cx);
					entity.setVelocity(v);
				}
				
				else
				{
					entity.setVelocity(v);
				}
				
				if(c.distance(t) < 1.5)
				{
					pointed++;
					
					if(pointed + 1 > points.size())
					{
						GlacialPlugin.instance().cancelTask(task);
						entity.remove();
					}
				}
			}
		});
	}
	
	public Location closestTarget()
	{
		Location t = null;
		Double minDist = Double.MAX_VALUE;
		
		for(Location i : targets)
		{
			double dist = i.distanceSquared(p.getLocation());
			
			if(dist < minDist)
			{
				minDist = dist;
				t = i.clone();
			}
		}
		
		return t;
	}
	
	public Location lookAt(Location loc, Location lookat)
	{
		loc = loc.clone();
		
		double dx = lookat.getX() - loc.getX();
		double dy = lookat.getY() - loc.getY();
		double dz = lookat.getZ() - loc.getZ();
		
		if(dx != 0)
		{
			if(dx < 0)
			{
				loc.setYaw((float) (1.5 * Math.PI));
			}
			else
			{
				loc.setYaw((float) (0.5 * Math.PI));
			}
			loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
		}
		else if(dz < 0)
		{
			loc.setYaw((float) Math.PI);
		}
		
		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
		
		loc.setPitch((float) -Math.atan(dy / dxz));
		
		loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
		loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);
		
		return loc;
	}
}
