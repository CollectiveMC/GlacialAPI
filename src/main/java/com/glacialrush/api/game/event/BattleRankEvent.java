package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;

public class BattleRankEvent extends PlayerEvent
{
	private final Long totalExperience;
	private final Integer battleRank;
	private final Integer prevBR;
	
	public BattleRankEvent(RegionedGame game, Player player, long totalExperience, int battleRank, int prevBR)
	{
		super(game, player);
		
		this.totalExperience = totalExperience;
		this.battleRank = battleRank;
		this.prevBR = prevBR;
	}

	public Long getTotalExperience()
	{
		return totalExperience;
	}

	public Integer getBattleRank()
	{
		return battleRank;
	}

	public Integer getPrevBR()
	{
		return prevBR;
	}
}
