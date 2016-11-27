package com.glacialrush.api.game.data;

import java.io.Serializable;
import org.bukkit.configuration.file.FileConfiguration;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.object.GMap;

public class PlayerStatistics implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private GMap<String, Double> data;
	
	public PlayerStatistics()
	{
		initial();
	}
	
	public void add(Statistic s, double i)
	{
		set(s, get(s) + i);
	}
	
	public Double get(Statistic s)
	{
		Double d = data.get(s.yamlTag());
		
		if(d != null)
		{
			return d;
		}
		
		return 0.0;
	}
	
	public void set(Statistic s, double v)
	{
		data.put(s.yamlTag(), v);
	}
	
	public PlayerStatistics(FileConfiguration fc, String key)
	{
		initial();
		
		for(Statistic i : Statistic.values())
		{
			if(fc.contains(i.yamlTag(key)))
			{
				data.put(i.yamlTag(), fc.getDouble(i.yamlTag(key)));
			}
		}
	}
	
	public FileConfiguration toYaml(FileConfiguration fc, String key)
	{
		for(Statistic i : Statistic.values())
		{
			if(data.containsKey(i.yamlTag()))
			{
				fc.set(i.yamlTag(key), data.get(i.yamlTag()));
			}
		}
		
		return fc;
	}
	
	public void initial()
	{
		data = new GMap<String, Double>();
		
		for(Statistic i : Statistic.values())
		{
			data.put(i.yamlTag(), 0.0);
		}
	}
}
