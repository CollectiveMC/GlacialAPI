package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.obtainable.Ability;

public class FireAbilityEvent extends AbilityEvent
{
	public FireAbilityEvent(RegionedGame game, Player player, Ability ability)
	{
		super(game, player, ability);
	}
}
