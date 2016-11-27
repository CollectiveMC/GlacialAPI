package com.glacialrush.api;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.phantomapi.construct.PhantomPlugin;

public class GPlugin extends PhantomPlugin
{	
	public void callEvent(Event event)
	{
		getServer().getPluginManager().callEvent(event);
	}
	
	public void msg(CommandSender sender, String msg)
	{
		sender.sendMessage(msg);
	}
	
	public void msg(CommandSender sender, String[] msgs)
	{
		for(String i : msgs)
		{
			msg(sender, i);
		}
	}
	
	public int scheduleSyncRepeatingTask(int delay, int interval, Runnable runnable)
	{
		return getServer().getScheduler().scheduleSyncRepeatingTask(this, runnable, delay, interval);
	}
	
	public int scheduleSyncTask(int delay, Runnable runnable)
	{
		return getServer().getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
	}
	
	public void cancelTask(int tid)
	{
		getServer().getScheduler().cancelTask(tid);
	}
	
	@SuppressWarnings("deprecation")
	public Location target(Player p)
	{
		return p.getTargetBlock((HashSet<Byte>) null, 512).getLocation();
	}
	
	public boolean canFindPlayer(String search)
	{
		return findPlayer(search) == null ? false : true;
	}
	
	public Player findPlayer(String search)
	{
		for(Player i : onlinePlayers())
		{
			if(i.getName().equalsIgnoreCase(search))
			{
				return i;
			}
		}
		
		for(Player i : onlinePlayers())
		{
			if(i.getName().toLowerCase().contains(search.toLowerCase()))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void register(Listener listener)
	{
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void unRegister(Listener listener)
	{
		HandlerList.unregisterAll(listener);
	}
	
	public void exportJarResource(String resource, File path) throws Exception
	{
		URL inputUrl = getClass().getResource(resource);
		FileUtils.copyURLToFile(inputUrl, path);
	}

	@Override
	public void disable()
	{
		
	}

	@Override
	public void enable()
	{
		
	}
}
