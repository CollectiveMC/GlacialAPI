package com.glacialrush.api.dispatch;

import java.util.Date;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.component.Controller;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;

public class LogController extends Controller
{
	private GMap<String, GList<String>> logs;
	
	public LogController(GlacialPlugin pl)
	{
		super(pl);
		logs = new GMap<String, GList<String>>();
	}
	
	public void save()
	{
		o("Saving Logs");
		pl.saveLogs(logs);
		logs.clear();
	}
	
	public void log(String ch, String msg)
	{
		if(!logs.contains(ch))
		{
			logs.put(ch, new GList<String>());
		}
		
		Date d = new Date();
		
		logs.get(ch).add(d.toString() + "::> " + msg);
		
		if(size() > 512)
		{
			save();
		}
	}
	
	public int size()
	{
		int s = 0;
		
		for(String i : logs.keySet())
		{
			s += logs.get(i).size();
		}
		
		return s;
	}
	
	public void preEnable()
	{
		super.preEnable();
	}
	
	public void postEnable()
	{
		super.postEnable();
	}
	
	public void preDisable()
	{
		super.preDisable();
	}
	
	public void postDisable()
	{
		super.postDisable();
		save();
	}
}
