package com.glacialrush.api.game.event;

import com.glacialrush.api.game.RegionedGame;

public class GameEvent extends GlacialEvent
{
	private final RegionedGame game;
	
	public GameEvent(RegionedGame game)
	{
		this.game = game;
	}

	public RegionedGame getGame()
	{
		return game;
	}
}
