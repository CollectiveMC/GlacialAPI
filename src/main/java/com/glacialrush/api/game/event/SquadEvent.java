package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.object.Squad;

public class SquadEvent extends NonRegionedPlayerEvent
{
	private final Squad squad;
	
	public SquadEvent(RegionedGame game, Player player, Squad squad)
	{
		super(game, player);
		
		this.squad = squad;
	}

	public Squad getSquad()
	{
		return squad;
	}
}
