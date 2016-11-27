package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;

public class ChatEvent extends NonRegionedPlayerEvent
{
	protected String message;
	
	public ChatEvent(RegionedGame game, Player player, String message)
	{
		super(game, player);
		
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
