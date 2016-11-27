package com.glacialrush.packet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener implements Listener, Runnable
{
	private final JavaPlugin pl;
	private String name;
	
	public VoteListener(JavaPlugin pl)
	{
		this.pl = pl;
		
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler
	public void onVote(VotifierEvent e)
	{
		run(e.getVote().getUsername());
	}
	
	public void run(String name)
	{
		this.name = name;
		run();
	}
	
	@Override
	public void run()
	{
		
	}

	public JavaPlugin getPl()
	{
		return pl;
	}

	public String getName()
	{
		return name;
	}
}
