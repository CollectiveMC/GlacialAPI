package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.experience.Experience;

public class ExperienceEvent extends PlayerEvent
{
	private final Experience reason;
	private Long experience;
	
	public ExperienceEvent(RegionedGame game, Player player, Experience reason, Long experience)
	{
		super(game, player);
		
		this.reason = reason;
		this.experience = experience;
	}

	public Long getExperience()
	{
		return experience;
	}

	public void setExperience(Long experience)
	{
		this.experience = experience;
	}

	public Experience getReason()
	{
		return reason;
	}
}
