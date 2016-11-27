package com.glacialrush.api.map;

import java.util.Iterator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.glacialrush.api.object.GList;
import com.glacialrush.xapi.Cuboid;

public class Chunklet
{
	protected int x;
	protected int z;
	protected World world;
	
	public Chunklet(int x, int z, World world)
	{
		this.x = x;
		this.z = z;
		this.world = world;
	}
	
	public Chunklet(Location location)
	{
		this.x = location.getBlockX() >> 2;
		this.z = location.getBlockZ() >> 2;
		this.world = location.getWorld();
	}
	
	public boolean contains(Chunk chunk)
	{
		return getMin().getChunk().equals(chunk);
	}
	
	public Location getMin()
	{
		return new Location(world, x << 2, 0, z << 2);
	}
	
	public Location getMax()
	{
		return new Location(world, (x << 2) + 3, 255, (z << 2) + 3);
	}
	
	public Iterator<Block> iterator()
	{
		return new Cuboid(getMin(), getMax()).iterator();
	}
	
	public Chunklet getRelative(Direction d)
	{
		return new Chunklet(x + d.x(), z + d.z(), world);
	}
	
	public GList<Chunklet> getNeighbors()
	{
		GList<Chunklet> n = new GList<Chunklet>();
		
		for(Direction d : Direction.news())
		{
			n.add(getRelative(d));
		}
		
		return n;
	}
	
	public GList<Chunklet> getCircle(int radius)
	{
		GList<Chunklet> n = new GList<Chunklet>();
		
		int x = radius;
		int z = 0;
		int xChange = 1 - (radius << 1);
		int zChange = 0;
		int radiusError = 0;
		
		while(x >= z)
		{
			for(int i = this.x - x; i <= this.x + x; i++)
			{
				n.add(new Chunklet(i, this.z + z, world));
				n.add(new Chunklet(i, this.z - z, world));
			}
			
			for(int i = this.x - z; i <= this.x + z; i++)
			{
				n.add(new Chunklet(i, this.z + x, world));
				n.add(new Chunklet(i, this.z - x, world));
			}
			
			z++;
			radiusError += zChange;
			zChange += 2;
			
			if(((radiusError << 1) + xChange) > 0)
			{
				x--;
				radiusError += xChange;
				xChange += 2;
			}
		}
		
		return n;
	}
	
	public boolean equals(Object object)
	{
		if(object == null)
		{
			return false;
		}
		
		if(object instanceof Chunklet)
		{
			Chunklet c = (Chunklet) object;
			if(c.x == x && c.z == z && c.world.equals(world))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public GList<Location> getBorder(int level, Direction d)
	{
		GList<Location> ls = new GList<Location>();
		Iterator<Block> it = new Cuboid(getMin(), getMax()).getFace(d.f()).flatten(level).iterator();

		while(it.hasNext())
		{
			ls.add(it.next().getLocation());
		}
		
		return ls;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getZ()
	{
		return z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}

	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}
}
