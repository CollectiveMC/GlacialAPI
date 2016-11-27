package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;

public class GameQuitEvent extends PlayerEvent
{
	public GameQuitEvent(RegionedGame game, Player player)
	{
		super(game, player);
	}
}
