package com.glacialrush.packet.resourcepack;

import org.bukkit.entity.Player;

public class ResourcePackLoadedEvent extends ResourcePackEvent
{
	public ResourcePackLoadedEvent(Player player)
	{
		super(player);
	}
}
