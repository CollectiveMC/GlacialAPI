package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.ObtainableBank;
import com.glacialrush.api.game.obtainable.Upgrade;

public class ProjectileUpgrade extends Upgrade
{
	private ProjectileType projectileType;
	private Double damageModifier;
	private Double velocityModifier;
	private Double ammunitionModifier;
	
	public ProjectileUpgrade(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setUpgradeType(UpgradeType.PROJECTILE);
		setDamageModifier(1.0);
		setVelocityModifier(1.0);
		setAmmunitionModifier(1.0);
	}

	public ProjectileType getProjectileType()
	{
		return projectileType;
	}

	public void setProjectileType(ProjectileType projectileType)
	{
		this.projectileType = projectileType;
	}

	public Double getDamageModifier()
	{
		return damageModifier;
	}

	public void setDamageModifier(Double damageModifier)
	{
		this.damageModifier = damageModifier;
	}

	public Double getVelocityModifier()
	{
		return velocityModifier;
	}

	public void setVelocityModifier(Double velocityModifier)
	{
		this.velocityModifier = velocityModifier;
	}

	public Double getAmmunitionModifier()
	{
		return ammunitionModifier;
	}

	public void setAmmunitionModifier(Double ammunitionModifier)
	{
		this.ammunitionModifier = ammunitionModifier;
	}
}
