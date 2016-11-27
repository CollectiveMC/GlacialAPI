package com.glacialrush.api.game.experience;

import java.io.Serializable;
import org.bukkit.configuration.file.FileConfiguration;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.object.GMap;

public class ExperienceBoostMap implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private GMap<ExperienceBoost, Double> boosts;
	
	public ExperienceBoostMap()
	{
		boosts = new GMap<ExperienceBoost, Double>();
		
		for(ExperienceBoost i : ExperienceBoost.values())
		{
			boosts.put(i, 0.0);
		}
	}
	
	public ExperienceBoostMap(FileConfiguration fc)
	{
		boosts = new GMap<ExperienceBoost, Double>();
		
		for(ExperienceBoost i : ExperienceBoost.values())
		{
			if(fc.contains("experience.boost." + i.toString().toLowerCase().replace('_', '-')))
			{
				boosts.put(i, fc.getDouble("experience.boost." + i.toString().toLowerCase().replace('_', '-')));
			}
			
			boosts.put(i, 0.0);
		}
	}
	
	public FileConfiguration yaml(FileConfiguration fc)
	{
		for(ExperienceBoost i : ExperienceBoost.values())
		{
			double f = 0.0;
			
			if(boosts.containsKey(i))
			{
				f = boosts.get(i);
			}
			
			fc.set("experience.boost." + i.toString().toLowerCase().replace('_', '-'), f);
		}
		
		return fc;
	}
	
	public void update(PlayerData pd)
	{
		double b = 0.0;
		
		for(ExperienceBoost i : ExperienceBoost.values())
		{
			double mb = 0.0;
			
			if(boosts.containsKey(i))
			{
				mb = boosts.get(i);
			}
			
			b+= mb;
		}
		
		pd.setExperienceBoost(b);
	}
	
	public void boost(PlayerData pd, ExperienceBoost boost, double ammount)
	{
		boosts.put(boost, ammount);
		update(pd);
	}
	
	public double getBoostComponent(ExperienceBoost boost)
	{
		return boosts.get(boost);
	}

	public GMap<ExperienceBoost, Double> getBoosts()
	{
		return boosts;
	}
}
