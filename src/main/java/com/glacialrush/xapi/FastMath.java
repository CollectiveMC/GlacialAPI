package com.glacialrush.xapi;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import com.glacialrush.api.object.GList;

public class FastMath
{
	public static boolean isInRadius(Location a, Location b, float f)
	{
		return a.distanceSquared(b) <= f * f;
	}
	
	public static GList<Location> circlePoints(int points, double radius, Location center)
	{
		GList<Location> pointx = new GList<Location>();
		double slice = 2 * Math.PI / points;
		
		for(int i = 0; i < points; i++)
		{
			double angle = slice * i;
			int newX = (int) (center.getX() + radius * Math.cos(angle));
			int newZ = (int) (center.getZ() + radius * Math.sin(angle));
			pointx.add(new Location(center.getWorld(), newX, center.getY(), newZ));
		}
		
		return pointx;
	}
	
	public static boolean los(Location a, Location b)
	{
		if(!a.getWorld().equals(b.getWorld()))
		{
			return false;
		}
		
		if(!isInRadius(a, b, 256))
		{
			return false;
		}
		
		Vector v = b.toVector().subtract(a.toVector()).normalize();
		Double d = a.distance(b);
		Location c = a.clone();
		
		for(int i = 0; i < d + 1; i++)
		{
			c.add(v);
			
			if(!transWide(c.getBlock()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean losFAT(Location a, Location b)
	{
		if(!a.getWorld().equals(b.getWorld()))
		{
			return false;
		}
		
		if(!isInRadius(a, b, 256))
		{
			return false;
		}
		
		Vector v = b.toVector().subtract(a.toVector()).normalize();
		Double d = a.distance(b);
		Location c = a.clone();
		int h = 0;
		
		for(int i = 0; i < d + 1; i++)
		{
			c.add(v);
			
			if(!trans(c.getBlock()))
			{
				h++;
			}
		}
		
		if(h > 5)
		{
			return false;
		}
		
		return true;
	}
	
	public static boolean losStrict(Location a, Location b)
	{
		if(!a.getWorld().equals(b.getWorld()))
		{
			return false;
		}
		
		if(!isInRadius(a, b, 256))
		{
			return false;
		}
		
		Vector v = b.toVector().subtract(a.toVector()).normalize();
		Double d = a.distance(b);
		Location c = a.clone();
		
		for(int i = 0; i < d + 1; i++)
		{
			c.add(v);
			
			if(!trans(c.getBlock()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean transWide(Block block)
	{
		if(trans(block) || trans(block.getRelative(BlockFace.UP)) || trans(block.getRelative(BlockFace.DOWN)) || trans(block.getRelative(BlockFace.SOUTH)) || trans(block.getRelative(BlockFace.NORTH)) || trans(block.getRelative(BlockFace.EAST)) || trans(block.getRelative(BlockFace.WEST)))
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean trans(Block block)
	{
		if(!block.getType().equals(Material.AIR) && !block.getType().equals(Material.TORCH) && !block.getType().equals(Material.GLASS) && !block.getType().equals(Material.STAINED_GLASS) && !block.getType().equals(Material.STAINED_GLASS_PANE) && !block.getType().equals(Material.THIN_GLASS) && !block.getType().equals(Material.REDSTONE_TORCH_OFF) && !block.getType().equals(Material.REDSTONE_TORCH_ON) && !block.getType().equals(Material.ICE) && !block.getType().equals(Material.IRON_BARDING) && !block.getType().equals(Material.FENCE) && !block.getType().equals(Material.FENCE_GATE) && !block.getType().equals(Material.WATER) && !block.getType().equals(Material.STATIONARY_WATER))
		{
			return false;
		}
		
		return true;
	}
	
	public static double distance2D(Location a, Location b)
	{
		return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow((a.getZ() - b.getZ()), 2));
	}
}
