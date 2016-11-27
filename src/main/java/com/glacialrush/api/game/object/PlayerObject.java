package com.glacialrush.api.game.object;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.phantomapi.nms.NMSX;
import org.phantomapi.physics.VectorMath;
import org.phantomapi.sync.Task;
import org.phantomapi.sync.TaskLater;
import org.phantomapi.util.C;
import org.phantomapi.util.P;
import org.phantomapi.world.Area;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.dispatch.notification.NotificationPriority;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameController;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.game.event.ActivateAbilityEvent;
import com.glacialrush.api.game.event.CombatEvent;
import com.glacialrush.api.game.event.DeathEvent;
import com.glacialrush.api.game.event.FireAbilityEvent;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.loadout.Loadout;
import com.glacialrush.api.game.obtainable.Ability;
import com.glacialrush.api.game.obtainable.Item;
import com.glacialrush.api.game.obtainable.Obtainable;
import com.glacialrush.api.game.obtainable.Projectile;
import com.glacialrush.api.game.obtainable.item.AbilityEffect;
import com.glacialrush.api.game.obtainable.item.Injectable;
import com.glacialrush.api.game.obtainable.item.InjectableType;
import com.glacialrush.api.game.obtainable.item.ItemType;
import com.glacialrush.api.game.obtainable.item.MeleeWeapon;
import com.glacialrush.api.game.obtainable.item.MeleeWeaponUpgrade;
import com.glacialrush.api.game.obtainable.item.ObtainableType;
import com.glacialrush.api.game.obtainable.item.ProjectileType;
import com.glacialrush.api.game.obtainable.item.ProjectileUpgrade;
import com.glacialrush.api.game.obtainable.item.RangedWeapon;
import com.glacialrush.api.game.obtainable.item.Shield;
import com.glacialrush.api.game.obtainable.item.Tool;
import com.glacialrush.api.game.obtainable.item.Utility;
import com.glacialrush.api.game.obtainable.item.Weapon;
import com.glacialrush.api.game.obtainable.item.WeaponEffect;
import com.glacialrush.api.game.obtainable.item.WeaponType;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.packet.NMS;
import com.glacialrush.xapi.FastMath;

public class PlayerObject implements Listener
{
	private Player player;
	private Weapon primary;
	private Shield shield;
	private Injectable sMOD;
	private Weapon secondary;
	private Weapon tertiary;
	private Ability ability;
	private Utility utility;
	private Double energy;
	private Tool tool;
	private Long lastFire;
	private Projectile projectile;
	private GMap<Arrow, Vector> tickingArrows;
	private GList<Arrow> arrows;
	private GameController gc;
	private Boolean built;
	private Boolean disabled;
	private Grapple grapple;
	private Boolean usingPack;
	private Double shields;
	private Integer waitreg;
	private Double lsEn;
	private Boolean abilityActive;
	private Boolean damaged;
	private Integer ticksAlive;
	private Boolean empt;
	private Boolean sready;
	private Integer particleLimit;
	private Integer particleUsed;
	
	public PlayerObject(Player player, GameController gc)
	{
		PlayerData pd = gc.gpd(player);
		Loadout loadout = pd.getLoadoutSet().getLoadout();
		
		this.gc = gc;
		this.player = player;
		this.gc.register(this);
		this.waitreg = 0;
		this.tickingArrows = new GMap<Arrow, Vector>();
		this.built = false;
		this.arrows = new GList<Arrow>();
		this.disabled = false;
		this.usingPack = false;
		this.empt = false;
		this.damaged = false;
		this.shields = 10.0;
		this.lastFire = System.currentTimeMillis();
		this.energy = 100.0;
		this.lsEn = 100.0;
		this.abilityActive = false;
		this.ticksAlive = 0;
		this.sready = true;
		this.particleLimit = 1024;
		this.particleUsed = 0;
		
		if(!loadout.getPrimaryWeapon().equals("null"))
		{
			primary = (Weapon) gc.getObtainableBank().resolve(loadout.getPrimaryWeapon());
		}
		
		if(!loadout.getSecondaryWeapon().equals("null"))
		{
			secondary = (Weapon) gc.getObtainableBank().resolve(loadout.getSecondaryWeapon());
		}
		
		if(!loadout.getTertiaryWeapon().equals("null"))
		{
			tertiary = (Weapon) gc.getObtainableBank().resolve(loadout.getTertiaryWeapon());
		}
		
		if(!loadout.getProjectile().equals("null"))
		{
			projectile = (Projectile) gc.getObtainableBank().resolve(loadout.getProjectile());
		}
		
		if(!loadout.getAbility().equals("null"))
		{
			ability = (Ability) gc.getObtainableBank().resolve(loadout.getAbility());
		}
		
		if(!loadout.getTool().equals("null"))
		{
			tool = (Tool) gc.getObtainableBank().resolve(loadout.getTool());
		}
		
		try
		{
			if(!loadout.getsMOD().equals("null"))
			{
				if((Injectable) gc.getObtainableBank().resolve(loadout.getsMOD()) == null)
				{
					
				}
				
				else
				{
					sMOD = (Injectable) gc.getObtainableBank().resolve(loadout.getsMOD());
				}
				
				sMOD = (Injectable) gc.getObtainableBank().resolve(loadout.getsMOD());
			}
		}
		
		catch(Exception e)
		{
			gc.getDispatcher().failure("COULD NOT LOAD DATA. LOOKING FOR FITTING ADAPTER...");
			
			for(Injectable i : gc.getObtainableBank().getObtainableFilter().getInjectables())
			{
				gc.getDispatcher().failure("ADAPT: + " + i.getId());
				
				loadout.setsMOD(i.getId());
				gc.gpd(player).getOwned().add(i.getId());
			}
			
			gc.getDispatcher().failure("ADAPT: +INJECTABLE");
			
			sMOD = (Injectable) gc.getObtainableBank().resolve(gc.getObtainableBank().getObtainableFilter().getInjectables().pickRandom().getId());
		}
		
		if(!loadout.getShield().equals("null"))
		{
			if((Shield) gc.getObtainableBank().resolve(loadout.getShield()) == null)
			{
				
			}
			
			else
			{
				shield = (Shield) gc.getObtainableBank().resolve(loadout.getShield());
				shields = shield.getMaxShields();
			}
		}
	}
	
	@EventHandler
	public void on(EntityDamageEvent e)
	{
		if(e.getEntity().equals(player))
		{
			if(e.getCause().equals(DamageCause.STARVATION))
			{
				e.setCancelled(true);
			}
		}
	}
	
	public boolean particleHit()
	{
		if(particleUsed < particleLimit)
		{
			particleUsed++;
			return true;
		}
		
		return false;
	}
	
