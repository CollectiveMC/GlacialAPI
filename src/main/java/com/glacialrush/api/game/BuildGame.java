package com.glacialrush.api.game;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.object.Job;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.object.GBiset;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.scoreboard.BoardController;
import org.bukkit.ChatColor;

public class BuildGame extends Game
{
	private GMap<Job, Integer> jobs;
	
	public BuildGame(GameController gameController)
	{
		super(gameController);
		boardController = new BoardController(ChatColor.GREEN + "Builder Information", pl);
		setType(GameType.BUILD);
		jobs = new GMap<Job, Integer>();
	}
	
	@Override
	public void join(Player p)
	{
		super.join(p);
	}
	
	public void updateJobStatus(Job job, Integer percent)
	{
		jobs.put(job, percent);
	}
	
	public void finishJob(Job job)
	{
		jobs.remove(job);
	}
	
	public void updateSelection(Player p, GBiset<Map, Region> selection)
	{
		boardController.remove(p);
		
		if(selection.getA() != null)
		{
			boardController.put(p, ChatColor.GREEN + "Name", ChatColor.AQUA + selection.getA().getName());
			boardController.put(p, ChatColor.GREEN + "Size", ChatColor.AQUA + "" + selection.getA().getRegions().size() + " Regions");
			
			if(selection.getB() != null)
			{
				boardController.put(p, ChatColor.YELLOW + "Rg Name", ChatColor.AQUA + selection.getB().getName());
				boardController.put(p, ChatColor.YELLOW + "Rg Type", ChatColor.AQUA + selection.getB().getType().toString());
				boardController.put(p, ChatColor.YELLOW + "Rg Size", ChatColor.AQUA + "" + selection.getB().getChunklets().size());
			}
			
			if(getMapHandler().building())
			{
				boardController.put(p, ChatColor.RED + "BUILDING: ", ChatColor.YELLOW + "" + getMapHandler().buildProgress() + "%");
			}
			
			if(getMapHandler().accenting())
			{
				boardController.put(p, ChatColor.RED + "ACCENTING: ", ChatColor.YELLOW + "" + getMapHandler().accentProgress() + "%");
			}
			
			if(!jobs.isEmpty())
			{
				int pr = 0;
				
				for(Job i : jobs.keySet())
				{
					pr+= jobs.get(i);
				}
				
				pr /= jobs.size();
				
				boardController.put(p, ChatColor.RED + "MANIPULATING: ", ChatColor.YELLOW + "" + pr + "%");
			}
		}
		
		boardController.update(p);
	}
}
