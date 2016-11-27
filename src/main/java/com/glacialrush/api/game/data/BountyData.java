package com.glacialrush.api.game.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.glacialrush.api.object.GMap;

public class BountyData
{
	private GMap<String, Integer> bounty;
	
	public BountyData()
	{
		this.bounty = new GMap<String, Integer>();
	}
	
	public BountyData(FileConfiguration fc)
	{
		this.bounty = new GMap<String, Integer>();
		
		for(String i : fc.getKeys(true))
		{
			bounty.put(i, fc.getInt(i));
		}
	}
	
	public FileConfiguration toYaml()
	{
		FileConfiguration fc = new YamlConfiguration();
		
		for(String i : bounty.keySet())
		{
			fc.set(i, bounty.get(i));
		}
		
		return fc;
	}

	public GMap<String, Integer> getBounty()
	{
		return bounty;
	}

	public void setBounty(GMap<String, Integer> bounty)
	{
		this.bounty = bounty;
	}
}
