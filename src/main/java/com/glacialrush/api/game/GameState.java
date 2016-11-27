package com.glacialrush.api.game;

import org.bukkit.entity.Player;
import com.glacialrush.api.GPlugin;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.object.PlayerObject;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.object.GList;

public class GameState
{
	protected final GPlugin pl;
	protected Map map;
	protected GList<Player> players;
	protected GList<PlayerObject> playerObjects;
	protected Boolean running;
	protected Game game;
	
	public GameState(Game game)
	{
		this.game = game;
		this.pl = GlacialPlugin.instance();
		this.players = new GList<Player>();
		this.running = false;
		this.playerObjects = new GList<PlayerObject>();
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public void setGame(Game game)
	{
		this.game = game;
	}
	
	public Boolean getRunning()
	{
		return running;
	}
	
	public void setRunning(Boolean running)
	{
		this.running = running;
	}
	
	public Map getMap()
	{
		return map;
	}
	
	public void setMap(Map map)
	{
		this.map = map;
	}
	
	public GList<Player> getPlayers()
	{
		return players;
	}
	
	public void setPlayers(GList<Player> players)
	{
		this.players = players;
	}
}
