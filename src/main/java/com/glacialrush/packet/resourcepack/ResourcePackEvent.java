package com.glacialrush.packet.resourcepack;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResourcePackEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	
	public ResourcePackEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public void setCancelled(boolean cancel)
	{
		this.cancelled = cancel;
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}
	
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
