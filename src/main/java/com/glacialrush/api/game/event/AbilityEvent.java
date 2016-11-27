package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.obtainable.Ability;

public class AbilityEvent extends PlayerEvent	
{
	private final Ability ability;
	
	public AbilityEvent(RegionedGame game, Player player, Ability ability)
	{
		super(game, player);
		
		this.ability = ability;
	}

	public Ability getAbility()
	{
		return ability;
	}
}
