package com.glacialrush.packet;

import org.bukkit.entity.Player;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

public class NCP
{
	public static void exemptFly(Player p)
	{
		NCPExemptionManager.exemptPermanently(p, CheckType.MOVING_SURVIVALFLY);
	}
	
	public static void unExemptFly(Player p)
	{
		NCPExemptionManager.unexempt(p, CheckType.MOVING_SURVIVALFLY);
	}
}
