package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;

import com.glacialrush.api.game.RegionedGame;

public class DeathEvent extends CombatEvent
{
	public DeathEvent(RegionedGame game, Player player, Player damager, Double damage)
	{
		super(game, player, damager, damage);
	}
	
	public boolean suicide()
	{
		return player.equals(damager);
	}
}
