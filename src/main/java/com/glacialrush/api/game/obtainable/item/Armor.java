package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.Item;
import com.glacialrush.api.game.obtainable.ObtainableBank;

public class Armor extends Item
{
	private Double explosiveResistance;
	private Double meleeResistance;
	private Double rangedResistance;
	private Double bonusEnergyRegen;
	
	public Armor(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setItemType(ItemType.ARMOR);
	}

	public Double getExplosiveResistance()
	{
		return explosiveResistance;
	}

	public void setExplosiveResistance(Double explosiveResistance)
	{
		this.explosiveResistance = explosiveResistance;
	}

	public Double getMeleeResistance()
	{
		return meleeResistance;
	}

	public void setMeleeResistance(Double meleeResistance)
	{
		this.meleeResistance = meleeResistance;
	}

	public Double getRangedResistance()
	{
		return rangedResistance;
	}

	public void setRangedResistance(Double rangedResistance)
	{
		this.rangedResistance = rangedResistance;
	}

	public Double getBonusEnergyRegen()
	{
		return bonusEnergyRegen;
	}

	public void setBonusEnergyRegen(Double bonusEnergyRegen)
	{
		this.bonusEnergyRegen = bonusEnergyRegen;
	}
}
