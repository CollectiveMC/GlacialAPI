package com.glacialrush.api.game.handler;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.util.FactionComparator;

public class FactionHandler extends GlacialHandler
{
	private GMap<Faction, GList<Player>> players;
	
	public FactionHandler(Game game)
	{
		super(game);
		
		players = new GMap<Faction, GList<Player>>();
		players.put(Faction.cryptic(), new GList<Player>());
		players.put(Faction.enigma(), new GList<Player>());
		players.put(Faction.omni(), new GList<Player>());
	}
	
	public Faction strongest(GList<Player> px)
	{
		Faction f = null;
		Integer s = Integer.MIN_VALUE;
		GMap<Faction, GList<Player>> players = sort(px);
		
		for(Faction i : players.keySet())
		{
			if(players.get(i).size() > s)
			{
				s = players.get(i).size();
				f = i;
			}
		}
		
		return f;
	}
	
	public Faction weakest(GList<Player> px)
	{
		Faction f = null;
		Integer s = Integer.MAX_VALUE;
		GMap<Faction, GList<Player>> players = sort(px);
		
		for(Faction i : players.keySet())
		{
			if(players.get(i).size() < s)
			{
				s = players.get(i).size();
				f = i;
			}
		}
		
		if(f == null)
		{
			return Faction.random();
		}
		
		return f;
	}
	
	public void cf(Player p, Faction f)
	{
		players.get(getFaction(p)).remove(p);
		players.get(f).add(p);
	}
	
	public GMap<Faction, GList<Player>> sort(GList<Player> players)
	{
		GMap<Faction, GList<Player>> px = new GMap<Faction, GList<Player>>();
		
		for(Player i : players)
		{
			if(getFaction(i) != null)
			{
				Faction f = getFaction(i);
				
				if(!px.containsKey(f))
				{
					px.put(f, new GList<Player>());
				}
				
				if(!px.get(f).contains(i))
				{
					px.get(f).add(i);
				}
			}
		}
		
		return px;
	}
	
	public Faction weakest()
	{
		Faction f = FactionComparator.getMin(sizes());
		
		if(f == null)
		{
			w("Invalid determination for weakest faction! Selecting RANDOM");
			return Faction.random();
		}
		
		return f;
	}
	
	public GMap<Faction, Integer> sizes()
	{
		GMap<Faction, Integer> map = new GMap<Faction, Integer>();
		
		for(Faction i : Faction.all())
		{
			if(((RegionedGame)game).isEliminated(i))
			{
				map.put(i, 10000);
			}
			
			else
			{
				map.put(i, players.get(i).size());
			}
		}
		
		return map;
	}
	
	public Faction strongest()
	{
		Faction f = FactionComparator.getMax(sizes());
		
		if(f == null)
		{
			w("Invalid determination for weakest faction! Selecting RANDOM");
			return Faction.random();
		}
		
		return f;
	}
	
	public Faction getFaction(Player p)
	{
		for(Faction i : Faction.all())
		{
			if(players.get(i).contains(p))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public GList<Player> getPlayers(Faction f)
	{
		return players.get(f);
	}
	
	public void clear()
	{
		players.clear();
		players.put(Faction.cryptic(), new GList<Player>());
		players.put(Faction.enigma(), new GList<Player>());
		players.put(Faction.omni(), new GList<Player>());
	}
	
	public String map()
	{
		String m = "";
		
		for(Faction i : Faction.all())
		{
			m = m + i.getColor() + players.get(i).size() + "  ";
		}
		
		return m;
	}
	
	public void insert(Player p)
	{
		if(!players.get(weakest()).contains(p))
		{
			players.get(weakest()).add(p);
		}
	}
	
	public void remove(Player p)
	{
		for(Faction i : Faction.all())
		{
			if(players.get(i).contains(p))
			{
				players.get(i).remove(p);
			}
		}
	}
}
