package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.Item;
import com.glacialrush.api.game.obtainable.ObtainableBank;

public class Shield extends Item
{
	private ShieldType shieldType;
	private Double maxShields;
	private Double cooldown;
	private Double ticksPerShield;
	private Double mitigation;
	
	public Shield(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setItemType(ItemType.SHIELD);
	}

	public ShieldType getShieldType()
	{
		return shieldType;
	}

	public void setShieldType(ShieldType shieldType)
	{
		this.shieldType = shieldType;
	}

	public Double getMaxShields()
	{
		return maxShields;
	}

	public void setMaxShields(Double maxShields)
	{
		this.maxShields = maxShields;
	}

	public Double getCooldown()
	{
		return cooldown;
	}

	public void setCooldown(Double cooldown)
	{
		this.cooldown = cooldown;
	}

	public Double getTicksPerShield()
	{
		return ticksPerShield;
	}

	public void setTicksPerShield(Double ticksPerShield)
	{
		this.ticksPerShield = ticksPerShield;
	}

	public Double getMitigation()
	{
		return mitigation;
	}

	public void setMitigation(Double mitigation)
	{
		this.mitigation = mitigation;
	}
}
