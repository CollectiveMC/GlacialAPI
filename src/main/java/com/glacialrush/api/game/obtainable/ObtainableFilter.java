package com.glacialrush.api.game.obtainable;

import org.bukkit.entity.Player;
import com.glacialrush.api.game.obtainable.item.Injectable;
import com.glacialrush.api.game.obtainable.item.ItemType;
import com.glacialrush.api.game.obtainable.item.MeleeWeapon;
import com.glacialrush.api.game.obtainable.item.MeleeWeaponUpgrade;
import com.glacialrush.api.game.obtainable.item.ObtainableType;
import com.glacialrush.api.game.obtainable.item.ProjectileType;
import com.glacialrush.api.game.obtainable.item.ProjectileUpgrade;
import com.glacialrush.api.game.obtainable.item.RangedWeapon;
import com.glacialrush.api.game.obtainable.item.RangedWeaponUpgrade;
import com.glacialrush.api.game.obtainable.item.Shield;
import com.glacialrush.api.game.obtainable.item.Tool;
import com.glacialrush.api.game.obtainable.item.UpgradeType;
import com.glacialrush.api.game.obtainable.item.Utility;
import com.glacialrush.api.game.obtainable.item.Weapon;
import com.glacialrush.api.game.obtainable.item.WeaponEnclosureType;
import com.glacialrush.api.game.obtainable.item.WeaponType;
import com.glacialrush.api.object.GList;

public class ObtainableFilter
{
	private final ObtainableBank obtainableBank;
	
	public ObtainableFilter(ObtainableBank obtainableBank)
	{
		this.obtainableBank = obtainableBank;
	}
	
	public ObtainableBank getObtainableBank()
	{
		return obtainableBank;
	}
	
