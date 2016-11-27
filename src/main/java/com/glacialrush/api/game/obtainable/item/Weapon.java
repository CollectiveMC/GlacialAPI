package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.Item;
import com.glacialrush.api.game.obtainable.ObtainableBank;

public class Weapon extends Item
{
	private WeaponType weaponType;
	private WeaponEnclosureType weaponEnclosureType;
	private WeaponEffect weaponEffect;
	
	public Weapon(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setItemType(ItemType.WEAPON);
		setWeaponEffect(WeaponEffect.NONE);
	}

	public WeaponType getWeaponType()
	{
		return weaponType;
	}

	public void setWeaponType(WeaponType weaponType)
	{
		this.weaponType = weaponType;
	}

	public WeaponEnclosureType getWeaponEnclosureType()
	{
		return weaponEnclosureType;
	}

	public void setWeaponEnclosureType(WeaponEnclosureType weaponEnclosureType)
	{
		this.weaponEnclosureType = weaponEnclosureType;
	}

	public WeaponEffect getWeaponEffect()
	{
		return weaponEffect;
	}

	public void setWeaponEffect(WeaponEffect weaponEffect)
	{
		this.weaponEffect = weaponEffect;
	}
}