	public void regenTick()
	{
		player.setSaturation(100f);
		
		if(NMSX.ping(player) > 120 && particleLimit > 32)
		{
			particleLimit /= 2;
		}
		
		else if(particleLimit < 1024)
		{
			particleLimit *= 2;
		}
		
		particleUsed = 0;
		
		if(player.getMaxHealth() > 20)
		{
			player.setMaxHealth(player.getHealth() < 20 ? 20 : player.getHealth());
		}
		
		damaged = false;
		
		if(empt && energy >= ((RegionedGame) gc.getGame(player)).getAbilityHandler().abc(player))
		{
			signalEnergy(100.0);
			empt = false;
		}
		
		else if((ticksAlive % 10 == 0) && energy < ((RegionedGame) gc.getGame(player)).getAbilityHandler().abc(player))
		{
			signalEnergy(((RegionedGame) gc.getGame(player)).getAbilityHandler().abc(player));
		}
		
		empt = energy < ((RegionedGame) gc.getGame(player)).getAbilityHandler().abc(player);
		
		if(player.getFoodLevel() < 6)
		{
			player.setWalkSpeed(0.235f);
		}
		
		else
		{
			player.setWalkSpeed(0.2f);
		}
		
		if(player.getGameMode().equals(GameMode.ADVENTURE))
		{
			ticksAlive++;
		}
		
		else
		{
			ticksAlive = 0;
		}
		
		if(energy != lsEn)
		{
			Double dist = 0.0;
			
			if(lsEn > energy)
			{
				dist = (lsEn - energy) / 2.6;
				lsEn -= dist;
			}
			
			if(energy > lsEn)
			{
				dist = (energy - lsEn) / 2.8;
				lsEn += dist;
			}
			
			player.setExp(lsEn.floatValue() / 100.0f);
		}
		
		if(!player.isSprinting() && player.getFoodLevel() < 4)
		{
			player.setSprinting(true);
		}
		
		if(waitreg > 0)
		{
			waitreg--;
		}
		
		else if(shields < shield.getMaxShields())
		{
			double inc = (1.0 / shield.getTicksPerShield());
			
			if(getEnergy() >= inc * 2)
			{
				if(shields == 0)
				{
					if(player.getGameMode().equals(GameMode.ADVENTURE))
					{
						((RegionedGame) getGc().getGame(player)).getNotificationHandler().queue(player, new Notification(null, null, ChatColor.AQUA + "Shields are Recharging"));
						Audio.SHIELD_UP.playGlobal(player.getLocation());
					}
				}
				
				if(player.getGameMode().equals(GameMode.ADVENTURE))
				{
					ParticleEffect.FIREWORKS_SPARK.display(0.05f, 0.05f, 0.05f, 0.05f, 2, player.getLocation().add(0, 1, 0), 32);
				}
				
				setEnergy(getEnergy() - (inc * 2));
				shields += inc;
			}
		}
	}
	
	public Long getLastFire()
	{
		return lastFire;
	}
	
	public void setLastFire(Long lastFire)
	{
		this.lastFire = lastFire;
	}
	
	public Double getLsEn()
	{
		return lsEn;
	}
	
	public void setLsEn(Double lsEn)
	{
		this.lsEn = lsEn;
	}
	
	public Boolean getDamaged()
	{
		return damaged;
	}
	
	public void setDamaged(Boolean damaged)
	{
		this.damaged = damaged;
	}
	
	public Integer getTicksAlive()
	{
		return ticksAlive;
	}
	
	public void setTicksAlive(Integer ticksAlive)
	{
		this.ticksAlive = ticksAlive;
	}
	
	public Boolean getUsingPack()
	{
		return usingPack;
	}
	
	public void setUsingPack(Boolean usingPack)
	{
		this.usingPack = usingPack;
	}
	
	public void dehook()
	{
		if(grapple != null)
		{
			grapple.unhook();
		}
		
		grapple = null;
	}
	
	public Grapple getGrapple()
	{
		return grapple;
	}
	
