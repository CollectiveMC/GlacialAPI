package com.glacialrush.api.object;

import org.bukkit.entity.Player;

public class PlayerRunnable implements Runnable
{
	private Player player;
	
	public void run(Player player)
	{
		this.player = player;
		run();
	}
	
	@Override
	public void run()
	{
		
	}

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}
}
