package com.glacialrush.api.rank;

import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.object.GList;
import org.bukkit.ChatColor;

public enum Rank
{
	COLD(ChatColor.GRAY + "Cold", ChatColor.GRAY + "", ChatColor.GRAY + "", 0, 0, 0, (Rank[]) null), 
	ICE(ChatColor.DARK_AQUA + "ICE", ChatColor.DARK_AQUA + "", ChatColor.WHITE + "", 100, 1, 0.2, COLD),
	ARCTIC(ChatColor.AQUA + "Arctic", ChatColor.AQUA + "", ChatColor.WHITE + "", 8000, 2, 0.7, ICE, COLD),
	GLACIAL(ChatColor.AQUA + "Glacial", ChatColor.AQUA + "" + ChatColor.BOLD, ChatColor.WHITE + "", 16000, 3, 1.0, ARCTIC, ICE, COLD),
	MOD(ChatColor.RED + "Mod", ChatColor.WHITE + "" + ChatColor.BOLD, ChatColor.WHITE + "" + ChatColor.BOLD, 0, 4, 0, COLD), 
	BUILDER(ChatColor.GREEN + "Builder", ChatColor.GRAY + "", ChatColor.GRAY + "", 0, 5, 0, COLD),
	OWNER(ChatColor.RED + "Owner", ChatColor.AQUA + "" + ChatColor.BOLD, ChatColor.WHITE + "" + ChatColor.BOLD, 0, 100, 0, MOD);
	
	private final String name;
	private final String chatPrefix;
	private final String chatSuffix;
	private final Rank[] inheritance;
	private final Integer cost;
	private final Integer power;
	private final Double boost;
	
	private Rank(String name, String chatPrefix, String chatSuffix, int cost, int power, double boost, Rank... inheritance)
	{
		this.power = power;
		this.name = name;
		this.chatPrefix = chatPrefix;
		this.cost = cost;
		this.boost = boost;
		this.chatSuffix = chatSuffix;
		
		if(inheritance != null)
		{
			this.inheritance = inheritance;
		}
		
		else
		{
			this.inheritance = new Rank[0];
		}
	}
	
	
	
	public Double getBoost()
	{
		return boost;
	}



	public Integer getPower()
	{
		return power;
	}
	
	public static Rank best(GList<Rank> rks)
	{
		int p = -1;
		Rank r = null;
		
		for(Rank i : rks)
		{
			if(i.getPower() > p)
			{
				r = i;
				p = r.getPower();
			}
		}
		
		if(r == null)
		{
			r = Rank.COLD;
		}
		
		return r;
	}
	
	public static GList<Rank> forPlayer(Player p)
	{
		GList<Rank> f = new GList<Rank>();
		PlayerData d = GlacialPlugin.instance().gpd(p);
		
		for(Rank i : Rank.values())
		{
			if(i.getCost() > 0)
			{
				if(!d.getRanks().contains(i))
				{
					f.add(i);
				}
			}
		}
		
		return f;
	}
	
	public Integer getCost()
	{
		return cost;
	}
	
	public boolean is(Rank rank)
	{
		if(this.equals(rank))
		{
			return true;
		}
		
		for(Rank i : inheritance)
		{
			if(i.equals(rank))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getChatSuffix()
	{
		return chatSuffix;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getChatPrefix()
	{
		return chatPrefix;
	}
	
	public Rank[] getInheritance()
	{
		return inheritance;
	}
}
