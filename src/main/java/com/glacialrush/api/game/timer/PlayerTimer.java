package com.glacialrush.api.game.timer;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;

public class PlayerTimer extends GameTimer
{
	public PlayerTimer(RegionedGame g)
	{
		super(g, 0, "Player Timer");
	}
	
	public void tick()
	{
		for(Player i : g.players())
		{
			g.getGameController().gpo(i).processWeaponAbilities();
		}
	}
}
