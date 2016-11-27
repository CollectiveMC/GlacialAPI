package com.glacialrush.api.game.obtainable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.InnerDispatcher;
import com.glacialrush.api.game.obtainable.item.AbilityEffect;
import com.glacialrush.api.game.obtainable.item.Injectable;
import com.glacialrush.api.game.obtainable.item.InjectableType;
import com.glacialrush.api.game.obtainable.item.ItemType;
import com.glacialrush.api.game.obtainable.item.MeleeWeapon;
import com.glacialrush.api.game.obtainable.item.MeleeWeaponUpgrade;
import com.glacialrush.api.game.obtainable.item.ObtainableType;
import com.glacialrush.api.game.obtainable.item.ProjectileArrow;
import com.glacialrush.api.game.obtainable.item.ProjectileType;
import com.glacialrush.api.game.obtainable.item.ProjectileUpgrade;
import com.glacialrush.api.game.obtainable.item.RangedWeapon;
import com.glacialrush.api.game.obtainable.item.RangedWeaponUpgrade;
import com.glacialrush.api.game.obtainable.item.Shield;
import com.glacialrush.api.game.obtainable.item.ShieldType;
import com.glacialrush.api.game.obtainable.item.Tool;
import com.glacialrush.api.game.obtainable.item.ToolType;
import com.glacialrush.api.game.obtainable.item.UpgradeType;
import com.glacialrush.api.game.obtainable.item.Utility;
import com.glacialrush.api.game.obtainable.item.Weapon;
import com.glacialrush.api.game.obtainable.item.WeaponEffect;
import com.glacialrush.api.game.obtainable.item.WeaponEnclosureType;
import com.glacialrush.api.game.obtainable.item.WeaponType;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.sfx.Audio;

public class ObtainableDataManager
{
	private final InnerDispatcher d;
	private final ObtainableFilter filter;
	private final File base;
	
	public ObtainableDataManager(GlacialPlugin pl, ObtainableFilter filter)
	{
		this.filter = filter;
		this.d = new InnerDispatcher(pl, "ODMX");
		this.base = new File(new File(pl.getDataFolder(), "gameobject"), "item");
	}
	
	public void load()
	{
		if(!new File(base, "injections").exists())
		{
			new File(base, "injections").mkdirs();
		}
		
		for(File i : base.listFiles())
		{
			load(i);
		}
		
		for(File i : new File(base, "injections").listFiles())
		{
			d.overbose("Creating gameobject: " + i.getPath());
			
			load(i);
			i.delete();
		}
	}
	
