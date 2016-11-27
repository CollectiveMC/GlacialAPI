package com.glacialrush.api.util;

import org.bukkit.World;

public class TimeUtil
{
	public static boolean isNight(World w)
	{
		return w.getTime() > 1300;
	}
}
