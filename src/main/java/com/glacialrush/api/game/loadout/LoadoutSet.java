package com.glacialrush.api.game.loadout;

import java.io.Serializable;
import org.bukkit.configuration.file.FileConfiguration;
import com.glacialrush.Info;

public class LoadoutSet implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Loadout[] loadouts;
	
	public LoadoutSet()
	{
		loadouts = new Loadout[1];
		
		for(int i = 0; i < loadouts.length; i++)
		{
			loadouts[i] = new Loadout();
		}
	}
	
	public Loadout getLoadout()
	{
		return loadouts[0];
	}
	
	public LoadoutSet(FileConfiguration fc)
	{
		loadouts = new Loadout[5];
		
		for(int i = 0; i < loadouts.length; i++)
		{
			loadouts[i] = new Loadout(fc, "loadout-set.loadout-" + Info.ALPHABET[i]);
		}
	}
	
	public FileConfiguration yml(FileConfiguration fc)
	{
		for(int i = 0; i < loadouts.length; i++)
		{
			fc = loadouts[i].yml(fc, "loadout-set.loadout-" + Info.ALPHABET[i]);
		}
		
		return fc;
	}
}
