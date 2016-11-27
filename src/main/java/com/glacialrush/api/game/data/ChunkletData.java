package com.glacialrush.api.game.data;

import java.io.Serializable;

public class ChunkletData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int x;
	private int z;
	
	public ChunkletData(int x, int z)
	{
		this.x = x;
		this.z = z;
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
}
