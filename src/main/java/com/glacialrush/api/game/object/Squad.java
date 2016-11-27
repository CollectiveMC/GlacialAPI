package com.glacialrush.api.game.object;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.glacialrush.api.gui.Element;
import com.glacialrush.api.gui.Pane;
import com.glacialrush.api.map.Chunklet;
import com.glacialrush.api.object.GList;

import org.bukkit.ChatColor;

public class Squad
{
	private Player leader;
	private GList<Player> members;
	private String objective;
	private String description;
	private Chunklet beacon;
	private Faction faction;
	private Greek greek;
	
	public Squad(Player leader, Faction faction, Greek greek)
	{
		this.leader = leader;
		this.faction = faction;
		this.greek = greek;
		this.members = new GList<Player>().qadd(leader);
		this.description = "No Description.";
		this.objective = "No Objective.";
		this.beacon = null;
	}
	
	public Element configure(Pane pane, Player p, int s)
	{
		Element e = new Element(pane, getColor() + "[" + greek.symbol() + "] " + greek.fName() + " Squad", Material.STAINED_GLASS_PANE, s);
		
		e.addLore(ChatColor.AQUA + "Description: " + getColor() + description);
		e.addLore(ChatColor.AQUA + "Objective: " + getColor() + objective);
		e.addLore(ChatColor.AQUA + "Members: " + getColor() + members.size());
		
		e.addLore(ChatColor.AQUA + "" + '\u2771' + "" + '\u2771' + " " + faction.getColor() + leader.getName());
		
		for(Player i : members.copy().qdel(leader))
		{
			e.addLore(ChatColor.AQUA + "" + '\u2771' + " " + faction.getColor() + i.getName());
		}
		
		return e;
	}
	
	public Faction getFaction()
	{
		return faction;
	}

	public void setFaction(Faction faction)
	{
		this.faction = faction;
	}

	public DyeColor getDye()
	{
		return greek.dye();
	}
	
	public ChatColor getColor()
	{
		return greek.color();
	}

	public Player getLeader()
	{
		return leader;
	}

	public void setLeader(Player leader)
	{
		this.leader = leader;
	}

	public GList<Player> getMembers()
	{
		return members;
	}

	public void setMembers(GList<Player> members)
	{
		this.members = members;
	}

	public String getObjective()
	{
		return objective;
	}

	public void setObjective(String objective)
	{
		this.objective = objective;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Chunklet getBeacon()
	{
		return beacon;
	}

	public void setBeacon(Chunklet beacon)
	{
		this.beacon = beacon;
	}

	public Greek getGreek()
	{
		return greek;
	}

	public void setGreek(Greek greek)
	{
		this.greek = greek;
	}
}
