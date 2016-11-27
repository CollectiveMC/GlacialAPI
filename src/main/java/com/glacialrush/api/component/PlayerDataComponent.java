package com.glacialrush.api.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.object.GMap;

public class PlayerDataComponent extends Controller
{
	private DataComponent<PlayerData> pdc;
	private File baseSerial;
	private File baseYAML;
	private String targetExtention;
	
	private GMap<Player, PlayerData> playerData;
	
	public PlayerDataComponent(GlacialPlugin pl, File base, String targetExtention)
	{
		super(pl);
		
		this.baseSerial = new File(base, "serial");
		this.baseYAML = new File(base, "yaml");
		this.targetExtention = targetExtention;
		playerData = new GMap<Player, PlayerData>();
		
		pdc = new DataComponent<PlayerData>(pl, baseSerial, targetExtention);
		pdc.verify(baseYAML);
	}
	
	public void save(Player player)
	{
		try
		{
			PlayerData pd = new PlayerData(player);
			
			if(playerData.containsKey(player))
			{
				pd = playerData.get(player);
			}
			
			FileConfiguration fc = get(player).yaml();
			File f = new File(baseYAML, player.getUniqueId() + ".yml");
			pdc.verifyFile(f);
			fc.save(f);
			pdc.save(new File(baseSerial, player.getUniqueId() + targetExtention), pd);
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void load(Player player)
	{
		loadFallback(player);
	}
	
	public void loadFallback(Player p)
	{
		File fx = new File(baseYAML, p.getUniqueId() + ".yml");
		
		if(fx.exists())
		{
			FileConfiguration fc = new YamlConfiguration();
			
			try
			{
				fc.load(fx);
				PlayerData pd = new PlayerData(fc);
				playerData.put(p, pd);
			}
			
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
				f("IOException: DATA FAILURE");
			}
			
			catch(InvalidConfigurationException e)
			{
				e.printStackTrace();
				f("INVALID CONFIGURATION. FAAAACK");
			}
		}
		
		else
		{
			f("No YAML EXISTANT FOR PLAYER: " + p.getName() + " " + p.getUniqueId().toString());
			PlayerData pd = new PlayerData(p);
			playerData.put(p, pd);
			FileConfiguration fc = get(p).yaml();
			File f = new File(baseYAML, p.getUniqueId() + ".yml");
			pdc.verifyFile(f);
			
			try
			{
				fc.save(f);
			}
			
			catch(IOException e)
			{
				f("DATA FAILURE");
				e.printStackTrace();
			}
		}
	}
	
	public PlayerData get(Player p)
	{
		return playerData.get(p);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e)
	{
		loadFallback(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e)
	{
		save(e.getPlayer());
		playerData.remove(e.getPlayer());
	}
}
