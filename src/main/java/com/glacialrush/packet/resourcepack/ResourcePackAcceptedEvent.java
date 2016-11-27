package com.glacialrush.packet.resourcepack;

import org.bukkit.entity.Player;

public class ResourcePackAcceptedEvent extends ResourcePackEvent
{
	public ResourcePackAcceptedEvent(Player player)
	{
		super(player);
	}
}
