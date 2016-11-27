package com.glacialrush.api.game.experience;

import org.apache.commons.lang3.StringUtils;

public enum ExperienceBoost
{
	FACTION_INBALANCE,
	DONATOR,
	BOOST,
	VILLAGE_BUFF;
	
	public String getName()
	{
		return StringUtils.capitalize(toString().toLowerCase().replace('_', ' '));
	}
}
