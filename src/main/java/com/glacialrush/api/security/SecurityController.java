package com.glacialrush.api.security;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;
import com.glacialrush.Info;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.component.Controller;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.ChatEvent;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;
import org.bukkit.ChatColor;

public class SecurityController extends Controller
{
	private ChatLog chatLog;
	private GMap<GList<String>, GList<String>> matches;
	
	public SecurityController(GlacialPlugin pl)
	{
		super(pl);
		
		this.chatLog = new ChatLog(new File(new File(pl.getDataFolder(), "logs"), "chat"));
		this.matches = new GMap<GList<String>, GList<String>>();
		
		File file = new File(new File(pl.getDataFolder(), "configuration"), "match.yml");
		FileConfiguration fc = new YamlConfiguration();
		
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			
			try
			{
				file.createNewFile();
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			fc.load(file);
			
			for(String i : fc.getKeys(false))
			{
				GList<String> ks = new GList<String>(i.split("-"));
				GList<String> vs = new GList<String>(fc.getStringList(i));
				matches.put(ks, vs);
				s("Match Inserted: " + ks.toString(", ") + " <> " + vs.toString(", "));
			}
		}
		
		catch(IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}
	
	public void preDisable()
	{
		chatLog.flush();
	}
	
	public ChatLog getChatLog()
	{
		return chatLog;
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void onPing(ServerListPingEvent e)
	{
		e.setMotd(ChatColor.AQUA + "Glacial Rush! " + ChatColor.GREEN + "v" + Info.VERSION);
	}
	
	public String matchReplace(Player p, String s)
	{
		int points = 0;
		
		for(GList<String> i : matches.keySet())
		{
			for(String j : i)
			{
				if(StringUtils.contains(s.toLowerCase(), j.toLowerCase()))
				{
					points += StringUtils.countMatches(s.toLowerCase(), j.toLowerCase());
					s = StringUtils.replace(s.toLowerCase(), j.toLowerCase(), matches.get(i).pickRandom());
				}
			}
		}
		
		if(points > 0)
		{
			Statistic.SOCIAL_TRASHMOUTH.add(p, (double) points);
		}
		
		return s;
	}
	
	@EventHandler
	public void onChat(ChatEvent e)
	{
		chatLog.log(e.getPlayer(), e.getMessage());
		e.setMessage(matchReplace(e.getPlayer(), e.getMessage()));
		
		if(e.getMessage().toLowerCase().contains("hacks") || e.getMessage().toLowerCase().contains("hax") || e.getMessage().toLowerCase().contains("killaura"))
		{
			Game game = pl.getGameControl().getGame(e.getPlayer());
			
			if(game != null && game.getType().equals(GameType.REGIONED))
			{
				RegionedGame rg = (RegionedGame)game;
				
				if(rg.getGameStateHandler().isDead(e.getPlayer()))
				{
					Player k = rg.getGameStateHandler().getKiller(e.getPlayer());
					
					if(k != null)
					{
						Statistic.SOCIAL_HACKS.add(k);
					}
				}
				
				Statistic.SOCIAL_HACUSATOR.add(e.getPlayer());
			}
		}
		
		if(e.getMessage().toLowerCase().contains("ddos"))
		{
			Notification n = new Notification();
			
			n.setTitlea(ChatColor.AQUA + "BRING IT ON");
			n.setTitleb(ChatColor.DARK_GRAY + "What did someone better than you kill you?");
			n.setDisplay(100);
			n.setAudio(Audio.UI_FAIL);
		}
	}
}
