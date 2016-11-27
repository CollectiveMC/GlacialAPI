package com.glacialrush.api.game.obtainable.item;

import org.bukkit.Material;
import com.glacialrush.api.game.obtainable.Item;
import com.glacialrush.api.game.obtainable.ObtainableBank;

public class Tool extends Item
{
	private ToolType toolType;
	private Integer cooldown;
	private Material usedMaterial;
	
	public Tool(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setItemType(ItemType.TOOL);
	}

	public ToolType getToolType()
	{
		return toolType;
	}

	public void setToolType(ToolType toolType)
	{
		this.toolType = toolType;
	}

	public Integer getCooldown()
	{
		return cooldown;
	}

	public void setCooldown(Integer cooldown)
	{
		this.cooldown = cooldown;
	}

	public Material getUsedMaterial()
	{
		return usedMaterial;
	}

	public void setUsedMaterial(Material usedMaterial)
	{
		this.usedMaterial = usedMaterial;
	}
}
