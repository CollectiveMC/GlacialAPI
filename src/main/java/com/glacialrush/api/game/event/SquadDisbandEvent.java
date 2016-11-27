package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.object.Squad;

public class SquadDisbandEvent extends SquadEvent
{
	public SquadDisbandEvent(RegionedGame game, Player player, Squad squad)
	{
		super(game, player, squad);
	}
}
