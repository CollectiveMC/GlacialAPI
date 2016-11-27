package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.ObtainableBank;
import com.glacialrush.api.game.obtainable.Upgrade;

public class MeleeWeaponUpgrade extends Upgrade
{
	private Double damageModifier;
	
	public MeleeWeaponUpgrade(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setUpgradeType(UpgradeType.MELEE_WEAPON);
		setDamageModifier(1.0);
	}

	public Double getDamageModifier()
	{
		return damageModifier;
	}

	public void setDamageModifier(Double damageModifier)
	{
		this.damageModifier = damageModifier;
	}
}
