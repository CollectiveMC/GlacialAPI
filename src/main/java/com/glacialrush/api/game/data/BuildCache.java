package com.glacialrush.api.game.data;

import java.io.Serializable;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GLocation;

public class BuildCache implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private GList<GLocation> captures;
	private GList<GLocation> accents;
	private GList<GLocation> spawns;
	private GList<GLocation> warpgates;
	
	public BuildCache(String name)
	{
		this.name = name;
		captures = new GList<GLocation>();
		accents = new GList<GLocation>();
		spawns = new GList<GLocation>();
		warpgates = new GList<GLocation>();
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setCaptures(GList<GLocation> captures)
	{
		this.captures = captures;
	}
	
	public void setAccents(GList<GLocation> accents)
	{
		this.accents = accents;
	}
	
	public void setSpawns(GList<GLocation> spawns)
	{
		this.spawns = spawns;
	}
	
	public void setWarpgates(GList<GLocation> warpgates)
	{
		this.warpgates = warpgates;
	}
	
	public String getName()
	{
		return name;
	}
	
	public GList<GLocation> getCaptures()
	{
		return captures;
	}
	
	public GList<GLocation> getAccents()
	{
		return accents;
	}
	
	public GList<GLocation> getSpawns()
	{
		return spawns;
	}
	
	public GList<GLocation> getWarpgates()
	{
		return warpgates;
	}
}
