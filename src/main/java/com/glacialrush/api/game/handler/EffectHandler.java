package com.glacialrush.api.game.handler;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.object.Electricity;
import com.glacialrush.api.game.object.Faction;

public class EffectHandler extends GlacialHandler
{
	public EffectHandler(Game game)
	{
		super(game);
	}
	
	public void shock(Player p)
	{
		Faction f = ((RegionedGame)game).getFactionHandler().getFaction(p);
		Electricity e = new Electricity((RegionedGame) game, p.getLocation(), 4, 8.8, f);
		
		for(LivingEntity i : e.shock())
		{
			s(i.getEntityId() + "");
			i.damage(2.1);
		}
	}
}
