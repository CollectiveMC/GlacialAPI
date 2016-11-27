package com.glacialrush.packet.resourcepack;

import org.bukkit.entity.Player;

public class ResourcePackFailedEvent extends ResourcePackEvent
{
	public ResourcePackFailedEvent(Player player)
	{
		super(player);
	}
}
