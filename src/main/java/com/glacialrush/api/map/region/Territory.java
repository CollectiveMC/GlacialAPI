package com.glacialrush.api.map.region;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.Generator;
import com.glacialrush.api.game.object.Influenced;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;

public class Territory extends LinkedRegion implements Influenced
{
	protected GList<Capture> captures;
	protected Boolean warpgate;
	protected Generator generator;
	protected GMap<Player, Integer> influence;
	
	public Territory(Map map, String name, Game game)
	{
		super(map, name, RegionType.TERRITORY, game);
		
		captures = new GList<Capture>();
		influence = new GMap<Player, Integer>();
		warpgate = false;
	}
	
	public void build(Block block)
	{
		super.build(block);
		
		if(block.getType().equals(Material.BEACON))
		{
			captures.add(new Capture(this, block.getLocation()));
		}
		
		if(block.getType().equals(Material.DIAMOND_BLOCK))
		{
			if(warpgate)
			{
				return;
			}
			
			warpgate = true;
			generator = new Generator(block.getLocation(), this);
			pl.s("Warpgate Created");
		}
	}
	
	public Generator getGenerator()
	{
		return generator;
	}
	
	public void setGenerator(Generator generator)
	{
		this.generator = generator;
	}
	
	public GMap<Player, Integer> getInfluence()
	{
		return influence;
	}
	
	public void setInfluence(GMap<Player, Integer> influence)
	{
		this.influence = influence;
	}
	
	public void preBuild()
	{
		super.preBuild();
		captures.clear();
		warpgate = false;
	}
	
	public void postBuild()
	{
		super.postBuild();
		
		if(warpgate)
		{
			generator.build();
			
			for(Capture i : captures)
			{
				i.getLocation().getBlock().setType(Material.AIR);
			}
			
			captures.clear();
		}
		
		else
		{
			for(Capture i : captures)
			{
				i.build();
				i.reset(getFaction());
				
				Double distance = Double.MAX_VALUE;
				
				for(Capture j : captures.copy())
				{
					if(!j.getLocation().equals(i.getLocation()))
					{
						Double dist = j.getLocation().distance(i.getLocation());
						
						if(dist < distance)
						{
							distance = dist;
						}
					}
				}
				
				distance = distance / 3;
				
				if(distance > 1000)
				{
					distance = 9.0;
				}
				
				i.setRange(distance.floatValue());
			}
		}
	}
	
	public void setFaction(Faction faction)
	{
		super.setFaction(faction);
		
		for(Capture i : captures)
		{
			i.reset(faction);
		}
	}
	
	@Override
	public GMap<Player, Integer> getInfluenceMap()
	{
		return influence;
	}
	
	@Override
	public void resetInfluenceMap()
	{
		influence.clear();
	}
	
	@Override
	public void influence(Player p, Integer i)
	{
		influence.put(p, (influence.containsKey(p) ? influence.get(p) : 0) + i);
	}
	
	@Override
	public void injectInfluence(GMap<Player, Integer> influence)
	{
		for(Player p : influence.keySet())
		{
			influence(p, influence.get(p));
		}
	}
	
	@Override
	public GMap<Player, Integer> popInfluenceMap()
	{
		for(Capture i : captures)
		{
			injectInfluence(i.popInfluenceMap());
		}
		
		GMap<Player, Integer> ic = influence.copy();
		resetInfluenceMap();
		
		return ic;
	}
	
	public void pullInfluenceMap()
	{
		for(Capture i : captures)
		{
			injectInfluence(i.popInfluenceMap());
		}
	}
	
	@Override
	public double getInfluence(Player p)
	{
		int ti = 0;
		
		for(Player i : influence.keySet())
		{
			ti += influence.get(i);
		}
		
		if(!influence.containsKey(p))
		{
			return 0.0;
		}
		
		return (double) ((double) influence.get(p) / (double) ti);
	}
	
	public boolean canCapture(Faction f)
	{
		for(Region i : getBorders())
		{
			if(i.getType().equals(RegionType.TERRITORY) || i.getType().equals(RegionType.VILLAGE))
			{
				LinkedRegion l = (LinkedRegion) i;
				
				if(l.getFaction().equals(f))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public GList<Capture> getCaptures()
	{
		return captures;
	}
	
	public void setCaptures(GList<Capture> captures)
	{
		this.captures = captures;
	}
	
	public Boolean getWarpgate()
	{
		return warpgate;
	}
	
	public void setWarpgate(Boolean warpgate)
	{
		this.warpgate = warpgate;
	}
}
