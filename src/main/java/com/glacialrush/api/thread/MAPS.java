package com.glacialrush.api.thread;

public class MAPS implements Runnable
{
	public static long MAPS = 0;
	public static int CGCOH = 0;
	public static int GCOH = 0;
	public static long CMEM = 0l;
	
	public static long memoryMax()
	{
		return Runtime.getRuntime().maxMemory() / 1024 / 1024;
	}
	
	public static long memoryUsed()
	{
		return Runtime.getRuntime().totalMemory() / 1024 / 1024;
	}
	
	public static long memoryFree()
	{
		return memoryMax() - (memoryUsed() - (Runtime.getRuntime().freeMemory() / 1024 / 1024));
	}
	
	public static long getMAPS()
	{
		return MAPS;
	}
	
	public static int getGCOH()
	{
		return GCOH;
	}
	
	public void run()
	{
		long MEMDIFF = (memoryFree() - CMEM);
		
		if(MEMDIFF < 0)
		{
			MAPS = -MEMDIFF;
		}
		
		else
		{
			GCOH = CGCOH;
			CGCOH = 0;
		}
		
		CGCOH++;
		
		CMEM = memoryFree();
	}
}