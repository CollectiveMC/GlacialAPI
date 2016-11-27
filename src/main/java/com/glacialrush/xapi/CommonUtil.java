package com.glacialrush.xapi;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.Date;
import com.glacialrush.api.object.GList;

public class CommonUtil
{
	public static String date()
	{
		return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date());
	}
	
	public static String address(InetSocketAddress address)
	{
		byte[] raw = address.getAddress().getAddress();
		GList<Integer> ipBuilder = new GList<Integer>();
		
		for(byte i : raw)
		{
			ipBuilder.add((int)i);
		}
		
		return ipBuilder.toString(".");
	}
}
