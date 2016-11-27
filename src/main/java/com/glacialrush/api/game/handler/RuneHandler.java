package com.glacialrush.api.game.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.DeathEvent;
import com.glacialrush.api.game.obtainable.RuneType;
import com.glacialrush.api.game.obtainable.item.Utility;

public class RuneHandler extends GlacialHandler
{
	public RuneHandler(Game game)
	{
		super(game);
	}
	
	public boolean hasRune(Player p)
	{
		return game.getGameController().gpo(p).getRune() != null;
	}
	
	public Utility getRune(Player p)
	{
		return game.getGameController().gpo(p).getRune();
	}
	
	public boolean is(Player p, RuneType t)
	{
		if(!hasRune(p))
		{
			return false;
		}
		
		return getRune(p).getType().equals(t);
	}
	
	@EventHandler
	public void onKill(DeathEvent e)
	{
		if(!e.suicide())
		{
			if(is(e.getDamager(), RuneType.PERSISTANCE))
			{
				Player p = e.getDamager();
				((RegionedGame)game).getAbilityHandler().buff(p);
			}
		}
	}
}
