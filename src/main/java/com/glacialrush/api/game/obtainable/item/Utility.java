package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.Item;
import com.glacialrush.api.game.obtainable.ObtainableBank;
import com.glacialrush.api.game.obtainable.RuneType;

public class Utility extends Item
{
	private RuneType type;
	
	public Utility(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setItemType(ItemType.UTILITY);
	}

	public RuneType getType()
	{
		return type;
	}

	public void setType(RuneType type)
	{
		this.type = type;
	}
}
