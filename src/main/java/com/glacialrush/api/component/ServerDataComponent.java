package com.glacialrush.api.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.data.BountyData;

public class ServerDataComponent extends Controller
{
	private File cfg;
	private File bounty;
	private Location hub;
	private Boolean production;
	private BountyData bountyData;
	
	public ServerDataComponent(GlacialPlugin pl)
	{
		super(pl);
		
		bountyData = new BountyData();
		bounty = new File(new File(pl.getDataFolder(), "configuration"), "bounty.yml");
		cfg = new File(new File(pl.getDataFolder(), "configuration"), "config.yml");
		load();
		loadBounty();
	}
	
	public void preDisable()
	{
		saveBounty();
	}
	
	public void setHub(Location l)
	{
		hub = l;
	}
	
	public Location getHub()
	{
		return hub.clone();
	}
	
	public void loadBounty()
	{
		FileConfiguration fc = new YamlConfiguration();
		
		if(!bounty.exists())
		{
			bounty.getParentFile().mkdirs();
			try
			{
				bounty.createNewFile();
				fc.load(bounty);
				bountyData = new BountyData(fc);
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			catch(InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void load()
	{
		FileConfiguration fc = new YamlConfiguration();
		
		try
		{
			fc.load(cfg);
			World w = pl.getServer().getWorld(fc.getString("hub.w"));
			int x = fc.getInt("hub.x");
			int y = fc.getInt("hub.y");
			int z = fc.getInt("hub.z");
			production = fc.getBoolean("production");
			
			if(w != null)
			{
				hub = new Location(w, x, y, z);
			}
		}
		
		catch(FileNotFoundException e)
		{
			create();
		}
		
		catch(IOException e)
		{
			create();
		}
		
		catch(InvalidConfigurationException e)
		{
			create();
		}
	}
	
	public void saveBounty()
	{
		try
		{
			bountyData.toYaml().save(bounty);
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void save()
	{
		try
		{
			cfg.createNewFile();
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		FileConfiguration fc = new YamlConfiguration();
		fc.set("hub.w", hub.getWorld().getName());
		fc.set("hub.x", hub.getBlockX());
		fc.set("hub.y", hub.getBlockY());
		fc.set("hub.z", hub.getBlockZ());
		fc.set("production", production);
		
		try
		{
			fc.save(cfg);
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		saveBounty();
	}
	
	public BountyData getBountyData()
	{
		return bountyData;
	}
	
	public void setBountyData(BountyData bountyData)
	{
		this.bountyData = bountyData;
	}
	
	public void setProduction(boolean prod)
	{
		production = prod;
	}
	
	public boolean isProduction()
	{
		return production;
	}
	
	public void create()
	{
		try
		{
			cfg.createNewFile();
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		FileConfiguration fc = new YamlConfiguration();
		fc.set("hub.w", ".");
		fc.set("hub.x", 0);
		fc.set("hub.y", 0);
		fc.set("hub.z", 0);
		fc.set("production", false);
		
		try
		{
			fc.save(cfg);
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
