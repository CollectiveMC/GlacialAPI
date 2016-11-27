package com.glacialrush.xapi;

import org.apache.commons.lang3.StringUtils;

public enum NumberPlace
{
	HUNDRED(101l), THOUSAND(1000l), MILLION(1000000l), BILLION(1000000000l), TRILLION(1000000000000l);
	
	private long splitter;
	
	private NumberPlace(long splitter)
	{
		this.splitter = splitter;
	}
	
	public long splitter()
	{
		return splitter;
	}
	
	public NumberPlace lower()
	{
		if(splitter() == 101l)
		{
			return null;
		}
		
		if(splitter() == 1000l)
		{
			return HUNDRED;
		}
		
		if(splitter() == 1000000l)
		{
			return THOUSAND;
		}
		
		if(splitter() == 1000000000l)
		{
			return MILLION;
		}
		
		if(splitter() == 1000000000000l)
		{
			return BILLION;
		}
		
		return null;
	}
	
	public String fname()
	{
		return StringUtils.capitalize(toString().toLowerCase());
	}
	
	public static NumberPlace htl(double dx)
	{
		long d = 101l;
		NumberPlace n = null;
		
		for(NumberPlace i : NumberPlace.values())
		{
			if(dx >= d)
			{
				d = i.splitter;
				n = i;
			}
		}
		
		if(n == null)
		{
			return null;
		}
		
		return n.lower();
	}
}
