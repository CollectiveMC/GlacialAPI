package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;

public class CombatEvent extends PlayerEvent
{
	protected final Player damager;
	protected Double damage;
	
	public CombatEvent(RegionedGame game, Player player, Player damager, Double damage)
	{
		super(game, player);
		
		this.damager = damager;
		this.damage = damage;
	}

	public Double getDamage()
	{
		return damage;
	}

	public void setDamage(Double damage)
	{
		this.damage = damage;
	}

	public Player getDamager()
	{
		return damager;
	}
}
