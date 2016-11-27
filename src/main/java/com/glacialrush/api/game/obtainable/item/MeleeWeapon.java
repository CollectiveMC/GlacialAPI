package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.ObtainableBank;

public class MeleeWeapon extends Weapon
{
	private Double damage;
	
	public MeleeWeapon(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setWeaponType(WeaponType.MELEE);
	}

	public Double getDamage()
	{
		return damage;
	}

	public void setDamage(Double damage)
	{
		this.damage = damage;
	}
}
