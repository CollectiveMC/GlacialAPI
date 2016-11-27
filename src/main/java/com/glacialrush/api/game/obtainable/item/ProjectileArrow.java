package com.glacialrush.api.game.obtainable.item;

import com.glacialrush.api.game.obtainable.ObtainableBank;
import com.glacialrush.api.game.obtainable.Projectile;

public class ProjectileArrow extends Projectile
{
	public ProjectileArrow(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setProjectileType(ProjectileType.ARROW);
	}
}
