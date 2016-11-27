package com.glacialrush.api.game.object;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.entity.Player;
import org.phantomapi.world.PhantomWorldQueue;

public class Operation
{
	private Location location;
	private Material material;
	private Player player;
	private DyeColor color;
	private Byte data;
	
	public Operation(Location location, DyeColor color)
	{
		this.location = location;
		this.color = color;
	}
	
	public Operation(Location location, Material material)
	{
		this.location = location;
		this.material = material;
	}
	
	public Operation(Location location, Material material, Byte data)
	{
		this.location = location;
		this.material = material;
		this.data = data;
	}
	
	public Operation(Location location, Material material, Player player)
	{
		this.location = location;
		this.material = material;
		this.player = player;
	}
	
	public Operation(Location location, Material material, Byte data, Player player)
	{
		this.location = location;
		this.material = material;
		this.player = player;
		this.data = data;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public void setMaterial(Material material)
	{
		this.material = material;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public DyeColor getColor()
	{
		return color;
	}
	
	public void setColor(DyeColor color)
	{
		this.color = color;
	}
	
	@SuppressWarnings("deprecation")
	public void execute(PhantomWorldQueue queue)
	{
		if(material != null)
		{
			if(player != null)
			{
				if(data != null)
				{
					player.sendBlockChange(location, material, data);
				}
				
				else
				{
					player.sendBlockChange(location, material, (byte) 0);
				}
			}
			
			else
			{
				queue.set(location, material);
								
				if(data != null)
				{
					queue.set(location, material, data);
				}
			}
		}
		
		else if(color != null)
		{
			if(location.getBlock().getType().equals(Material.BANNER) || location.getBlock().getType().equals(Material.WALL_BANNER) || location.getBlock().getType().equals(Material.STANDING_BANNER))
			{
				Banner banner = (Banner) location.getBlock().getState();
				banner.setBaseColor(color);
				banner.update();
				
				return;
			}
			
			queue.set(location, queue.get(location).getMaterial(), color.getData());
		}
	}
}
