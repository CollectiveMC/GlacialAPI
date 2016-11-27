package com.glacialrush.api.map;

import org.bukkit.Material;

public enum RegionType
{
	TERRITORY(Material.REDSTONE_BLOCK),
	VILLAGE(Material.GOLD_BLOCK),
	SCENERY(Material.IRON_BLOCK),
	EDGE(Material.COAL_BLOCK);
	
	private Material material;
	
	private RegionType(Material material)
	{
		this.material = material;
	}
	
	public Material material()
	{
		return material;
	}
}
