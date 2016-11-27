package com.glacialrush.api.game.event;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.map.Region;

public class PlayerEvent extends GameEvent
{
	protected final Player player;
	protected final Faction faction;
	protected final Region region;
	
	public PlayerEvent(RegionedGame game, Player player)
	{
		super(game);
		
		this.player = player;
		this.faction = game.getFactionHandler().getFaction(player);
		this.region = game.getMap().getRegion(player);
	}
	
	public Faction getFaction()
	{
		return faction;
	}
	
	public Region getRegion()
	{
		return region;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
