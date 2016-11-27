package com.glacialrush.api.game.obtainable;

import com.glacialrush.api.game.obtainable.item.ObtainableType;
import com.glacialrush.api.game.obtainable.item.UpgradeType;

public class Upgrade extends Obtainable
{
	private UpgradeType upgradeType;
	
	public Upgrade(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setObtainableType(ObtainableType.UPGRADE);
	}

	public UpgradeType getUpgradeType()
	{
		return upgradeType;
	}

	public void setUpgradeType(UpgradeType upgradeType)
	{
		this.upgradeType = upgradeType;
	}
}
