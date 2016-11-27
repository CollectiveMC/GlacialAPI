package com.glacialrush.api.game.data;

import java.io.Serializable;
import com.glacialrush.api.object.GList;

public class MapData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private GList<RegionData> regions;
	private Boolean locked;
	private String world;
	private MapConfiguration config;
	
	public MapData(String name, GList<RegionData> regions, Boolean locked, String world)
	{
		this.name = name;
		this.regions = regions;
		this.world = world;
		this.locked = locked;
		this.config = new MapConfiguration();
	}
	
	public MapConfiguration getConfig()
	{
		return config;
	}
	
	public void setConfig(MapConfiguration config)
	{
		this.config = config;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public GList<RegionData> getRegions()
	{
		return regions;
	}
	
	public void setRegions(GList<RegionData> regions)
	{
		this.regions = regions;
	}
	
	public Boolean getLocked()
	{
		return locked;
	}
	
	public void setLocked(Boolean locked)
	{
		this.locked = locked;
	}
	
	public String getWorld()
	{
		return world;
	}
	
	public void setWorld(String world)
	{
		this.world = world;
	}
}
