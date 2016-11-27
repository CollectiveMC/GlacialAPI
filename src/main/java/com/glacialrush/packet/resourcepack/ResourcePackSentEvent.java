package com.glacialrush.packet.resourcepack;

import org.bukkit.entity.Player;

public class ResourcePackSentEvent extends ResourcePackEvent
{
	public ResourcePackSentEvent(Player player)
	{
		super(player);
	}
}
