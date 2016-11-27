package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.Item;
import com.glacialrush.api.game.obtainable.ObtainableBank;

public class Injectable extends Item
{
	private Double power;
	private InjectableType injectableType;
	
	public Injectable(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setItemType(ItemType.INJECTABLE);
	}
	
	public Double getPower()
	{
		return power;
	}
	
	public void setPower(Double power)
	{
		this.power = power;
	}

	public InjectableType getInjectableType()
	{
		return injectableType;
	}

	public void setInjectableType(InjectableType injectableType)
	{
		this.injectableType = injectableType;
	}
}
