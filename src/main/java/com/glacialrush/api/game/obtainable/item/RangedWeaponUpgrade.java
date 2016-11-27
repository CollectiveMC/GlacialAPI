package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.ObtainableBank;
import com.glacialrush.api.game.obtainable.Upgrade;

public class RangedWeaponUpgrade extends Upgrade
{
	private ProjectileType projectileType;
	private Double rateOfFireModifier;
	
	public RangedWeaponUpgrade(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setUpgradeType(UpgradeType.RANGED_WEAPON);
		setRateOfFireModifier(1.0);
	}

	public ProjectileType getProjectileType()
	{
		return projectileType;
	}

	public void setProjectileType(ProjectileType projectileType)
	{
		this.projectileType = projectileType;
	}

	public Double getRateOfFireModifier()
	{
		return rateOfFireModifier;
	}

	public void setRateOfFireModifier(Double rateOfFireModifier)
	{
		this.rateOfFireModifier = rateOfFireModifier;
	}
}
