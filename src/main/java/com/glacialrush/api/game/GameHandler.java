package com.glacialrush.api.game;

public interface GameHandler
{
	void start(GameState state);
	void tick(GameState state);
	void stop(GameState state);
}
