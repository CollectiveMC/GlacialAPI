package com.glacialrush.api.game.obtainable;

import com.glacialrush.api.game.obtainable.item.ObtainableType;
import com.glacialrush.api.game.obtainable.item.ProjectileType;

public class Projectile extends Obtainable
{
	private ProjectileType projectileType;
	private Double damage;
	private Double velocity;
	private Double ammunitionCount;
	
	public Projectile(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setObtainableType(ObtainableType.PROJECTILE);
	}
	
	public Double getAmmunitionCount()
	{
		return ammunitionCount;
	}
	
	public void setAmmunitionCount(Double ammunitionCount)
	{
		this.ammunitionCount = ammunitionCount;
	}
	
	public ProjectileType getProjectileType()
	{
		return projectileType;
	}
	
	public void setProjectileType(ProjectileType projectileType)
	{
		this.projectileType = projectileType;
	}
	
	public Double getDamage()
	{
		return damage;
	}
	
	public void setDamage(Double damage)
	{
		this.damage = damage;
	}
	
	public Double getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(Double velocity)
	{
		this.velocity = velocity;
	}
}
