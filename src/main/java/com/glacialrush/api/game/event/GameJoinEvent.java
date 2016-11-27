package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;

public class GameJoinEvent extends PlayerEvent
{
	public GameJoinEvent(RegionedGame game, Player player)
	{
		super(game, player);
	}
}
