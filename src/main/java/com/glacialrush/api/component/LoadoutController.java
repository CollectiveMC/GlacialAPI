package com.glacialrush.api.component;

import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.loadout.Loadout;
import com.glacialrush.api.game.obtainable.Obtainable;

public class LoadoutController extends Controller
{
	public LoadoutController(GlacialPlugin pl)
	{
		super(pl);
	}
	
	public void applyToLoadout(Player p, Loadout loadout, Obtainable o)
	{
		if(o.getObtainableBank().getObtainableFilter().isPrimaryWeapon(o))
		{
			loadout.setPrimaryWeapon(o.getId());
		}
		
		else if(o.getObtainableBank().getObtainableFilter().isSecondaryWeapon(o))
		{
			loadout.setSecondaryWeapon(o.getId());
		}
		
		else if(o.getObtainableBank().getObtainableFilter().isTertiaryWeapon(o))
		{
			loadout.setTertiaryWeapon(o.getId());
		}
		
		else if(o.getObtainableBank().getObtainableFilter().isAbility(o))
		{
			loadout.setAbility(o.getId());
		}
		
		else if(o.getObtainableBank().getObtainableFilter().isTool(o))
		{
			loadout.setTool(o.getId());
		}
		
		else if(o.getObtainableBank().getObtainableFilter().isUtility(o))
		{
			loadout.setUtility(o.getId());
		}
		
		else if(o.getObtainableBank().getObtainableFilter().isShield(o))
		{
			loadout.setShield(o.getId());
		}
	}
}
