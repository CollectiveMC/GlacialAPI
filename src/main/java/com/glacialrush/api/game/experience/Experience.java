package com.glacialrush.api.game.experience;

public enum Experience
{
	KILL("Kill"),
	
	PALADIN_KILL("Paladin Slain"),
	
	KILL_DOUBLE("Double Kill"),
	
	KILL_HEADSHOT("Headshot!"),
	
	KILL_MULTI("MULTI KILL!"),
	
	KILL_ASSIST("Kill Assist"),
	
	KILL_REPEAT("Recurring Customer"),
	
	RAGE_QUIT("Rage Quit"),
	
	VOTE("Comminity Voter"),
	
	BOUNTY("Bounty Fulfilled (REWARD X 500XP)"),
	
	KILL_BONUS("Kill Bonus"),
	
	KILL_ASSIST_BONUS("Kill Assist Bonus"),
	
	CAPTURE_POINT("Point Control"),
	
	CAPTURE_TERRITORY("Territory Control"),
	
	CAPTURE_VILLAGE("Village Control"),
	
	CAPTURE_MAP("Map Control"),
	
	CAPTURE_WARPGATE("Warpgate Control"),
	
	INFLUENCE_POINT("Point Influence"),
	
	INFLUENCE_TERRITORY("Territory Influence"), 
	
	INFLUENCE_VILLAGE("Village Influence"), 
	
	INFLUENCE_MAP("Map Influence"),
	
	UNKNOWN("yolo1337ftw");
	
	private String reason;
	
	private Experience(String reason)
	{
		this.reason = reason;
	}
	
	public String reason()
	{
		return reason;
	}
}