	public Boolean has(Player p, Obtainable o)
	{
		for(String i : obtainableBank.getPl().gpd(p).getOwned())
		{
			if(o.getId().equals(i))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean contains(String id)
	{
		for(Obtainable o : o())
		{
			if(o.getId().equals(id))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public GList<Obtainable> o()
	{
		return obtainableBank.getItems();
	}
	
	public boolean isItem(Obtainable o)
	{
		return o.getObtainableType().equals(ObtainableType.ITEM);
	}
	
	public GList<Shield> getShields()
	{
		GList<Shield> shields = new GList<Shield>();
		
		for(Item i : getItems())
		{
			if(i.getItemType().equals(ItemType.SHIELD))
			{
				shields.add((Shield) i);
			}
		}
		
		return shields;
	}
	
	public GList<RangedWeaponUpgrade> getRangedWeaponUpgrades()
	{
		GList<RangedWeaponUpgrade> us = new GList<RangedWeaponUpgrade>();
		
		for(Upgrade i : getUpgrades())
		{
			if(i.getUpgradeType().equals(UpgradeType.RANGED_WEAPON))
			{
				us.add((RangedWeaponUpgrade) i);
			}
		}
		
		return us;
	}
	
	public GList<MeleeWeaponUpgrade> getMeleeWeaponUpgrades()
	{
		GList<MeleeWeaponUpgrade> us = new GList<MeleeWeaponUpgrade>();
		
		for(Upgrade i : getUpgrades())
		{
			if(i.getUpgradeType().equals(UpgradeType.MELEE_WEAPON))
			{
				us.add((MeleeWeaponUpgrade) i);
			}
		}
		
		return us;
	}
	
	public GList<ProjectileUpgrade> getProjectileUpgrades(ProjectileType type)
	{
		GList<ProjectileUpgrade> us = new GList<ProjectileUpgrade>();
		
		for(Upgrade i : getUpgrades())
		{
			if(i.getUpgradeType().equals(UpgradeType.PROJECTILE))
			{
				ProjectileUpgrade pj = (ProjectileUpgrade) i;
				
				if(pj.getProjectileType().equals(type))
				{
					us.add(pj);
				}
			}
		}
		
		return us;
	}
	
	public boolean isWeapon(Obtainable o)
	{
		if(isItem(o))
		{
			return ((Item) o).getItemType().equals(ItemType.WEAPON);
		}
		
		return false;
	}
	
	public boolean isRangedWeapon(Obtainable o)
	{
		if(isWeapon(o))
		{
			return ((Weapon) o).getWeaponType().equals(WeaponType.RANGED);
		}
		
		return false;
	}
	
	public boolean isMeleeWeapon(Obtainable o)
	{
		if(isWeapon(o))
		{
			return ((Weapon) o).getWeaponType().equals(WeaponType.MELEE);
		}
		
		return false;
	}
	
	public boolean isPrimaryWeapon(Obtainable o)
	{
		if(isWeapon(o))
		{
			return ((Weapon) o).getWeaponEnclosureType().equals(WeaponEnclosureType.PRIMARY);
		}
		
		return false;
	}
	
	public boolean isShield(Obtainable o)
	{
		return isItem(o) && ((Item) o).getItemType().equals(ItemType.SHIELD);
	}
	
	public boolean isSecondaryWeapon(Obtainable o)
	{
		if(isWeapon(o))
		{
			return ((Weapon) o).getWeaponEnclosureType().equals(WeaponEnclosureType.SECONDARY);
		}
		
		return false;
	}
	
	public boolean isTertiaryWeapon(Obtainable o)
	{
		if(isWeapon(o))
		{
			return ((Weapon) o).getWeaponEnclosureType().equals(WeaponEnclosureType.TERTIARY);
		}
		
		return false;
	}
	
	public boolean isUtility(Obtainable o)
	{
		if(isItem(o))
		{
			return ((Item) o).getItemType().equals(ItemType.UTILITY);
		}
		
		return false;
	}
	
	public boolean isTool(Obtainable o)
	{
		if(isItem(o))
		{
			return ((Item) o).getItemType().equals(ItemType.TOOL);
		}
		
		return false;
	}
	
	public boolean isUpgrade(Obtainable o)
	{
		return o.getObtainableType().equals(ObtainableType.UPGRADE);
	}
	
	public boolean isWeaponAbility(Obtainable o)
	{
		return o.getObtainableType().equals(ObtainableType.WEAPON_ABILITY);
	}
	
	public boolean isAbility(Obtainable o)
	{
		return o.getObtainableType().equals(ObtainableType.ABILITY);
	}
	
	public boolean isProjectile(Obtainable o)
	{
		return o.getObtainableType().equals(ObtainableType.PROJECTILE);
	}
	
	public boolean isArrowProjectile(Obtainable o)
	{
		if(isProjectile(o))
		{
			return ((Projectile) o).getProjectileType().equals(ProjectileType.ARROW);
		}
		
		else
		{
			return false;
		}
	}
	
	public GList<String> getStarters()
	{
		GList<String> ids = new GList<String>();
		
		for(Obtainable o : o())
		{
			if(o.getStarter())
			{
				ids.add(o.getId());
			}
		}
		
		return ids;
	}
	
	public Obtainable resolve(String id)
	{
		for(Obtainable o : o())
		{
			if(o.getId().equals(id))
			{
				return o;
			}
		}
		
		return null;
	}
	
	public GList<Item> getItems()
	{
		GList<Item> ids = new GList<Item>();
		
		for(Obtainable o : o())
		{
			if(o.getObtainableType().equals(ObtainableType.ITEM))
			{
				ids.add((Item) o);
			}
		}
		
		return ids;
	}
	
	public GList<Weapon> getWeapons()
	{
		GList<Weapon> ids = new GList<Weapon>();
		
		for(Item i : getItems())
		{
			if(i.getItemType().equals(ItemType.WEAPON))
			{
				ids.add((Weapon) i);
			}
		}
		
		return ids;
	}
	
	public GList<Weapon> getPrimaryWeapons()
	{
		GList<Weapon> ids = new GList<Weapon>();
		
		for(Weapon i : getWeapons())
		{
			if(i.getWeaponEnclosureType().equals(WeaponEnclosureType.PRIMARY))
			{
				ids.add((Weapon) i);
			}
		}
		
		return ids;
	}
	
	public GList<Weapon> getSecondaryWeapons()
	{
		GList<Weapon> ids = new GList<Weapon>();
		
		for(Weapon i : getWeapons())
		{
			if(i.getWeaponEnclosureType().equals(WeaponEnclosureType.SECONDARY))
			{
				ids.add((Weapon) i);
			}
		}
		
		return ids;
	}
	
	public GList<Weapon> getTertiaryWeapons()
	{
		GList<Weapon> ids = new GList<Weapon>();
		
		for(Weapon i : getWeapons())
		{
			if(i.getWeaponEnclosureType().equals(WeaponEnclosureType.TERTIARY))
			{
				ids.add((Weapon) i);
			}
		}
		
		return ids;
	}
	
	public GList<RangedWeapon> getRangedWeapons()
	{
		GList<RangedWeapon> ids = new GList<RangedWeapon>();
		
		for(Weapon i : getWeapons())
		{
			if(i.getWeaponType().equals(WeaponType.RANGED))
			{
				ids.add((RangedWeapon) i);
			}
		}
		
		return ids;
	}
	
	public GList<MeleeWeapon> getMeleeWeapons()
	{
		GList<MeleeWeapon> ids = new GList<MeleeWeapon>();
		
		for(Weapon i : getWeapons())
		{
			if(i.getWeaponType().equals(WeaponType.MELEE))
			{
				ids.add((MeleeWeapon) i);
			}
		}
		
		return ids;
	}
	
	public GList<Tool> getTools()
	{
		GList<Tool> ids = new GList<Tool>();
		
		for(Item i : getItems())
		{
			if(i.getItemType().equals(ItemType.TOOL))
			{
				ids.add((Tool) i);
			}
		}
		
		return ids;
	}
	
	public GList<Injectable> getInjectables()
	{
		GList<Injectable> ids = new GList<Injectable>();
		
		for(Item i : getItems())
		{
			if(i.getItemType().equals(ItemType.INJECTABLE))
			{
				ids.add((Injectable) i);
			}
		}
		
		return ids;
	}
	
	public GList<Utility> getUtilities()
	{
		GList<Utility> ids = new GList<Utility>();
		
		for(Item i : getItems())
		{
			if(i.getItemType().equals(ItemType.UTILITY))
			{
				ids.add((Utility) i);
			}
		}
		
		return ids;
	}
	
	public GList<Projectile> getProjectiles()
	{
		GList<Projectile> ids = new GList<Projectile>();
		
		for(Obtainable o : o())
		{
			if(o.getObtainableType().equals(ObtainableType.PROJECTILE))
			{
				ids.add((Projectile) o);
			}
		}
		
		return ids;
	}
	
	public GList<Ability> getAbilities()
	{
		GList<Ability> ids = new GList<Ability>();
		
		for(Obtainable o : o())
		{
			if(o.getObtainableType().equals(ObtainableType.ABILITY))
			{
				ids.add((Ability) o);
			}
		}
		
		return ids;
	}
	
	public GList<Upgrade> getUpgrades()
	{
		GList<Upgrade> ids = new GList<Upgrade>();
		
		for(Obtainable o : o())
		{
			if(o.getObtainableType().equals(ObtainableType.UPGRADE))
			{
				ids.add((Upgrade) o);
			}
		}
		
		return ids;
	}

	public boolean isInjectable(Obtainable o)
	{
		if(isItem(o))
		{
			return ((Item) o).getItemType().equals(ItemType.INJECTABLE);
		}
		
		return false;
	}
}
