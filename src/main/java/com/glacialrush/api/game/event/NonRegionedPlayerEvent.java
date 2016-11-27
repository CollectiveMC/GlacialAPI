package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;

public class NonRegionedPlayerEvent extends GameEvent
{
	private Player player; 
	
	public NonRegionedPlayerEvent(RegionedGame game, Player player)
	{
		super(game);
		
		this.player = player;
	}

	public Player getPlayer()
	{
		return player;
	}
}