	public void generateDemo()
	{
		GList<Obtainable> obdm = new GList<Obtainable>();
		
		MeleeWeapon mw = new MeleeWeapon(filter.getObtainableBank());
		mw.setCost(0);
		mw.setStarter(false);
		mw.setMaterial(Material.STICK);
		mw.setName("MeleeWeapon");
		mw.setWeaponEnclosureType(WeaponEnclosureType.PRIMARY);
		mw.setDamage(0.0);
		mw.setDescription("description of weapon");
		mw.setId("id");
		obdm.add(mw);
		
		RangedWeapon rw = new RangedWeapon(filter.getObtainableBank());
		rw.setCost(0);
		rw.setStarter(false);
		rw.setMaterial(Material.STICK);
		rw.setName("RangedWeapon");
		rw.setWeaponEnclosureType(WeaponEnclosureType.SECONDARY);
		rw.setProjectileType(ProjectileType.ARROW);
		rw.setDescription("description of weapon");
		rw.setId("id");
		rw.setRateOfFire(30.0);
		rw.setAmmunition(48);
		rw.setDamageMultiplier(1.0);
		rw.setAutomatic(false);
		obdm.add(rw);
		
		//TODO demos
		
		Shield ss = new Shield(filter.getObtainableBank());
		ss.setCost(0);
		ss.setStarter(false);
		ss.setName("Shield Manipulator");
		ss.setDescription("Shield description");
		ss.setId("id");
		ss.setCooldown(5.0);
		ss.setMaxShields(10.0);
		ss.setShieldType(ShieldType.NORMAL);
		ss.setMaterial(Material.IRON_INGOT);
		ss.setMitigation(1000.0);
		ss.setTicksPerShield(1.0);
		obdm.add(ss);
		
		Injectable ssx = new Injectable(filter.getObtainableBank());
		ssx.setCost(0);
		ssx.setStarter(false);
		ssx.setName("Stimpack");
		ssx.setDescription("Health pack");
		ssx.setId("id");
		ssx.setMaterial(Material.GOLD_RECORD);
		ssx.setInjectableType(InjectableType.HEALTH);
		ssx.setPower(7.0);
		obdm.add(ssx);
		
		Ability ab = new Ability(filter.getObtainableBank());
		ab.setCost(0);
		ab.setStarter(false);
		ab.setName("Ability");
		ab.setAbilityEffect(AbilityEffect.STRENGTH);
		ab.setDescription("Abilitiy description");
		ab.setId("id");
		ab.setAbilityFiredSound(Audio.ABILITY_RAGE);
		ab.setCooldown(20);
		ab.setDuration(10);
		obdm.add(ab);
		
		ProjectileArrow ap = new ProjectileArrow(filter.getObtainableBank());
		ap.setCost(0);
		ap.setStarter(false);
		ap.setName("ProjectileArrow");
		ap.setDescription("description of ProjectileArrow");
		ap.setId("id");
		ap.setDamage(0.0);
		obdm.add(ap);
		
		ProjectileUpgrade pu = new ProjectileUpgrade(filter.getObtainableBank());
		pu.setCost(0);
		pu.setStarter(false);
		pu.setName("ProjectileUpgrade");
		pu.setDescription("description of ProjectileUpgrade");
		pu.setId("id");
		pu.setAmmunitionModifier(1.0);
		pu.setDamageModifier(1.0);
		pu.setVelocityModifier(1.0);
		pu.setProjectileType(ProjectileType.ARROW);
		obdm.add(pu);
		
		RangedWeaponUpgrade rwu = new RangedWeaponUpgrade(filter.getObtainableBank());
		rwu.setCost(0);
		rwu.setStarter(false);
		rwu.setName("RangedWeaponUpgrade");
		rwu.setDescription("description of ProjectileUpgrade");
		rwu.setId("id");
		rwu.setProjectileType(ProjectileType.ARROW);
		rwu.setRateOfFireModifier(1.0);
		obdm.add(rwu);
		
		MeleeWeaponUpgrade mwu = new MeleeWeaponUpgrade(filter.getObtainableBank());
		mwu.setCost(0);
		mwu.setStarter(false);
		mwu.setName("MeleeWeaponUpgrade");
		mwu.setDescription("description of ProjectileUpgrade");
		mwu.setId("id");
		mwu.setDamageModifier(1.0);
		obdm.add(mwu);
				
		Tool tt = new Tool(filter.getObtainableBank());
		tt.setCost(0);
		tt.setStarter(false);
		tt.setName("Tool");
		tt.setDescription("description of Tool");
		tt.setId("id");
		tt.setCooldown(20);
		tt.setMaterial(Material.STICK);
		tt.setToolType(ToolType.GRAPPLE);
		tt.setUsedMaterial(Material.BLAZE_ROD);
		obdm.add(tt);
		
		Utility up = new Utility(filter.getObtainableBank());
		up.setCost(0);
		up.setStarter(false);
		up.setName("Rune");
		up.setDescription("description of rune");
		up.setId("id");
		up.setType(RuneType.PERSISTANCE);
		up.setMaterial(Material.STICK);
		obdm.add(up);
		
		File ffm = new File(base, "demo");
		
		for(Obtainable i : obdm)
		{
			FileConfiguration fc = yaml(i);
			File f = new File(ffm, i.getName() + ".yml");
			
			try
			{
				fc.save(f);
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void load(File file)
	{
		if(file.isDirectory())
		{
			d.warning("  Invalid gameobject not a file: " + file.getPath());
			return;
		}
		
		if(!file.getName().endsWith(".yml"))
		{
			d.warning("  Invalid gameobject extention: " + file.getPath());
			return;
		}
		
		if(file.getName().endsWith("-id.yml"))
		{
			d.warning("  Invalid gameobject extention: " + file.getPath());
			return;
		}
		
		FileConfiguration fc = new YamlConfiguration();
		
		try
		{
			fc.load(file);
			d.success("Loaded gameobject: " + fc.getString("game-object.name") + " - " + fc.getString("game-object.hash"));
			inject(fc);
		}
		
		catch(FileNotFoundException e)
		{
			d.failure("FAILED TO LOAD GAMEOBJECT <FNF>: " + file.getPath());
		}
		
		catch(IOException e)
		{
			d.failure("FAILED TO LOAD GAMEOBJECT <IOEX>: " + file.getPath());
		}
		
		catch(InvalidConfigurationException e)
		{
			d.failure("FAILED TO LOAD GAMEOBJECT <INVALID>: " + file.getPath());
		}
	}
	
	public void inject(FileConfiguration fc)
	{
		String name = fc.getString("game-object.name");
		String description = fc.getString("game-object.description");
		String id = fc.getString("game-object.hash");
		Boolean starter = fc.getBoolean("game-object.starter");
		Integer cost = fc.getInt("game-object.cost");
		ObtainableType ot = ObtainableType.valueOf(fc.getString("game-object.type"));
		
		if(id.equals("id"))
		{
			id = IDGenerator.nextID(13, 37);
		}
		
		if(ot != null)
		{
			if(ot.equals(ObtainableType.ITEM))
			{
				ItemType it = ItemType.valueOf(fc.getString("game-object.item.type"));
				Material im = Material.valueOf(fc.getString("game-object.item.material"));
				Byte ib = (byte) fc.getInt("game-object.item.metadata");
								
				if(it != null)
				{
					if(it.equals(ItemType.WEAPON))
					{
						WeaponType wt = WeaponType.valueOf(fc.getString("game-object.item.weapon.type"));
						WeaponEnclosureType wet = WeaponEnclosureType.valueOf(fc.getString("game-object.item.weapon.enclosure"));
						WeaponEffect we = WeaponEffect.valueOf(fc.getString("game-object.item.weapon.effect"));
						
						if(wt != null)
						{							
							if(wet != null)
							{								
								if(wt.equals(WeaponType.MELEE))
								{
									Double damage = fc.getDouble("game-object.item.weapon.melee.damage");
																		
									MeleeWeapon mw = new MeleeWeapon(filter.getObtainableBank());
									mw.setCost(cost);
									mw.setDamage(damage);
									mw.setDescription(description);
									mw.setId(id);
									mw.setMaterial(im);
									mw.setStarter(starter);
									mw.setMaterialMeta(ib);
									mw.setWeaponEnclosureType(wet);
									mw.setName(name);
									mw.setWeaponEffect(we);
									
									filter.getObtainableBank().add(mw);
								}
								
								else if(wt.equals(WeaponType.RANGED))
								{
									ProjectileType pt = ProjectileType.valueOf(fc.getString("game-object.item.weapon.ranged.projectile.type"));
									Double rpm = fc.getDouble("game-object.item.weapon.ranged.rate-of-fire");
									Double damageMultiplier = fc.getDouble("game-object.item.weapon.ranged.damage-multiplier");
									Boolean auto = fc.getBoolean("game-object.item.weapon.ranged.automatic");
									Integer amm = fc.getInt("game-object.item.weapon.ranged.ammunition");
									
									if(pt != null)
									{										
										RangedWeapon rw = new RangedWeapon(filter.getObtainableBank());
										rw.setCost(cost);
										rw.setDescription(description);
										rw.setId(id);
										rw.setMaterial(im);
										rw.setStarter(starter);
										rw.setMaterialMeta(ib);
										rw.setWeaponEnclosureType(wet);
										rw.setProjectileType(pt);
										rw.setRateOfFire(rpm);
										rw.setDamageMultiplier(damageMultiplier);
										rw.setAutomatic(auto);
										rw.setName(name);
										rw.setAmmunition(amm);
										rw.setWeaponEffect(we);
										
										filter.getObtainableBank().add(rw);
									}
									
									else
									{
										d.failure("     Failed to parse object: weapon-ranged: projectile-type (null)");
									}
								}
								
								else
								{
									d.failure("    Failed to parse object: weapon-type (unknown)");
								}
							}
							
							else
							{
								d.failure("    Failed to parse object: weapon-enclosure-type (null)");
							}
						}
						
						else
						{
							d.failure("    Failed to parse object: weapon-type (null)");
						}
					}
					
					else if(it.equals(ItemType.TOOL))
					{
						ToolType ttype = ToolType.valueOf(fc.getString("game-object.item.tool.type"));
						Integer tcol = fc.getInt("game-object.item.tool.cooldown");
						Material ucol = Material.valueOf(fc.getString("game-object.item.tool.used-material"));
						
						Tool t = new Tool(filter.getObtainableBank());
						t.setCost(cost);
						t.setDescription(description);
						t.setId(id);
						t.setMaterial(im);
						t.setStarter(starter);
						t.setMaterialMeta(ib);
						t.setName(name);
						t.setToolType(ttype);
						t.setCooldown(tcol);
						t.setUsedMaterial(ucol);
						
						filter.getObtainableBank().add(t);
					}
					
					else if(it.equals(ItemType.SHIELD))
					{
						ShieldType ttype = ShieldType.valueOf(fc.getString("game-object.item.shield.type"));
						Double tcol = fc.getDouble("game-object.item.shield.cooldown");
						Double reg = fc.getDouble("game-object.item.shield.ticks-per-regen");
						Double miti = fc.getDouble("game-object.item.shield.mitigation");
						Double maxx = fc.getDouble("game-object.item.shield.max-shields");
						
						Shield t = new Shield(filter.getObtainableBank());
						t.setCost(cost);
						t.setDescription(description);
						t.setId(id);
						t.setMaterial(im);
						t.setStarter(starter);
						t.setMaterialMeta(ib);
						t.setName(name);
						t.setCooldown(tcol);
						t.setMitigation(miti);
						t.setTicksPerShield(reg);
						t.setMaxShields(maxx);
						t.setShieldType(ttype);
						
						filter.getObtainableBank().add(t);
					}
					
					else if(it.equals(ItemType.INJECTABLE))
					{
						InjectableType ttype = InjectableType.valueOf(fc.getString("game-object.item.injectable.type"));
						Double power = fc.getDouble("game-object.item.injectable.power");
						
						Injectable t = new Injectable(filter.getObtainableBank());
						t.setCost(cost);
						t.setDescription(description);
						t.setId(id);
						t.setMaterial(im);
						t.setStarter(starter);
						t.setMaterialMeta(ib);
						t.setName(name);
						t.setPower(power);
						t.setInjectableType(ttype);
						
						filter.getObtainableBank().add(t);
					}
					
					else if(it.equals(ItemType.UTILITY))
					{
						Utility u = new Utility(filter.getObtainableBank());
						u.setCost(cost);
						u.setDescription(description);
						u.setId(id);
						u.setMaterial(im);
						u.setStarter(starter);
						u.setMaterialMeta(ib);
						u.setType(RuneType.valueOf(fc.getString("game-object.item.rune.type")));
						u.setName(name);
						
						filter.getObtainableBank().add(u);
					}
					
					else
					{
						d.failure("   Failed to parse object: item-type (unknown)");
					}
				}
				
				else
				{
					d.failure("   Failed to parse object: item-type (null)");
				}
			}
			
			else if(ot.equals(ObtainableType.ABILITY))
			{
				AbilityEffect at = AbilityEffect.valueOf(fc.getString("game-object.ability.type"));
				Audio af = Audio.valueOf(fc.getString("game-object.ability.audio"));
				Integer duration = fc.getInt("game-object.ability.duration");
				Integer cooldown = fc.getInt("game-object.ability.cooldown");
				
				if(at != null && af != null)
				{					
					Ability a = new Ability(filter.getObtainableBank());
					
					a.setName(name);
					a.setDescription(description);
					a.setId(id);
					a.setCost(cost);
					a.setStarter(starter);
					a.setAbilityEffect(at);
					a.setAbilityFiredSound(af);
					a.setCooldown(cooldown);
					a.setDuration(duration);
					
					filter.getObtainableBank().add(a);
				}
				
				else
				{
					d.failure("   Failed to parse object: ability-type (null)");
				}
			}
			
			else if(ot.equals(ObtainableType.PROJECTILE))
			{
				ProjectileType pt = ProjectileType.valueOf(fc.getString("game-object.projectile.type"));
				Double damage = fc.getDouble("game-object.projectile.damage");
				Double velocity = fc.getDouble("game-object.projectile.velocity");
								
				if(pt != null)
				{
					if(pt.equals(ProjectileType.ARROW))
					{
						d.overbose("   Gameobject projectile: type: " + pt.toString());
						
						ProjectileArrow pa = new ProjectileArrow(filter.getObtainableBank());
						
						pa.setCost(cost);
						pa.setDamage(damage);
						pa.setDescription(description);
						pa.setId(id);
						pa.setStarter(starter);
						pa.setVelocity(velocity);
						pa.setName(name);
						
						filter.getObtainableBank().add(pa);
					}
					
					else
					{
						d.failure("   Failed to parse object: projectile-type (unknown)");
					}
				}
				
				else
				{
					d.failure("   Failed to parse object: projectile-type (null)");
				}
			}
			
			else if(ot.equals(ObtainableType.UPGRADE))
			{
				UpgradeType ut = UpgradeType.valueOf(fc.getString("game-object.upgrade.type"));
				
				if(ut.equals(UpgradeType.PROJECTILE))
				{
					ProjectileUpgrade u = new ProjectileUpgrade(filter.getObtainableBank());
					
					ProjectileType pj = ProjectileType.valueOf(fc.getString("game-object.upgrade.projectile.type"));
					Double damageMod = fc.getDouble("game-object.upgrade.projectile.damage-modifier");
					Double ammunitionMod = fc.getDouble("game-object.upgrade.projectile.ammunition-modifier");
					Double velocityMod = fc.getDouble("game-object.upgrade.projectile.velocity-modifier");
					
					u.setCost(cost);
					u.setDescription(description);
					u.setId(id);
					u.setStarter(starter);
					u.setName(name);
					u.setDamageModifier(damageMod);
					u.setAmmunitionModifier(ammunitionMod);
					u.setVelocityModifier(velocityMod);
					u.setProjectileType(pj);
					
					filter.getObtainableBank().add(u);
				}
				
				else if(ut.equals(UpgradeType.RANGED_WEAPON))
				{
					RangedWeaponUpgrade u = new RangedWeaponUpgrade(filter.getObtainableBank());
					ProjectileType pj = ProjectileType.valueOf(fc.getString("game-object.upgrade.ranged-weapon.type"));
					
					Double rofMod = fc.getDouble("game-object.upgrade.ranged-weapon.rate-of-fire-modifier");
					
					u.setCost(cost);
					u.setDescription(description);
					u.setId(id);
					u.setStarter(starter);
					u.setName(name);
					u.setProjectileType(pj);
					u.setRateOfFireModifier(rofMod);
					
					filter.getObtainableBank().add(u);
				}
				
				else if(ut.equals(UpgradeType.MELEE_WEAPON))
				{
					MeleeWeaponUpgrade u = new MeleeWeaponUpgrade(filter.getObtainableBank());
					
					Double damageMod = fc.getDouble("game-object.upgrade.melee-weapon.damage-modifier");
					
					u.setCost(cost);
					u.setDescription(description);
					u.setId(id);
					u.setStarter(starter);
					u.setName(name);
					u.setDamageModifier(damageMod);
					
					filter.getObtainableBank().add(u);
				}
				
				else
				{
					
				}
			}
			
			//TODO from yaml
			
			else
			{
				d.failure("  Failed to parse object: obtainable-type (unknown)");
			}
		}
		
		else
		{
			d.failure("  Failed to parse object: obtainable-type (null)");
		}
	}
	
	public void save()
	{
		for(Obtainable o : filter.o())
		{
			d.overbose("Saving gameobject: " + o.getName() + " - " + o.getId());
			
			FileConfiguration fc = yaml(o);
			
			if(fc != null)
			{
				try
				{
					fc.save(objectFile(o));
					d.success("Saved Gameobject: " + o.getName() + " - " + o.getId());
				}
				
				catch(IOException e)
				{
					d.failure("FAILED TO SAVE GAMEOBJECT <IOEX>: " + o.getName() + " - " + o.getId());
				}
			}
			
			else
			{
				d.failure("FAILED TO SAVE GAMEOBJECT <NULL>: " + o.getName() + " - " + o.getId());
			}
		}
	}
	
	public File objectFile(Obtainable o)
	{
		return new File(base, "gameobject-" + o.getId() + ".yml");
	}
	
	/**
	 * @param o
	 * @return
	 */
	public FileConfiguration yaml(Obtainable o)
	{
		FileConfiguration fc = new YamlConfiguration();
		
		fc.set("game-object.name", o.getName());
		fc.set("game-object.description", o.getDescription());
		fc.set("game-object.hash", o.getId());
		fc.set("game-object.starter", o.getStarter());
		fc.set("game-object.cost", o.getCost());
		fc.set("game-object.type", o.getObtainableType().toString());
		
		if(filter.isItem(o))
		{
			Item i = (Item) o;
			
			fc.set("game-object.item.type", i.getItemType().toString());
			fc.set("game-object.item.material", i.getMaterial().toString());
			fc.set("game-object.item.metadata", i.getMaterialMeta().intValue());
			
			if(filter.isWeapon(o))
			{
				Weapon w = (Weapon) i;
				
				fc.set("game-object.item.weapon.type", w.getWeaponType().toString());
				fc.set("game-object.item.weapon.enclosure", w.getWeaponEnclosureType().toString());
				fc.set("game-object.item.weapon.effect", w.getWeaponEffect().toString());
				
				if(filter.isRangedWeapon(o))
				{
					RangedWeapon rw = (RangedWeapon) w;
					
					fc.set("game-object.item.weapon.ranged.projectile.type", rw.getProjectileType().toString());
					fc.set("game-object.item.weapon.ranged.rate-of-fire", rw.getRateOfFire());
					fc.set("game-object.item.weapon.ranged.automatic", rw.getAutomatic());
					fc.set("game-object.item.weapon.ranged.damage-multiplier", rw.getDamageMultiplier());
					fc.set("game-object.item.weapon.ranged.ammunition", rw.getAmmunition());
				}
				
				else if(filter.isMeleeWeapon(o))
				{
					MeleeWeapon mw = (MeleeWeapon) w;
					
					fc.set("game-object.item.weapon.melee.damage", mw.getDamage());
				}
				
				else
				{
					d.failure("  Invalid WeaponType: " + o.getName());
					return null;
				}
			}
			
			else if(filter.isShield(o))
			{
				Shield a = (Shield) o;
				
				fc.set("game-object.item.shield.type", a.getShieldType().toString());
				fc.set("game-object.item.shield.cooldown", a.getCooldown());
				fc.set("game-object.item.shield.ticks-per-regen", a.getTicksPerShield());
				fc.set("game-object.item.shield.max-shields", a.getMaxShields());
				fc.set("game-object.item.shield.mitigation", a.getMitigation());
			}
			
			else if(filter.isInjectable(o))
			{
				Injectable a = (Injectable) o;
				
				fc.set("game-object.item.injectable.type", a.getInjectableType().toString());
				fc.set("game-object.item.injectable.power", a.getPower());
			}
			
			else if(filter.isTool(o))
			{
				Tool tt = (Tool) o;
				
				fc.set("game-object.item.tool.type", tt.getToolType().toString());
				fc.set("game-object.item.tool.cooldown", tt.getCooldown());
				fc.set("game-object.item.tool.used-material", tt.getUsedMaterial().toString());
			}
			
			else if(filter.isUtility(o))
			{
				Utility uu = (Utility) o;
				fc.set("game-object.item.rune.type", uu.getType().toString());
			}
			
			else
			{
				d.failure("  Invalid ItemType: " + o.getName());
				return null;
			}
		}
		
		else if(filter.isAbility(o))
		{
			Ability a = (Ability) o;
			
			fc.set("game-object.ability.type", a.getAbilityEffect().toString());
			fc.set("game-object.ability.audio", a.getAbilityFiredSound().toString());
			fc.set("game-object.ability.duration", a.getDuration());
			fc.set("game-object.ability.cooldown", a.getCooldown());
		}
		
		else if(filter.isProjectile(o))
		{
			Projectile p = (Projectile) o;
			
			fc.set("game-object.projectile.type", p.getProjectileType().toString());
			fc.set("game-object.projectile.damage", p.getDamage());
			fc.set("game-object.projectile.velocity", p.getVelocity());
			
			if(filter.isArrowProjectile(o))
			{
			
			}
			
			else
			{
				d.failure("  Invalid ProjectileType: " + o.getName());
				return null;
			}
		}
		
		else if(filter.isUpgrade(o))
		{
			Upgrade u = (Upgrade) o;
			
			fc.set("game-object.upgrade.type", u.getUpgradeType().toString());
			
			if(u.getUpgradeType().equals(UpgradeType.PROJECTILE))
			{
				ProjectileUpgrade pu = (ProjectileUpgrade) u;
				
				fc.set("game-object.upgrade.projectile.damage-modifier", pu.getDamageModifier());
				fc.set("game-object.upgrade.projectile.ammunition-modifier", pu.getAmmunitionModifier());
				fc.set("game-object.upgrade.projectile.velocity-modifier", pu.getVelocityModifier());
				fc.set("game-object.upgrade.projectile.type", pu.getProjectileType().toString());
			}
			
			else if(u.getUpgradeType().equals(UpgradeType.MELEE_WEAPON))
			{
				MeleeWeaponUpgrade pu = (MeleeWeaponUpgrade) u;
				
				fc.set("game-object.upgrade.melee-weapon.damage-modifier", pu.getDamageModifier());
			}
			
			else if(u.getUpgradeType().equals(UpgradeType.RANGED_WEAPON))
			{
				RangedWeaponUpgrade pu = (RangedWeaponUpgrade) u;
				
				fc.set("game-object.upgrade.ranged-weapon.rate-of-fire-modifier", pu.getRateOfFireModifier());
				fc.set("game-object.upgrade.ranged-weapon.type", pu.getProjectileType().toString());
			}
			
			else
			{
				d.failure("  Invalid UpgradeType: " + o.getName());
				
			}
		}
		
		//TODO from yaml
		
		else
		{
			d.failure("  Invalid ObtainableType: " + o.getName());
			return null;
		}
		
		return fc;
	}
}
