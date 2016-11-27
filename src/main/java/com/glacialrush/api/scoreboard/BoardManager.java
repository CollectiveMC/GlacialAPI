package com.glacialrush.api.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import com.glacialrush.api.GPlugin;
import com.glacialrush.api.object.GMap;

public class BoardManager
{
	private GPlugin pl;
	private String name;
	private GMap<Player, Scoreboard> boards;
	
	public BoardManager(String name, GPlugin pl)
	{
		this.pl = pl;
		this.name = name;
		this.boards = new GMap<Player, Scoreboard>();
	}
	
	public void set(Player p, GMap<String, String> data)
	{
		Scoreboard board = pl.getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("objective", "dummy");
		
		objective.setDisplayName(name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		int ix = 0;
		
		for(String k : data.keySet())
		{
			String s = k + ": " + data.get(k);
			
			if(k.contains("_"))
			{
				s = data.get(k);
			}
			
			if(s.length() > 40)
			{
				s = s.substring(0, 37) + "...";
			}
			
			objective.getScore(s).setScore(ix);
			ix++;
		}
		
		boards.put(p, board);
		p.setScoreboard(board);
	}
}
