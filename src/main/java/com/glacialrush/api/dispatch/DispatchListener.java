package com.glacialrush.api.dispatch;

import org.bukkit.entity.Player;
import com.glacialrush.api.object.GList;

public class DispatchListener
{
	private GList<Player> players;
	
	public DispatchListener()
	{
		players = new GList<Player>();
	}
	
	public void log(String s)
	{
		for(Player i : players)
		{
			i.sendMessage(s);
		}
	}

	public GList<Player> getPlayers()
	{
		return players;
	}

	public void setPlayers(GList<Player> players)
	{
		this.players = players;
	}
}
