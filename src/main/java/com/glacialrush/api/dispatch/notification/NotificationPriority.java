package com.glacialrush.api.dispatch.notification;

import com.glacialrush.api.object.GList;

public enum NotificationPriority
{
	LOWEST(0),
	VERYLOW(1),
	LOW(2),
	MEDIUM(3),
	HIGH(4),
	VERYHIGH(5),
	HIGHEST(6);
	
	private int level;
	
	private NotificationPriority(int level)
	{
		this.level = level;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public static GList<NotificationPriority> topDown()
	{
		return new GList<NotificationPriority>().qadd(HIGHEST).qadd(VERYHIGH).qadd(HIGH).qadd(MEDIUM).qadd(LOW).qadd(VERYLOW).qadd(LOWEST);
	}
}
