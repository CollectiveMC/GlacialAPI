package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.ObtainableBank;

public class RangedWeapon extends Weapon
{
	private ProjectileType projectileType;
	private Double rateOfFire;
	private Double damageMultiplier;
	private Integer ammunition;
	private Boolean automatic;
	
	public RangedWeapon(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setWeaponType(WeaponType.RANGED);
	}
	
	public Integer getAmmunition()
	{
		return ammunition;
	}
	
	public void setAmmunition(Integer ammunition)
	{
		this.ammunition = ammunition;
	}
	
	public Double getDamageMultiplier()
	{
		return damageMultiplier;
	}
	
	public void setDamageMultiplier(Double damageMultiplier)
	{
		this.damageMultiplier = damageMultiplier;
	}
	
	public ProjectileType getProjectileType()
	{
		return projectileType;
	}
	
	public void setProjectileType(ProjectileType projectileType)
	{
		this.projectileType = projectileType;
	}
	
	public Double getRateOfFire()
	{
		return rateOfFire;
	}
	
	public void setRateOfFire(Double rateOfFire)
	{
		this.rateOfFire = rateOfFire;
	}
	
	public Boolean getAutomatic()
	{
		return automatic;
	}
	
	public void setAutomatic(Boolean automatic)
	{
		this.automatic = automatic;
	}
}
