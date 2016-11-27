package com.glacialrush.api.game.data;

import java.io.Serializable;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.object.GList;

public class RegionData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private GList<ChunkletData> chunklets;
	private RegionType type;
	
	public RegionData(String name, GList<ChunkletData> chunklets, RegionType type)
	{
		this.name = name;
		this.chunklets = chunklets;
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public GList<ChunkletData> getChunklets()
	{
		return chunklets;
	}
	
	public void setChunklets(GList<ChunkletData> chunklets)
	{
		this.chunklets = chunklets;
	}

	public RegionType getType()
	{
		return type;
	}

	public void setType(RegionType type)
	{
		this.type = type;
	}
}
