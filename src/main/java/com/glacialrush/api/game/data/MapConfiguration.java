package com.glacialrush.api.game.data;

import java.io.Serializable;

public class MapConfiguration implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	boolean useStainedClay;
	
	public MapConfiguration()
	{
		useStainedClay = true;
	}

	public boolean isUseStainedClay()
	{
		return useStainedClay;
	}

	public void setUseStainedClay(boolean useStainedClay)
	{
		this.useStainedClay = useStainedClay;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
}