	public void setGrapple(Grapple grapple)
	{
		this.grapple = grapple;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public void setPrimary(Weapon primary)
	{
		this.primary = primary;
	}
	
	public void setSecondary(Weapon secondary)
	{
		this.secondary = secondary;
	}
	
	public void setTertiary(Weapon tertiary)
	{
		this.tertiary = tertiary;
	}
	
	public void setAbility(Ability ability)
	{
		this.ability = ability;
	}
	
	public Boolean getAbilityActive()
	{
		return abilityActive;
	}
	
	public void setAbilityActive(Boolean abilityActive)
	{
		this.abilityActive = abilityActive;
	}
	
	public void setUtility(Utility utility)
	{
		this.utility = utility;
	}
	
	public void setTool(Tool tool)
	{
		this.tool = tool;
	}
	
	public void setProjectile(Projectile projectile)
	{
		this.projectile = projectile;
	}
	
	public void setTickingArrows(GMap<Arrow, Vector> tickingArrows)
	{
		this.tickingArrows = tickingArrows;
	}
	
	public Double getEnergy()
	{
		return energy;
	}
	
	public void signalEnergy(Double energy)
	{
		lsEn = energy;
	}
	
	public void setEnergy(Double energy)
	{
		if(energy > 100)
		{
			energy = 100.0;
		}
		
		if(energy < 0)
		{
			energy = 0.0;
		}
		
		this.energy = energy;
	}
	
	public Integer getWaitreg()
	{
		return waitreg;
	}
	
	public void setWaitreg(Integer waitreg)
	{
		this.waitreg = waitreg;
	}
	
	public void setShield(Shield shield)
	{
		this.shield = shield;
	}
	
	public void setShields(Double shields)
	{
		this.shields = shields;
	}
	
	public void setArrows(GList<Arrow> arrows)
	{
		this.arrows = arrows;
	}
	
	public void setGc(GameController gc)
	{
		this.gc = gc;
	}
	
	public void setBuilt(Boolean built)
	{
		this.built = built;
	}
	
	public void setDisabled(Boolean disabled)
	{
		this.disabled = disabled;
	}
	
	public void updateArmor()
	{
		if(!built)
		{
			return;
		}
		
		RegionedGame rg = (RegionedGame) gc.getGame(player);
		Faction f = rg.getFactionHandler().getFaction(player);
		DyeColor dc = f.getDyeColor();
		String n = f.getColor() + "" + f.getName() + " Armor";
		Squad s = rg.getSquadHandler().getSquad(getPlayer());
		
		if(rg.getSquadHandler().inSquad(getPlayer()))
		{
			dc = s.getDye();
			n = s.getColor() + s.getGreek().symbol() + " " + s.getGreek().fName() + " Armor";
			
			ItemStack lhelmet = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
			lam.setDisplayName(n);
			lam.setColor(dc.getColor());
			lhelmet.setItemMeta(lam);
			player.getInventory().setChestplate(lhelmet);
		}
		
		else
		{
			ItemStack lhelmet = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
			lam.setDisplayName(n);
			lam.setColor(dc.getColor());
			lhelmet.setItemMeta(lam);
			player.getInventory().setChestplate(lhelmet);
		}
	}
	
	public void disable()
	{
		disabled = true;
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 5, true, false), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 5, true, false), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, true, false), true);
		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(gc.pl().getServerDataComponent().getHub().clone().add((Math.random() - 0.5) * 30, 30, (Math.random() - 0.5) * 30));
		clear();
	}
	
	public void enable()
	{
		disabled = false;
		player.setGameMode(GameMode.ADVENTURE);
		clearPotionEffects();
		clear();
		gc.pl().getUiController().moveShortcuts(player, false);
		
		Location k = gc.pl().getServerDataComponent().getHub();
		
		if(k != null)
		{
			player.teleport(gc.pl().getServerDataComponent().getHub());
		}
	}
	
	public void mdisable()
	{
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 3, true, false), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 3, true, false), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, true, false), true);
	}
	
	public void menable()
	{
		player.setGameMode(GameMode.ADVENTURE);
		clearPotionEffects();
	}
	
	public GList<Arrow> getArrows()
	{
		return arrows;
	}
	
	public Boolean getDisabled()
	{
		return disabled;
	}
	
	public void processWeaponAbilities()
	{
		processWeaponAbility(getWeapon());
	}
	
	public WeaponEffect getEffect()
	{
		Weapon w = getWeapon();
		
		if(w != null)
		{
			return w.getWeaponEffect();
		}
		
		return WeaponEffect.NONE;
	}
	
	public Weapon getWeapon()
	{
		int slot = player.getInventory().getHeldItemSlot();
		
		if(slot == 0)
		{
			return getPrimary();
		}
		
		else if(slot == 1)
		{
			return getSecondary();
		}
		
		else if(slot == 2)
		{
			return getTertiary();
		}
		
		return null;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onHitCloaker(PlayerInteractEvent e)
	{
		if(e.getPlayer().equals(player))
		{
			RegionedGame rg = (RegionedGame) (gc.getGame(player) == null ? null : gc.getGame(player));
			
			if(rg == null)
			{
				return;
			}
			
			if(e.getAction().equals(Action.LEFT_CLICK_AIR))
			{
				if(rg.getAbilityHandler().isActive(player))
				{
					if(rg.getAbilityHandler().getAbilityEffect(player).equals(AbilityEffect.CLOAK))
					{
						return;
					}
				}
				
				Vector v = e.getPlayer().getLocation().getDirection().normalize();
				Location s = e.getPlayer().getEyeLocation();
				
				for(int i = 0; i < 5; i++)
				{
					for(Player j : rg.players())
					{
						if(!player.equals(j) && !rg.getFactionHandler().getFaction(j).equals(rg.getFactionHandler().getFaction(player)))
						{
							if(j.getLocation().getBlockX() == s.getBlockX() && j.getLocation().getBlockZ() == s.getBlockZ() && Math.abs(j.getLocation().getBlockY() - s.getBlockY()) < 3)
							{
								if(rg.getAbilityHandler().isActive(j))
								{
									if(rg.getAbilityHandler().getAbilityEffect(j).equals(AbilityEffect.CLOAK))
									{
										rg.getAbilityHandler().unc(j);
										return;
									}
								}
							}
						}
					}
					
					s = s.clone().add(v);
				}
				
				v = e.getPlayer().getLocation().getDirection().normalize();
				s = e.getPlayer().getEyeLocation().clone().subtract(0, 1, 0);
				
				for(int i = 0; i < 5; i++)
				{
					for(Player j : rg.players())
					{
						if(!player.equals(j) && !rg.getFactionHandler().getFaction(j).equals(rg.getFactionHandler().getFaction(player)))
						{
							if(j.getLocation().getBlockX() == s.getBlockX() && j.getLocation().getBlockZ() == s.getBlockZ() && Math.abs(j.getLocation().getBlockY() - s.getBlockY()) < 3)
							{
								if(rg.getAbilityHandler().isActive(j))
								{
									if(rg.getAbilityHandler().getAbilityEffect(j).equals(AbilityEffect.CLOAK))
									{
										rg.getAbilityHandler().unc(j);
										return;
									}
								}
							}
						}
					}
					
					s = s.clone().add(v);
				}
			}
		}
	}
	
	public void processWeaponAbility(Weapon w)
	{
		if(w == null)
		{
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			player.removePotionEffect(PotionEffectType.FAST_DIGGING);
			return;
		}
		
		if(w.getWeaponEffect().equals(WeaponEffect.NONE))
		{
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			player.removePotionEffect(PotionEffectType.FAST_DIGGING);
		}
		
		else if(w.getWeaponEffect().equals(WeaponEffect.SLUGGISH))
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 5, 2, true, false));
		}
		
		else if(w.getWeaponEffect().equals(WeaponEffect.LIGHTWEIGHT))
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5, 1, true, false));
		}
		
		else if(w.getWeaponEffect().equals(WeaponEffect.SEMI_SLUGGISH))
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 5, 0, true, false));
		}
		
		else if(w.getWeaponEffect().equals(WeaponEffect.SEMI_LIGHTWEIGHT))
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5, 0, true, false));
		}
		
		else
		{
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			player.removePotionEffect(PotionEffectType.FAST_DIGGING);
		}
	}
	
	public void clearPotionEffects()
	{
		for(PotionEffect i : new GList<PotionEffect>(player.getActivePotionEffects()))
		{
			player.removePotionEffect(i.getType());
		}
	}
	
	public void activateWeaponAbility()
	{
		
	}
	
	public void spectator()
	{
		player.getInventory().clear();
		gc.pl().getUiController().moveShortcuts(player, true);
	}
	
	public Obtainable getCurrent()
	{
		if(player.getInventory().getHeldItemSlot() == 0)
		{
			return primary;
		}
		
		if(player.getInventory().getHeldItemSlot() == 1)
		{
			return secondary;
		}
		
		if(player.getInventory().getHeldItemSlot() == 2)
		{
			return tertiary;
		}
		
		return null;
	}
	
	public GList<Obtainable> obtainables()
	{
		GList<Obtainable> obs = new GList<Obtainable>();
		
		for(String i : gc.gpd(player).getOwned())
		{
			obs.add(gc.getObtainableBank().getObtainableFilter().resolve(i));
		}
		
		return obs;
	}
	
	public GList<Weapon> weapons()
	{
		GList<Weapon> wp = new GList<Weapon>();
		
		for(Obtainable i : obtainables())
		{
			if(i.getObtainableType().equals(ObtainableType.ITEM))
			{
				if(((Item) i).getItemType().equals(ItemType.WEAPON))
				{
					wp.add((Weapon) i);
				}
			}
		}
		
		return wp;
	}
	
	public GList<Projectile> projectiles()
	{
		GList<Projectile> wp = new GList<Projectile>();
		
		for(Obtainable i : obtainables())
		{
			if(i.getObtainableType().equals(ObtainableType.PROJECTILE))
			{
				wp.add((Projectile) i);
			}
		}
		
		return wp;
	}
	
	public GList<Ability> abilities()
	{
		GList<Ability> wp = new GList<Ability>();
		
		for(Obtainable i : obtainables())
		{
			if(i.getObtainableType().equals(ObtainableType.ABILITY))
			{
				wp.add((Ability) i);
			}
		}
		
		return wp;
	}
	
	public Shield getShield()
	{
		return shield;
	}
	
	public GList<Shield> shields()
	{
		GList<Shield> wp = new GList<Shield>();
		
		for(Obtainable i : obtainables())
		{
			for(Shield j : getGc().getObtainableBank().getObtainableFilter().getShields())
			{
				if(i.getId().equals(j.getId()))
				{
					wp.add(j);
				}
			}
		}
		
		return wp;
	}
	
	public GList<Injectable> smods()
	{
		GList<Injectable> wp = new GList<Injectable>();
		
		for(Obtainable i : obtainables())
		{
			for(Injectable j : getGc().getObtainableBank().getObtainableFilter().getInjectables())
			{
				if(i.getId().equals(j.getId()))
				{
					wp.add(j);
				}
			}
		}
		
		return wp;
	}
	
	public Utility getRune()
	{
		return utility;
	}
	
	public GList<Utility> utilities()
	{
		GList<Utility> wp = new GList<Utility>();
		
		for(Obtainable i : obtainables())
		{
			if(i.getObtainableType().equals(ObtainableType.ITEM))
			{
				Item ii = (Item) i;
				
				if(ii.getItemType().equals(ItemType.UTILITY))
				{
					wp.add((Utility) i);
				}
			}
		}
		
		return wp;
	}
	
	public GList<Tool> tools()
	{
		GList<Tool> wp = new GList<Tool>();
		
		for(Obtainable i : obtainables())
		{
			if(i.getObtainableType().equals(ObtainableType.ITEM))
			{
				Item ii = (Item) i;
				
				if(ii.getItemType().equals(ItemType.TOOL))
				{
					wp.add((Tool) i);
				}
			}
		}
		
		return wp;
	}
	
	public void useInjectable()
	{
		if(sready)
		{
			sready = false;
			Audio.COMBAT_INJECT.play(player);
			updateInjectable();
			handleInjectable();
		}
		
		else
		{
			Audio.UI_FAIL.play(player);
		}
	}
	
	public void handleInjectable()
	{
		if(sMOD != null)
		{
			if(sMOD.getInjectableType().equals(InjectableType.ENERGY))
			{
				setEnergy(getEnergy() + 70.8);
				Audio.SHIELD_MITIGATED.playGlobal(player.getLocation());
				
				new TaskLater(5)
				{
					@Override
					public void run()
					{
						waitreg = 0;
					}
				};
			}
			
			else if(sMOD.getInjectableType().equals(InjectableType.HEALTH))
			{
				Audio.SHIELD_MITIGATED.playGlobal(player.getLocation());
				player.setMaxHealth(player.getMaxHealth() + sMOD.getPower());
				player.setHealth(player.getHealth() + sMOD.getPower() > player.getMaxHealth() ? player.getMaxHealth() : player.getHealth() + sMOD.getPower());
			}
		}
	}
	
	public void updateInjectable()
	{
		if(sMOD != null)
		{
			ItemStack tx = new ItemStack(sready ? sMOD.getMaterial() : Material.RECORD_3);
			ItemMeta tmi = tx.getItemMeta();
			tmi.setDisplayName(ChatColor.AQUA + sMOD.getName());
			tx.setItemMeta(tmi);
			player.getInventory().setItem(6, tx);
		}
	}
	
	public void updateTool(String message, boolean ready)
	{
		if(!built || !hasTool())
		{
			return;
		}
		
		ItemStack tx = new ItemStack(ready ? tool.getMaterial() : tool.getUsedMaterial());
		ItemMeta tmi = tx.getItemMeta();
		tmi.setDisplayName(ChatColor.GOLD + tool.getName() + " " + message);
		tx.setItemMeta(tmi);
		player.getInventory().setItem(7, tx);
	}
	
	public void updateCompass(Location heading, String objective)
	{
		if(!built)
		{
			return;
		}
		
		ItemStack mm = player.getInventory().getItem(8);
		
		if(mm == null || !mm.getType().equals(Material.COMPASS))
		{
			ItemStack cp = new ItemStack(Material.COMPASS, 1);
			ItemMeta im = cp.getItemMeta();
			im.setDisplayName(objective);
			cp.setItemMeta(im);
			
			player.getInventory().setItem(8, cp);
		}
		
		else
		{
			player.getInventory().remove(Material.COMPASS);
			ItemMeta im = mm.getItemMeta();
			im.setDisplayName(objective);
			mm.setItemMeta(im);
			player.getInventory().setItem(8, mm);
		}
		
		player.setCompassTarget(heading);
		
		if(player.getInventory().getHeldItemSlot() == 8)
		{
			int[] v = new int[] {0};
			
			new Task(0)
			{
				@Override
				public void run()
				{
					if(v[0] > 20)
					{
						cancel();
					}
					
					for(int i = 0; i < 20; i++)
					{
						ParticleEffect.FIREWORKS_SPARK.display(new Vector(0, 1, 0), 1.9f, new Area(heading.add(0, 0, 0), 4.5).random(), player);
					}
					
					if(Math.random() > 0.8)
					{
						Vector vx = VectorMath.direction(player.getLocation(), heading).normalize();
						
						ParticleEffect.FIREWORKS_SPARK.display(vx, 1.9f, new Area(P.getHand(player), 0.01).random(), player);
					}
					
					v[0]++;
				}
			};
		}
	}
	
	public void breakSheild()
	{
		damage(100);
		shields = 0.0;
		Audio.SHIELD_DOWN.playGlobal(player.getLocation());
	}
	
	@SuppressWarnings("deprecation")
	public void buildItems()
	{
		player.getInventory().clear();
		player.setMaxHealth(20);
		player.setFoodLevel(20);
		player.setHealth(20);
		sready = true;
		abilityActive = false;
		shields = 20.0;
		lsEn = 0.0;
		player.getInventory().setHeldItemSlot(0);
		gc.pl().getUiController().moveShortcuts(player, true);
		built = true;
		
		if(gc.getGame(player) == null)
		{
			player.getInventory().clear();
			gc.pl().getUiController().moveShortcuts(player, false);
			return;
		}
		
		if(gc.getGame(player).getType().equals(GameType.REGIONED))
		{
			PlayerData pd = gc.gpd(player);
			Loadout loadout = pd.getLoadoutSet().getLoadout();
			((RegionedGame) gc.getGame(player)).getPlayerHandler().reset(player);
			
			if(!loadout.getPrimaryWeapon().equals("null"))
			{
				primary = (Weapon) gc.getObtainableBank().resolve(loadout.getPrimaryWeapon());
			}
			
			if(!loadout.getSecondaryWeapon().equals("null"))
			{
				secondary = (Weapon) gc.getObtainableBank().resolve(loadout.getSecondaryWeapon());
			}
			
			if(!loadout.getShield().equals("null"))
			{
				shield = (Shield) gc.getObtainableBank().resolve(loadout.getShield());
				shields = shield.getMaxShields() == null ? 10 : shield.getMaxShields();
			}
			
			if(!loadout.getsMOD().equals("null"))
			{
				sMOD = (Injectable) gc.getObtainableBank().resolve(loadout.getsMOD());
			}
			
			if(sMOD == null)
			{
				for(Injectable i : gc.getObtainableBank().getObtainableFilter().getInjectables())
				{
					if(gc.getObtainableBank().getObtainableFilter().has(player, i))
					{
						loadout.setsMOD(i.getId());
						sMOD = i;
						break;
					}
				}
				
				new TaskLater(80)
				{
					@Override
					public void run()
					{
						Notification n = new Notification(C.AQUA + "Equip Items!", C.LIGHT_PURPLE + "Open your inventory and click loadout!", C.YELLOW + "Seriously, its a good idea to equip stuff.", Audio.REGION_CAPTURE);
						n.setDisplay(120);
						n.setDelay(10);
						n.setPriority(NotificationPriority.LOWEST);
						
						Notification n2 = new Notification(C.AQUA + "Enjoy the Fight!", C.LIGHT_PURPLE + "Follow your compass!", C.YELLOW + "See you in battle.", Audio.REGION_LOST);
						n2.setDisplay(120);
						n2.setDelay(10);
						n2.setPriority(NotificationPriority.LOWEST);
						((RegionedGame)gc.getGame(getPlayer())).getNotificationHandler().queue(getPlayer(), n);
						((RegionedGame)gc.getGame(getPlayer())).getNotificationHandler().queue(getPlayer(), n2);
						
						new TaskLater(100)
						{
							@Override
							public void run()
							{
								Notification n = new Notification(C.AQUA + "Capture Points", C.LIGHT_PURPLE + "Capturing points take down enemies", C.YELLOW + "Territories cosist of multiple points", Audio.EMP);
								n.setDisplay(120);
								n.setPriority(NotificationPriority.LOWEST);
								
								((RegionedGame)gc.getGame(getPlayer())).getNotificationHandler().queue(getPlayer(), n);
								
								new TaskLater(100)
								{
									@Override
									public void run()
									{
										Notification n = new Notification(C.AQUA + "Skill? XP? Shards?", C.LIGHT_PURPLE + "Simply check with /xp any time", C.YELLOW + "You can spend that stuff!", Audio.EXPERIENCE_EARN);
										n.setDisplay(120);
										n.setPriority(NotificationPriority.LOWEST);
										
										((RegionedGame)gc.getGame(getPlayer())).getNotificationHandler().queue(getPlayer(), n);
										
										new TaskLater(100)
										{
											@Override
											public void run()
											{
												Notification n = new Notification(C.AQUA + "Buy equipment in the shop!", C.LIGHT_PURPLE + "Open your inventory and click shop!", C.YELLOW + "Use SKILL POINTS as a currency", Audio.CAPTURE_CAPTURE);
												n.setDisplay(120);
												n.setPriority(NotificationPriority.LOWEST);
												
												((RegionedGame)gc.getGame(getPlayer())).getNotificationHandler().queue(getPlayer(), n);
											}
										};
									}
								};
							}
						};
					}
				};
			}
			
			if(!loadout.getTertiaryWeapon().equals("null"))
			{
				tertiary = (Weapon) gc.getObtainableBank().resolve(loadout.getTertiaryWeapon());
			}
			
			if(!loadout.getProjectile().equals("null"))
			{
				projectile = (Projectile) gc.getObtainableBank().resolve(loadout.getProjectile());
			}
			
			if(!loadout.getAbility().equals("null"))
			{
				ability = (Ability) gc.getObtainableBank().resolve(loadout.getAbility());
			}
			
			if(!loadout.getUtility().equals("null"))
			{
				utility = (Utility) gc.getObtainableBank().resolve(loadout.getUtility());
			}
			
			if(!loadout.getTool().equals("null"))
			{
				tool = (Tool) gc.getObtainableBank().resolve(loadout.getTool());
			}
			
			if(tool == null)
			{
				for(Tool i : gc.getObtainableBank().getObtainableFilter().getTools())
				{
					if(gc.getObtainableBank().getObtainableFilter().has(player, i))
					{
						loadout.setTool(i.getId());
						tool = i;
						break;
					}
				}
			}
			
			RegionedGame rg = (RegionedGame) gc.getGame(player);
			Faction f = rg.getFactionHandler().getFaction(player);
			DyeColor dc = f.getDyeColor();
			String n = f.getColor() + "" + f.getName() + " Armor";
			
			ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
			lam.setDisplayName(n);
			lam.setColor(dc.getColor());
			lhelmet.setItemMeta(lam);
			
			ItemStack lchestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			LeatherArmorMeta lch = (LeatherArmorMeta) lchestplate.getItemMeta();
			lch.setDisplayName(n);
			lch.setColor(dc.getColor());
			lchestplate.setItemMeta(lch);
			
			ItemStack lleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
			LeatherArmorMeta lll = (LeatherArmorMeta) lleggings.getItemMeta();
			lll.setDisplayName(n);
			lll.setColor(dc.getColor());
			lleggings.setItemMeta(lll);
			
			ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS, 1);
			LeatherArmorMeta lbo = (LeatherArmorMeta) lboots.getItemMeta();
			lbo.setDisplayName(n);
			lbo.setColor(dc.getColor());
			lboots.setItemMeta(lbo);
			
			updateCompass(player.getLocation(), ChatColor.GREEN + "Objective");
			player.getInventory().setHelmet(lhelmet);
			player.getInventory().setChestplate(lchestplate);
			player.getInventory().setLeggings(lleggings);
			player.getInventory().setBoots(lboots);
			player.setPlayerListName(f.getColor() + player.getName());
		}
		
		boolean ffx = false;
		
		if(primary != null)
		{
			if(primary.getWeaponType().equals(WeaponType.RANGED))
			{
				ffx = true;
			}
			
			ItemStack ipw = new ItemStack(primary.getMaterial(), 1, (short) 0, primary.getMaterialMeta());
			ItemMeta im = ipw.getItemMeta();
			im.setDisplayName(ChatColor.AQUA + primary.getName());
			ipw.setItemMeta(im);
			player.getInventory().setItem(0, ipw);
		}
		
		if(secondary != null)
		{
			if(secondary.getWeaponType().equals(WeaponType.RANGED))
			{
				ffx = true;
			}
			
			ItemStack ipw = new ItemStack(secondary.getMaterial(), 1, (short) 0, secondary.getMaterialMeta());
			ItemMeta im = ipw.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + secondary.getName());
			ipw.setItemMeta(im);
			player.getInventory().setItem(1, ipw);
		}
		
		if(tertiary != null)
		{
			if(tertiary.getWeaponType().equals(WeaponType.RANGED))
			{
				ffx = true;
			}
			
			ItemStack ipw = new ItemStack(tertiary.getMaterial(), 1, (short) 0, tertiary.getMaterialMeta());
			ItemMeta im = ipw.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + tertiary.getName());
			ipw.setItemMeta(im);
			player.getInventory().setItem(2, ipw);
		}
		
		if(tool != null)
		{
			ItemStack tx = new ItemStack(tool.getMaterial());
			ItemMeta tmi = tx.getItemMeta();
			tmi.setDisplayName(ChatColor.GOLD + tool.getName());
			tx.setItemMeta(tmi);
			player.getInventory().setItem(7, tx);
		}
		
		if(sMOD != null)
		{
			ItemStack tx = new ItemStack(sMOD.getMaterial());
			ItemMeta tmi = tx.getItemMeta();
			tmi.setDisplayName(ChatColor.AQUA + sMOD.getName());
			tx.setItemMeta(tmi);
			player.getInventory().setItem(6, tx);
		}
		
		if(projectile != null && ffx)
		{
			int am = 0;
			int b = 0;
			int k = 3;
			
			if(primary.getWeaponType().equals(WeaponType.RANGED))
			{
				am = ((RangedWeapon) primary).getAmmunition();
				GList<ProjectileUpgrade> up = gc.getObtainableBank().getObtainableFilter().getProjectileUpgrades(ProjectileType.ARROW);
				
				for(ProjectileUpgrade i : up)
				{
					if(gc.getObtainableBank().getObtainableFilter().has(player, i))
					{
						b += (am * i.getAmmunitionModifier());
					}
				}
			}
			
			am += b;
			
			if(am > 64)
			{
				ItemStack ipw = new ItemStack(Material.ARROW, 64, (short) 0, (byte) 0);
				ItemMeta im = ipw.getItemMeta();
				im.setDisplayName(ChatColor.BLUE + projectile.getName());
				ipw.setItemMeta(im);
				player.getInventory().setItem(k, ipw);
				k++;
				am -= 64;
			}
			
			else
			{
				ItemStack ipw = new ItemStack(Material.ARROW, am, (short) 0, (byte) 0);
				ItemMeta im = ipw.getItemMeta();
				im.setDisplayName(ChatColor.BLUE + projectile.getName());
				ipw.setItemMeta(im);
				player.getInventory().setItem(3, ipw);
				am = 0;
			}
			
			if(am > 0)
			{
				ItemStack ipw = new ItemStack(Material.ARROW, am, (short) 0, (byte) 0);
				ItemMeta im = ipw.getItemMeta();
				im.setDisplayName(ChatColor.BLUE + projectile.getName());
				ipw.setItemMeta(im);
				player.getInventory().setItem(k, ipw);
				am = 0;
			}
		}
		
		resetActions(player);
	}
	
	public void clear()
	{
		built = false;
		ItemStack a = new ItemStack(Material.AIR);
		player.getInventory().clear();
		player.getInventory().setHelmet(a);
		player.getInventory().setChestplate(a);
		player.getInventory().setLeggings(a);
		player.getInventory().setBoots(a);
		player.setPlayerListName(ChatColor.AQUA + player.getName());
		dehook();
	}
	
	public void resetActions(Player p)
	{
		ItemStack k = new ItemStack(Material.DIAMOND);
		ItemMeta km = k.getItemMeta();
		km.setDisplayName(ChatColor.BLACK + "   ");
		k.setItemMeta(km);
		
		for(int i = 0; i < 35; i++)
		{
			ItemStack s = p.getInventory().getItem(i);
			
			if(s == null || s.getType().equals(Material.AIR))
			{
				p.getInventory().setItem(i, k);
			}
		}
	}
	
	public double arrowDamage()
	{
		double dam = 0.0;
		double dbm = 0.0;
		
		if(projectile != null)
		{
			dam = projectile.getDamage();
			
			for(ProjectileUpgrade i : gc.getObtainableBank().getObtainableFilter().getProjectileUpgrades(ProjectileType.ARROW))
			{
				dbm += (dam * i.getDamageModifier());
			}
		}
		
		else
		{
			return 5.2;
		}
		
		return dam + dbm;
	}
	
	@EventHandler
	public void bowFix(EntityShootBowEvent e)
	{
		double mbn = 0.0;
		
		if(projectile != null)
		{
			double mpb = 3.5;
			
			for(ProjectileUpgrade i : gc.getObtainableBank().getObtainableFilter().getProjectileUpgrades(ProjectileType.ARROW))
			{
				if(!gc.getObtainableBank().getObtainableFilter().has((Player) e.getEntity(), i))
				{
					continue;
				}
				
				mpb += i.getVelocityModifier();
			}
			
			if(e.getEntityType().equals(EntityType.PLAYER))
			{
				if(!((Player) e.getEntity()).equals(player))
				{
					return;
				}
				
				else
				{
					if(gc.getGame(player).getType().equals(GameType.REGIONED))
					{
						RegionedGame rg = (RegionedGame) gc.getGame(player);
						rg.getToolHandler().cooldown(player);
						
						if(((Arrow) e.getProjectile()).isCritical())
						{
							if(rg.getAbilityHandler().isActive(player))
							{
								if(rg.getAbilityHandler().getAbilityEffect(player) != null && rg.getAbilityHandler().getAbilityEffect(player).equals(AbilityEffect.VELOCITY_BOOST))
								{
									mpb = 9.0;
									Audio.COMBAT_KILL.playGlobal(player.getLocation());
									
									setEnergy(getEnergy() - 20);
								}
							}
						}
					}
				}
			}
			
			mbn += mpb;
			
			if(!((Arrow) e.getProjectile()).isCritical())
			{
				mbn = 1;
			}
			
			setSpeed(player, (Arrow) e.getProjectile(), mbn);
		}
		
		else
		{
			e.getProjectile().setVelocity(e.getProjectile().getVelocity());
		}
		
		e.getProjectile().setVelocity(e.getProjectile().getVelocity());
	}
	
	public void setSpeed(Player player, Arrow arrow, double multiple)
	{
		Vector i = player.getLocation().getDirection();
		Vector j = arrow.getVelocity();
		Vector r = i.add(j);
		Vector v = r.normalize();
		Vector velocity = v.multiply(multiple);
		arrow.setVelocity(velocity);
		arrow.setVelocity(arrow.getVelocity());
		
		if(multiple > 6)
		{
			teleportEffect(arrow);
		}
	}
	
	public void teleportEffect(Arrow arrow)
	{
		tickingArrows.put(arrow, arrow.getVelocity());
		
		for(Player j : gc.pl().onlinePlayers())
		{
			if(FastMath.isInRadius(arrow.getLocation(), j.getLocation(), 64))
			{
				NMS.sendEntityVelocity(arrow.getEntityId(), 0.0, 0.0, 0.0, j);
			}
		}
	}
	
	public void tickArrows()
	{
		for(Arrow i : new GList<Arrow>(tickingArrows.keySet()))
		{
			if(i.isDead())
			{
				tickingArrows.remove(i);
				continue;
			}
			
			ParticleEffect.FIREWORKS_SPARK.display(0f, 0f, 0f, 0.1f, 8, i.getLocation(), 256);
			
			for(Player j : gc.pl().onlinePlayers())
			{
				if(FastMath.isInRadius(i.getLocation(), j.getLocation(), 64))
				{
					NMS.sendEntityTeleport(i, i.getLocation(), j);
				}
			}
		}
	}
	
	public void fireArrow(Vector cloned)
	{
		if(gc.getGame(player).getType().equals(GameType.REGIONED))
		{
			RegionedGame rg = (RegionedGame) gc.getGame(player);
			player.launchProjectile(Arrow.class, cloned);
			rg.getMap().getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 1f, 1.4f);
			
			if(rg.getAbilityHandler().isActive(player))
			{
				if(rg.getAbilityHandler().getAbilityEffect(player) != null && rg.getAbilityHandler().getAbilityEffect(player).equals(AbilityEffect.VELOCITY_BOOST))
				{
					Audio.COMBAT_KILL.playGlobal(player.getLocation());
				}
			}
		}
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public Tool getTool()
	{
		return tool;
	}
	
	public boolean hasTool()
	{
		return tool != null;
	}
	
	public boolean isHoldingTool()
	{
		return player.getInventory().getHeldItemSlot() == 7 && getTool() != null;
	}
	
	public boolean isHoldingsMOD()
	{
		return player.getInventory().getHeldItemSlot() == 6 && getsMOD() != null;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e)
	{
		if(e.getPlayer().equals(player))
		{
			if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				if(isHoldingTool() && player.getGameMode().equals(GameMode.ADVENTURE))
				{
					if(gc.getGame(player) != null && gc.getGame(player).getType().equals(GameType.REGIONED))
					{
						((RegionedGame) gc.getGame(player)).getToolHandler().fireTool(player, tool);
					}
				}
				
				else if(isHoldingsMOD() && player.getGameMode().equals(GameMode.ADVENTURE))
				{
					if(gc.getGame(player) != null && gc.getGame(player).getType().equals(GameType.REGIONED))
					{
						useInjectable();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onArrow(EntityDamageByEntityEvent e)
	{
		boolean shouldDie = false;
		
		if(e.getDamager().getType().equals(EntityType.ARROW))
		{
			Arrow a = (Arrow) e.getDamager();
			ProjectileSource s = a.getShooter();
			Double damage = 6.0;
			Player hit = null;
			Boolean go = false;
			
			if(s instanceof LivingEntity)
			{
				LivingEntity l = (LivingEntity) s;
				
				if(l.getType().equals(EntityType.PLAYER))
				{
					if(e.getEntity().getType().equals(EntityType.PLAYER))
					{
						Game game = gc.getGame((Player) e.getEntity());
						Game gameb = gc.getGame((Player) l);
						
						if(game != null && gameb != null)
						{
							if(game.equals(gameb))
							{
								if(game.getType().equals(GameType.REGIONED))
								{
									go = true;
									Player shooter = (Player) l;
									hit = (Player) e.getEntity();
									RegionedGame rg = (RegionedGame) game;
									
									arrows.remove(a);
									
									if(rg.getFactionHandler().getFaction(shooter).equals(rg.getFactionHandler().getFaction(hit)))
									{
										e.setCancelled(true);
										return;
									}
									
									else
									{
										
										final Player hitx = hit;
										
										if(player.equals(hit))
										{
											rg.getToolHandler().cooldown(hit);
											
											hit.setVelocity(new Vector(0, 0, 0));
											
											rg.getPl().scheduleSyncTask(1, new Runnable()
											{
												@Override
												public void run()
												{
													hitx.setVelocity(new Vector(0, 0, 0));
												}
											});
											
											if(getAbility().getAbilityEffect().equals(AbilityEffect.CLOAK))
											{
												if(rg.getAbilityHandler().isActive(hit))
												{
													rg.getAbilityHandler().unc(hit);
												}
											}
											
											if(getGc().gpo(shooter).getAbility().getAbilityEffect().equals(AbilityEffect.VELOCITY_BOOST))
											{
												if(rg.getAbilityHandler().isActive(shooter))
												{
													damage = shooter.getLocation().distance(hit.getLocation()) * 0.4;
													shooter.sendMessage(ChatColor.GOLD + "Distance: " + damage / 0.4 + ChatColor.RED + " Damage: " + damage);
													
													gc.pl().scheduleSyncTask(10, new Runnable()
													{
														@Override
														public void run()
														{
															rg.getExperienceHandler().giveXp(shooter, 100 + (long) (700 * Math.random()), Experience.KILL_BONUS);
														}
													});
													
													if(damage > hit.getHealth() + gc.gpo(hit).getShields())
													{
														DeathEvent de = new DeathEvent((RegionedGame) game, hit, shooter, 1000.0);
														rg.pl().callEvent(de);
													}
												}
											}
										}
										
										double dis = hit.getLocation().distance(shooter.getLocation());
										
										if(dis > Statistic.COMBAT_BOW_LONGEST.get(shooter))
										{
											Statistic.COMBAT_BOW_LONGEST.set(shooter, dis);
										}
										
										Statistic.COMBAT_BOW_SHOT.add(shooter);
										
										if(!a.isCritical())
										{
											Statistic.COMBAT_BOW_CHEAPSHOT.add(shooter);
											Statistic.COMBAT_BOW_CHEAPRATE.set(shooter, 100.0 * (Statistic.COMBAT_BOW_CHEAPSHOT.get(shooter) / Statistic.COMBAT_BOW_SHOT.get(shooter)));
										}
									}
								}
							}
						}
					}
					
					PlayerObject po = gc.gpo((Player) l);
					e.setDamage(Math.abs(po.arrowDamage() / 4));
					
					if(a.isCritical())
					{
						e.setDamage(Math.abs(po.arrowDamage()));
					}
					
					if(e.getEntityType().equals(EntityType.PLAYER))
					{
						resetActions((Player) l);
					}
					
					if(go)
					{
						damage = gc.gpo(hit).adamage(damage);
						e.setDamage(damage);
					}
				}
			}
		}
		
		if(shouldDie)
		{
			
		}
	}
	
	@EventHandler
	public void onArrow(ProjectileHitEvent e)
	{
		if(e.getEntity().getType().equals(EntityType.ARROW))
		{
			final Arrow a = (Arrow) e.getEntity();
			
			if(a.getShooter() instanceof LivingEntity)
			{
				LivingEntity l = (LivingEntity) a.getShooter();
				
				if(l.getType().equals(EntityType.PLAYER))
				{
					if(((Player) l).equals(player))
					{
						arrows.add((Arrow) e.getEntity());
						
						gc.pl().scheduleSyncTask(1, new Runnable()
						{
							@Override
							public void run()
							{
								if(arrows.contains(a))
								{
									Statistic.COMBAT_BOW_MISS.add(player);
									arrows.remove(a);
									
									Statistic.COMBAT_BOW_ACCURACY.set(player, 100.0 * (Statistic.COMBAT_BOW_SHOT.get(player) / (Statistic.COMBAT_BOW_SHOT.get(player) + Statistic.COMBAT_BOW_MISS.get(player))));
								}
								
								a.remove();
							}
						});
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		e.setCancelled(true);
		
		if(!e.getPlayer().equals(player))
		{
			return;
		}
		
		if(gc.getGame(e.getPlayer()) == null)
		{
			return;
		}
		
		if(System.currentTimeMillis() - lastFire < 1000)
		{
			Audio.UI_FAIL.play(e.getPlayer());
			return;
		}
		
		if(!gc.getGame(e.getPlayer()).getType().equals(GameType.REGIONED))
		{
			Audio.UI_FAIL.play(e.getPlayer());
			return;
		}
		
		if(ability == null)
		{
			Audio.UI_FAIL.play(e.getPlayer());
			return;
		}
		
		ActivateAbilityEvent ae = new ActivateAbilityEvent((RegionedGame) gc.getGame(e.getPlayer()), e.getPlayer(), ability);
		gc.pl().callEvent(ae);
		
		if(!ae.isCancelled())
		{
			lastFire = System.currentTimeMillis();
			gc.pl().callEvent(new FireAbilityEvent((RegionedGame) gc.getGame(e.getPlayer()), player, ability));
		}
		
		else
		{
			Audio.UI_FAIL.play(e.getPlayer());
		}
	}
	
	public GMap<Arrow, Vector> getTickingArrows()
	{
		return tickingArrows;
	}
	
	public Boolean getBuilt()
	{
		return built;
	}
	
	@EventHandler
	public void onCombat(CombatEvent e)
	{
		if(!e.getDamager().equals(player))
		{
			return;
		}
		
		dehook();
		
		if(getEffect().equals(WeaponEffect.FROST))
		{
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2, true, false));
		}
		
		int sl = e.getDamager().getInventory().getHeldItemSlot();
		
		if(sl == 0)
		{
			if(primary != null)
			{
				if(primary.getWeaponType().equals(WeaponType.MELEE))
				{
					MeleeWeapon mw = (MeleeWeapon) primary;
					
					double dam = mw.getDamage();
					double dbm = 0.0;
					
					for(MeleeWeaponUpgrade i : gc.getObtainableBank().getObtainableFilter().getMeleeWeaponUpgrades())
					{
						dbm += (dam * i.getDamageModifier());
					}
					
					e.setDamage(dam + dbm);
				}
			}
		}
		
		else if(sl == 1)
		{
			if(secondary != null)
			{
				if(secondary.getWeaponType().equals(WeaponType.MELEE))
				{
					MeleeWeapon mw = (MeleeWeapon) secondary;
					
					double dam = mw.getDamage();
					double dbm = 0.0;
					
					for(MeleeWeaponUpgrade i : gc.getObtainableBank().getObtainableFilter().getMeleeWeaponUpgrades())
					{
						dbm += (dam * i.getDamageModifier());
					}
					
					e.setDamage(dam + dbm);
				}
			}
		}
		
		else if(sl == 2)
		{
			if(tertiary != null)
			{
				if(tertiary.getWeaponType().equals(WeaponType.MELEE))
				{
					MeleeWeapon mw = (MeleeWeapon) tertiary;
					
					double dam = mw.getDamage();
					double dbm = 0.0;
					
					for(MeleeWeaponUpgrade i : gc.getObtainableBank().getObtainableFilter().getMeleeWeaponUpgrades())
					{
						dbm += (dam * i.getDamageModifier());
					}
					
					e.setDamage(dam + dbm);
				}
			}
		}
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Weapon getPrimary()
	{
		return primary;
	}
	
	public Weapon getSecondary()
	{
		return secondary;
	}
	
	public Weapon getTertiary()
	{
		return tertiary;
	}
	
	public Ability getAbility()
	{
		return ability;
	}
	
	public Projectile getProjectile()
	{
		return projectile;
	}
	
	public GameController getGc()
	{
		return gc;
	}
	
	public Double getShields()
	{
		return shields;
	}
	
	public double adamage(double dmg)
	{
		if(damaged)
		{
			if(shields <= 0)
			{
				return dmg;
			}
			
			else
			{
				return 0.0;
			}
		}
		
		return damage(dmg);
	}
	
	public double damage(double dmg)
	{
		if(damaged)
		{
			return 0;
		}
		
		damaged = true;
		waitreg = (int) (getShield().getCooldown() * 20);
		
		if(getAbility().getAbilityEffect().equals(AbilityEffect.SHIELD) && abilityActive)
		{
			double eng = energy / 6.0;
			
			if(dmg <= eng)
			{
				eng = eng - dmg;
				ParticleEffect.LAVA.display(0, 1, 0, 1.9f, 12, player.getLocation().clone().subtract(0, 1, 0), 48);
				Audio.ABILITY_SHK.playGlobal(player.getLocation());
				setEnergy(eng * 6.0);
				dmg = 0;
			}
			
			else
			{
				dmg = dmg - eng;
				ParticleEffect.LAVA.display(0, 1, 0, 1.9f, 12, player.getLocation().clone().subtract(0, 1, 0), 48);
				Audio.ABILITY_SHK.playGlobal(player.getLocation());
				setEnergy(0.0);
			}
		}
		
		if(getShields() > 0)
		{
			if(dmg > getShield().getMitigation())
			{
				((RegionedGame) getGc().getGame(player)).getNotificationHandler().queue(player, new Notification(null, null, ChatColor.RED + (dmg - getShield().getMitigation() + " Damage Mitigated")));
				dmg = getShield().getMitigation();
				Audio.SHIELD_MITIGATED.playGlobal(player.getLocation());
			}
			
			if(getShields() > dmg)
			{
				shields = (shields - dmg);
				return 0;
			}
			
			else
			{
				double sShield = shields;
				shields = 0.0;
				Audio.SHIELD_DOWN.playGlobal(player.getLocation());
				ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1.5f, 0.5f, (float) Math.random(), 50, player.getLocation().add(0, 1, 0), 48);
				((RegionedGame) getGc().getGame(player)).getNotificationHandler().queue(player, new Notification(null, null, ChatColor.RED + "Shields are DOWN!"));
				return (dmg - sShield);
			}
		}
		
		else
		{
			return dmg;
		}
	}
	
	public void upSh()
	{
		shields++;
	}
	
	public void destroyRecharger(int i)
	{
		waitreg += i;
	}
	
	public Injectable getsMOD()
	{
		return sMOD;
	}
	
	public void setsMOD(Injectable sMOD)
	{
		this.sMOD = sMOD;
	}
	
	public Boolean getEmpt()
	{
		return empt;
	}
	
	public void setEmpt(Boolean empt)
	{
		this.empt = empt;
	}
}
