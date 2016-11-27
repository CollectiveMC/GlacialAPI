package com.glacialrush.api.game.loadout;

import java.io.Serializable;
import org.bukkit.configuration.file.FileConfiguration;
import com.glacialrush.api.object.GList;

public class Loadout implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String primaryWeapon;
	private String secondaryWeapon;
	private String tertiaryWeapon;
	private String projectile;
	private String ability;
	private String tool;
	private String utility;
	private String shield;
	private String sMOD;
	
	private GList<String> primaryWeaponUpgrades;
	private GList<String> secondaryWeaponUpgrades;
	private GList<String> tertiaryWeaponUpgrades;
	private GList<String> projectileUpgrades;
	private GList<String> abilityUpgrades;
	private GList<String> toolUpgrades;
	private GList<String> utilityUpgrades;
	private GList<String> shieldUpgrades;
	private GList<String> sMODUpgrades;
	
	public Loadout()
	{
		primaryWeapon = "none";
		secondaryWeapon = "none";
		tertiaryWeapon = "none";
		projectile = "none";
		ability = "none";
		tool = "none";
		utility = "none";
		shield = "none";
		sMOD = "none";
		
		primaryWeaponUpgrades = new GList<String>().qadd("none");
		secondaryWeaponUpgrades = new GList<String>().qadd("none");
		tertiaryWeaponUpgrades = new GList<String>().qadd("none");
		projectileUpgrades = new GList<String>().qadd("none");
		abilityUpgrades = new GList<String>().qadd("none");
		toolUpgrades = new GList<String>().qadd("none");
		utilityUpgrades = new GList<String>().qadd("none");
		shieldUpgrades = new GList<String>().qadd("none");
		sMODUpgrades = new GList<String>().qadd("none");
	}
	
	public Loadout(FileConfiguration fc, String key)
	{
		primaryWeapon = fc.getString(key + ".weapon.primary.item");
		secondaryWeapon = fc.getString(key + ".weapon.secondary.item");
		tertiaryWeapon = fc.getString(key + ".weapon.tertiary.item");
		projectile = fc.getString(key + ".weapon.projectile.item");
		ability = fc.getString(key + ".ability.item");
		tool = fc.getString(key + ".auxiliary.tool.item");
		utility = fc.getString(key + ".auxiliary.utility.item");
		shield = fc.getString(key + ".auxiliary.shield.item");
		sMOD = fc.getString(key + ".auxiliary.smod.item");
		
		primaryWeaponUpgrades = new GList<String>(fc.getStringList(key + ".weapon.primary.upgrades"));
		secondaryWeaponUpgrades = new GList<String>(fc.getStringList(key + ".weapon.secondary.upgrades"));
		tertiaryWeaponUpgrades = new GList<String>(fc.getStringList(key + ".weapon.tertiary.upgrades"));
		projectileUpgrades = new GList<String>(fc.getStringList(key + ".weapon.projectile.upgrades"));
		abilityUpgrades = new GList<String>(fc.getStringList(key + ".ability.upgrades"));
		toolUpgrades = new GList<String>(fc.getStringList(key + ".auxiliary.tool.upgrades"));
		utilityUpgrades = new GList<String>(fc.getStringList(key + ".auxiliary.utility.upgrades"));
		shieldUpgrades = new GList<String>(fc.getStringList(key + ".auxiliary.shield.upgrades"));
		sMODUpgrades = new GList<String>(fc.getStringList(key + ".auxiliary.smod.upgrades"));
	}
	
	public FileConfiguration yml(FileConfiguration fc, String key)
	{
		fc.set(key + ".weapon.primary.item", primaryWeapon);
		fc.set(key + ".weapon.secondary.item", secondaryWeapon);
		fc.set(key + ".weapon.tertiary.item", tertiaryWeapon);
		fc.set(key + ".weapon.projectile.item", projectile);
		fc.set(key + ".ability.item", ability);
		fc.set(key + ".auxiliary.tool.item", tool);
		fc.set(key + ".auxiliary.utility.item", utility);
		fc.set(key + ".auxiliary.shield.item", shield);
		fc.set(key + ".auxiliary.smod.item", sMOD);
		
		fc.set(key + ".weapon.primary.upgrades", primaryWeaponUpgrades);
		fc.set(key + ".weapon.secondary.upgrades", secondaryWeaponUpgrades);
		fc.set(key + ".weapon.tertiary.upgrades", tertiaryWeaponUpgrades);
		fc.set(key + ".weapon.projectile.upgrades", projectileUpgrades);
		fc.set(key + ".ability.upgrades", abilityUpgrades);
		fc.set(key + ".auxiliary.tool.upgrades", toolUpgrades);
		fc.set(key + ".auxiliary.shield.upgrades", shieldUpgrades);
		fc.set(key + ".auxiliary.utility.upgrades", utilityUpgrades);
		fc.set(key + ".auxiliary.smod.upgrades", sMODUpgrades);
		
		return fc;
	}
	
	public GList<String> getShieldUpgrades()
	{
		return shieldUpgrades;
	}

	public void setShieldUpgrades(GList<String> shieldUpgrades)
	{
		this.shieldUpgrades = shieldUpgrades;
	}

	public String getProjectile()
	{
		return projectile;
	}
	
	public void setProjectile(String projectile)
	{
		this.projectile = projectile;
	}
	
	public GList<String> getProjectileUpgrades()
	{
		return projectileUpgrades;
	}
	
	public void setProjectileUpgrades(GList<String> projectileUpgrades)
	{
		this.projectileUpgrades = projectileUpgrades;
	}
	
	public String getPrimaryWeapon()
	{
		return primaryWeapon;
	}
	
	public void setPrimaryWeapon(String primaryWeapon)
	{
		this.primaryWeapon = primaryWeapon;
	}
	
	public String getSecondaryWeapon()
	{
		return secondaryWeapon;
	}
	
	public void setSecondaryWeapon(String secondaryWeapon)
	{
		this.secondaryWeapon = secondaryWeapon;
	}
	
	public String getTertiaryWeapon()
	{
		return tertiaryWeapon;
	}
	
	public void setTertiaryWeapon(String tertiaryWeapon)
	{
		this.tertiaryWeapon = tertiaryWeapon;
	}
	
	public String getAbility()
	{
		return ability;
	}
	
	public void setAbility(String ability)
	{
		this.ability = ability;
	}
	
	public String getTool()
	{
		return tool;
	}
	
	public void setTool(String tool)
	{
		this.tool = tool;
	}
	
	public String getUtility()
	{
		return utility;
	}
	
	public void setUtility(String utility)
	{
		this.utility = utility;
	}
	
	public GList<String> getAbilityUpgrades()
	{
		return abilityUpgrades;
	}
	
	public GList<String> getPrimaryWeaponUpgrades()
	{
		return primaryWeaponUpgrades;
	}
	
	public void setPrimaryWeaponUpgrades(GList<String> primaryWeaponUpgrades)
	{
		this.primaryWeaponUpgrades = primaryWeaponUpgrades;
	}
	
	public GList<String> getSecondaryWeaponUpgrades()
	{
		return secondaryWeaponUpgrades;
	}
	
	public void setSecondaryWeaponUpgrades(GList<String> secondaryWeaponUpgrades)
	{
		this.secondaryWeaponUpgrades = secondaryWeaponUpgrades;
	}
	
	public GList<String> getTertiaryWeaponUpgrades()
	{
		return tertiaryWeaponUpgrades;
	}
	
	public void setTertiaryWeaponUpgrades(GList<String> tertiaryWeaponUpgrades)
	{
		this.tertiaryWeaponUpgrades = tertiaryWeaponUpgrades;
	}
	
	public GList<String> getActiveUpgrades()
	{
		return abilityUpgrades;
	}
	
	public void setAbilityUpgrades(GList<String> abilityUpgrades)
	{
		this.abilityUpgrades = abilityUpgrades;
	}
	
	public GList<String> getToolUpgrades()
	{
		return toolUpgrades;
	}
	
	public void setToolUpgrades(GList<String> toolUpgrades)
	{
		this.toolUpgrades = toolUpgrades;
	}
	
	public GList<String> getUtilityUpgrades()
	{
		return utilityUpgrades;
	}
	
	public void setUtilityUpgrades(GList<String> utilityUpgrades)
	{
		this.utilityUpgrades = utilityUpgrades;
	}

	public String getShield()
	{
		return shield;
	}

	public void setShield(String shield)
	{
		this.shield = shield;
	}

	public String getsMOD()
	{
		return sMOD;
	}

	public void setsMOD(String sMOD)
	{
		this.sMOD = sMOD;
	}

	public GList<String> getsMODUpgrades()
	{
		return sMODUpgrades;
	}

	public void setsMODUpgrades(GList<String> sMODUpgrades)
	{
		this.sMODUpgrades = sMODUpgrades;
	}
}
