package com.glacialrush.packet.resourcepack;

import org.bukkit.entity.Player;

public class ResourcePackDeclinedEvent extends ResourcePackEvent
{
	public ResourcePackDeclinedEvent(Player player)
	{
		super(player);
	}
}
