package com.glacialrush;

import com.glacialrush.api.object.GList;

public enum Strings
{
	COMBAT_SUICIDE("Suicide", "Self Destruction"),
	COMBAT_JANITOR("The Janitor", "Cleaned Up", "Mopped Up", "Kill Snatched", "Kill Stealer");
	
	private GList<String> strings;
	
	private Strings(String... strings)
	{
		this.strings = new GList<String>(strings);
	}
	
	public String get()
	{
		return strings.pickRandom();
	}
}
