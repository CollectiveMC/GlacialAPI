package com.glacialrush.api.game.object;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.DyeColor;

import com.glacialrush.xapi.ColorUtils;

import org.bukkit.ChatColor;

public enum Greek
{
	ALPHA('\u0391', DyeColor.BLUE),
	BETA('\u0392', DyeColor.GREEN),
	GAMMA('\u0393', DyeColor.ORANGE),
	DELTA('\u0394', DyeColor.LIGHT_BLUE),
	EPSILON('\u03F6', DyeColor.PINK),
	LAMBDA('\u03BB', DyeColor.PURPLE),
	SIGMA('\u03A3', DyeColor.LIME),
	OMEGA('\u03A9', DyeColor.MAGENTA);
	
	private DyeColor color;
	private Character symbol;
	
	private Greek(char symbol, DyeColor color)
	{
		this.color = color;
		this.symbol = symbol;
	}
	
	public String fName()
	{
		return StringUtils.capitalize(toString());
	}
	
	public String symbol()
	{
		return symbol.toString();
	}
	
	public DyeColor dye()
	{
		return color;
	}
	
	public ChatColor color()
	{
		return ColorUtils.dyeToChat(color);
	}
}
