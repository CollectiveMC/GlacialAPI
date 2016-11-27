package com.glacialrush.api.security;

import org.bukkit.entity.Player;
import com.glacialrush.packet.NMS;

public class NetworkUtils
{
	public static String getAddress(Player player)
	{
		return player.getAddress().getAddress().getHostAddress();
	}
	
	public static boolean matches(Player a, Player b)
	{
		return getAddress(a).equals(getAddress(b));
	}
	
	public static long ping(Player p)
	{
		return NMS.ping(p);
	}
}
