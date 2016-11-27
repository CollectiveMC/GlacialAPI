package com.glacialrush.api.host;

import java.util.Collection;
import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.PlayerObject;
import com.glacialrush.api.json.JSONArray;
import com.glacialrush.api.json.JSONObject;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.map.region.LinkedRegion;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.object.GList;

public class GAPIHost
{
	private GlacialPlugin pl;
	
	public GAPIHost(GlacialPlugin pl)
	{
		this.pl = pl;
	}
	
	public Collection<? extends Player> getPlayers()
	{
		return pl.onlinePlayers();
	}
	
	public GList<Game> games()
	{
		return pl.getGameControl().getGames();
	}
	
	public GList<RegionedGame> regionedGames()
	{
		GList<RegionedGame> g = new GList<RegionedGame>();
		
		for(Game i : games())
		{
			if(i.getType().equals(GameType.REGIONED))
			{
				g.add((RegionedGame)i);
			}
		}
		
		return g;
	}
	
	public RegionedGame getGame(Map map)
	{
		for(RegionedGame i : regionedGames())
		{
			if(i.getMap().equals(map))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public Game getGame(Player p)
	{
		return pl.gameControl.getGame(p);
	}
	
	public PlayerData getPlayerData(Player p)
	{
		return pl.gpd(p);
	}
	
	public PlayerObject getPlayerObject(Player p)
	{
		return pl.getGameControl().gpo(p);
	}
	
	public JSONObject statusJSON()
	{
		JSONObject o = new JSONObject();
		JSONArray games = new JSONArray();
		JSONArray players = new JSONArray();
		
		for(RegionedGame i : regionedGames())
		{
			games.put(gameJSON(i));
		}
		
		for(Player i : pl.onlinePlayers())
		{
			players.put(playerJSON(i));
		}
		
		o.put("players", players);
		o.put("games", games);
		
		return o;
	}
	
	public JSONObject playerJSON(Player p)
	{
		JSONObject player = new JSONObject();
		
		player.put("name", p.getName());
		player.put("uuid", p.getUniqueId().toString());
		
		return player;
	}
	
	public JSONObject gameJSON(RegionedGame g)
	{
		JSONObject game = new JSONObject();
		JSONArray regions = new JSONArray();
		JSONArray players = new JSONArray();
		
		for(Player i : g.players())
		{
			JSONObject player = new JSONObject();
			
			player.put("uuid", i.getUniqueId().toString());
			player.put("faction", g.getFactionHandler().getFaction(i).getName());
			
			players.put(player);
		}
		
		for(Region i : g.getMap().getRegions())
		{
			regions.put(regionJSON(i));
		}
		
		game.put("uuid", g.getUuid().toString());
		game.put("map", g.getMap().getName());
		game.put("regions", regions);
		game.put("players", players);
		
		return game;
	}
	
	public JSONObject regionJSON(Region r)
	{
		JSONObject region = new JSONObject();
		
		region.put("name", r.getName());
		region.put("type", r.getType().toString());
		
		if(r.getType().equals(RegionType.VILLAGE) || r.getType().equals(RegionType.TERRITORY))
		{
			LinkedRegion lr = (LinkedRegion) r;
			
			region.put("faction", lr.getFaction().getName());
			
			if(lr.getType().equals(RegionType.TERRITORY))
			{
				Territory t = (Territory) lr;
				JSONArray captures = new JSONArray();
				
				for(Capture i : t.getCaptures())
				{
					captures.put(captureJSON(i));
				}
				
				region.put("captures", captures);
			}
		}
		
		return region;
	}
	
	public JSONObject captureJSON(Capture c)
	{
		JSONObject capture = new JSONObject();
		
		capture.put("faction", c.getFaction().getName());
		
		return capture;
	}
}
