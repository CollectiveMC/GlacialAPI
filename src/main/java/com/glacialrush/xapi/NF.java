package com.glacialrush.xapi;

import java.text.NumberFormat;

public class NF
{
	public static String a(double i)
	{
		double d = i;
		String a = "";
		
		if(d / 1000 >= 1 || d / 1000 <= -1)
		{
			a = "K";
			d /= 1000;
		}
		
		else if(d / 1000000 >= 1 || d / 1000000 <= -1)
		{
			a = "M";
			d /= 1000000;
		}
		
		else if(d / 1000000000 >= 1 || d / 1000000000 <= -1)
		{
			a = "B";
			d /= 1000000000;
		}
		
		return d + a;
	}
	
	public static String a(int i)
	{
		return a((double)i);
	}
	
	public static String a(long i)
	{
		return a((double)i);
	}
	
	public static String c(double i)
	{
		return NumberFormat.getInstance().format(i);
	}
	
	public static String c(int i)
	{
		return a((double)i);
	}
	
	public static String c(long i)
	{
		return a((double)i);
	}
}
