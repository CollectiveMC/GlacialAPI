package com.glacialrush.api.security;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import org.bukkit.entity.Player;
import com.glacialrush.api.object.GMap;

public class ChatLog
{
	private GMap<Long, String> cache;
	private Integer day;
	private File folder;
	private Calendar c;
	
	public ChatLog(File folder)
	{
		c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		this.cache = new GMap<Long, String>();
		this.day = c.get(Calendar.DAY_OF_MONTH);
		this.folder = folder;
	}
	
	public String date(Long time)
	{
		c.setTimeInMillis(time);
		return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
	}
	
	public String sdate(Long time)
	{
		c.setTimeInMillis(time);
		return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + ">";
	}
	
	public void log(Player player, String msg)
	{
		c.setTimeInMillis(System.currentTimeMillis());
		
		if(c.get(Calendar.DAY_OF_MONTH) != day)
		{
			day = c.get(Calendar.DAY_OF_MONTH);
			flush();
		}
		
		cache.put(System.currentTimeMillis(), player.getUniqueId().toString() + ":" + msg);
	}
	
	public void flush()
	{
		if(!folder.exists())
		{
			folder.mkdirs();
		}
		
		if(folder.isDirectory())
		{
			File fo = new File(folder, "chat-" + date(System.currentTimeMillis()) + ".txt");
			
			if(!fo.exists())
			{
				try
				{
					fo.createNewFile();
				}
				
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			try
			{
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fo, true)));
				
				for(Long i : cache.keySet())
				{
					pw.println(sdate(i) + cache.get(i));
				}
				
				pw.close();
				cache.clear();
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
	}
}
