package com.glacialrush.api.game.handler;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.experience.ExperienceBoost;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.obtainable.RuneType;
import com.glacialrush.api.rank.Rank;
import com.glacialrush.xapi.FastMath;

public class BalanceHandler extends GlacialHandler
{
	private RegionedGame rg;
	
	public BalanceHandler(Game game)
	{
		super(game);
		
		rg = (RegionedGame) game;
		
		rg.pl().scheduleSyncRepeatingTask(20, 20, new Runnable()
		{
			@Override
			public void run()
			{
				rebalance();
			}
		});
	}
	
	public void rebalance()
	{
		for(Faction i : Faction.all())
		{
			double b = 0.0;
			
			b += balanceFactionSize(i);
			b += balanceRegions(i);
			
			if(rg.players().size() > 12)
			{
				b += 0.34;
			}
			
			for(Player j : rg.getFactionHandler().getPlayers(i))
			{
				updateBoosts(j);
				rg.getGameController().gpd(j).getExperienceBoostMap().boost(rg.getGameController().gpd(j), ExperienceBoost.FACTION_INBALANCE, b + balanceEnemiesNearby(j));
			}
		}
	}
	
	public void updateBoosts(Player p)
	{
		double rr = 0;
		
		if(rg.getRuneHandler().is(p, RuneType.GREED))
		{
			rr = 0.3;
		}
		
		Rank r = Rank.best(rg.getGameController().gpd(p).getRanks());
		rg.getExperienceHandler().setXpb(p, ExperienceBoost.DONATOR, r.getBoost() + rr);
	}
	
	public double balanceRegions(Faction f)
	{
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		
		Faction x = null;
		Faction n = null;
		Faction m = null;
		
		for(Faction i : Faction.all())
		{
			int c = rg.getMap().factionTerritoryCount(i);
			
			if(c > max)
			{
				max = c;
				x = i;
			}
		}
		
		for(Faction i : Faction.all())
		{
			int c = rg.getMap().factionTerritoryCount(i);
			
			if(c < min)
			{
				min = c;
				n = i;
			}
		}
		
		for(Faction i : Faction.all())
		{
			if(!i.equals(x) && !i.equals(n))
			{
				m = i;
				break;
			}
		}
		
		if(f.equals(x))
		{
			return 0.06;
		}
		
		else if(f.equals(m))
		{
			return 0.11;
		}
		
		else
		{
			return 0.19;
		}
	}
	
	public double balanceEnemiesNearby(Player p)
	{
		double b = 0;
		
		for(Player i : rg.players())
		{
			if(FastMath.isInRadius(p.getLocation(), i.getLocation(), 48f))
			{
				Faction f = rg.getFactionHandler().getFaction(i);
				
				if(f.equals(rg.getFactionHandler().getFaction(p)))
				{
					b += 0.01;
				}
			}
		}
		
		return b;
	}
	
	public double balanceFactionSize(Faction f)
	{
		double b = 0.0;
		
		if(rg.getFactionHandler().strongest().equals(f))
		{
			b = 0.0;
		}
		
		else if(rg.getFactionHandler().weakest().equals(f))
		{
			b = 0.24;
		}
		
		else
		{
			b = 0.12;
		}
		
		return b;
	}
}
