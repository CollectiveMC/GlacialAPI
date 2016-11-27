package com.glacialrush.api.game.object;

public enum VillageBuff
{
	EXPERIENCE_BOOST("30% Xp Boost", "Grants a 30% xp boost for the dominating faction.");
	
	private String name;
	private String description;
	
	private VillageBuff(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}
}
