package com.glacialrush.api.game.object;

import java.io.Serializable;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.object.GList;
import com.glacialrush.xapi.ColorUtils;

import org.bukkit.ChatColor;

public class Faction implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final String name;
	private final ChatColor color;
	private final DyeColor dyeColor;
	private final DyeColor darkDye;
	
	private final static Faction neutral = new Faction("Neutral", ChatColor.AQUA, DyeColor.BROWN);
	private final static Faction omni = new Faction("Omni", ChatColor.DARK_PURPLE, DyeColor.BROWN);
	private final static Faction enigma = new Faction("Enigma", ChatColor.RED, DyeColor.BROWN);
	private final static Faction cryptic = new Faction("Cryptic", ChatColor.YELLOW, DyeColor.BROWN);
	
	public Faction(String name, ChatColor color, DyeColor darkDye)
	{
		this.name = name;
		this.color = color;
		this.darkDye = darkDye;
		this.dyeColor = ColorUtils.chatToDye(color);
	}
	
	public DyeColor getDarkDye()
	{
		return darkDye;
	}
	
	public static Faction getNeutral()
	{
		return neutral;
	}
	
	public static Faction getOmni()
	{
		return omni;
	}
	
	public static Faction getEnigma()
	{
		return enigma;
	}
	
	public static Faction getCryptic()
	{
		return cryptic;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ChatColor getColor()
	{
		return color;
	}
	
	public DyeColor getDyeColor()
	{
		return dyeColor;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		
		if(obj instanceof Faction)
		{
			Faction f = (Faction) obj;
			
			if(f.getColor().equals(getColor()) && f.getName().equals(getName()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static Faction get(Player p)
	{
		for(RegionedGame i : GlacialPlugin.instance().getGameControl().getRegionedGames())
		{
			if(i.players().contains(p))
			{
				return i.getFactionHandler().getFaction(p);
			}
		}
		
		return null;
	}
	
	public static Faction neutral()
	{
		return neutral;
	}
	
	public static Faction omni()
	{
		return omni;
	}
	
	public static Faction enigma()
	{
		return enigma;
	}
	
	public static Faction cryptic()
	{
		return cryptic;
	}
	
	public static Faction random()
	{
		return new GList<Faction>().qadd(cryptic).qadd(enigma).qadd(omni).pickRandom();
	}
	
	public static GList<Faction> all()
	{
		return new GList<Faction>().qadd(cryptic).qadd(enigma).qadd(omni);
	}
	
	public static GList<Faction> allwithNeutral()
	{
		return new GList<Faction>().qadd(cryptic).qadd(enigma).qadd(omni).qadd(neutral);
	}
}
