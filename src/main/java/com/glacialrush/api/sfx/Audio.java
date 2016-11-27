package com.glacialrush.api.sfx;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.glacialrush.api.GPlugin;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.object.GSound;

public enum Audio
{
	ABILITY_RAGE("g.event.combat.grenade", 2f, 1.5f, 0f, 0.3f, 0f),
	ABILITY_SHIELD("g.event.ability.shield", 2f, 1f, 0f, 0.3f, 0f),
	SHIELD_DOWN("g.event.combat.shielddown", 2f, 1f, 0f, 0f, 0f),
	ABILITY_SHK("g.event.combat.shielddown", 2f, 0.9f, 0f, 0.3f, 0f),
	SHIELD_UP("g.event.combat.shieldup", 2f, 1f, 0f, 0f, 0f),
	SHIELD_MITIGATED("g.event.combat.shieldmitigated", 2f, 1f, 0f, 0f, 0f),
	CAPTURE_CAPPED("g.event.combat.shieldmitigated", 2f, 1.5f, 0f, 0f, 0f),
	ABILITY_HEAL("g.event.ability.heal", 2f, 1f, 0f, 0f, 0f),
	ABILITY_STEADYAIM("g.event.ability.steadyaim", 2f, 1f, 0f, 0f, 0f),
	ABILITY_TELEPORT("g.event.ability.teleport", 2f, 1f, 0f, 0f, 0f),
	ABILITY_REPELL("g.event.combat.grenade", 2f, 1.5f, 0f, 0.3f, 0f),
	ABILITY_CLOAK("g.event.ability.cloak", 2f, 0.9f, 0f, 0f, 0f),
	ABILITY_CLOAK_DIE("g.event.ability.cloak", 2f, 1.2f, 0f, 0f, 0f),
	MONSTER("g.event.combat.monster", 2f, 1.0f, 0f, 0.4f, 0f),
	COMBAT_KILL("g.event.combat.kill", 2f, 1f, 0f, 0f, 0f),
	COMBAT_INSANEKILL("g.event.combat.insanekill", 10f, 1f, 0f, 0f, 0f),
	COMBAT_BLOCK("g.event.combat.block", 2f, 1.3f, 0f, 0.2f, 0f),
	COMBAT_CENA("g.event.combat.cena", 2f, 1.0f, 0f, 0f, 0f),
	CAPTURE_CAPTURE("g.event.capture.capture", 2f, 1f, 0f, 0f, 0f),
	CAPTURE_AMBIENT_ENEMY("g.event.ambient.enemy", 0.223f, 1f, 0f, 0f, 0f),
	CAPTURE_AMBIENT_ALLY("g.event.ambient.ally", 0.223f, 1f, 0f, 0f, 0f),
	UI_CLICK("g.event.ui.click", 2f, 1f, 0f, 0f, 0f),
	MUSIC_VICTORY("g.music.victory", 20f, 1f, 0f, 0f, 0f),
	MUSIC_STARTER("g.music.starter", 20f, 1f, 0f, 0f, 0f),
	MUSIC_COUNTDOWN("g.music.countdown", 20f, 1.2f, 0f, 0f, 0f),
	UI_OPEN("g.event.ui.open", 2f, 1f, 0f, 0f, 0f),
	UI_CLOSE("g.event.ui.close", 2f, 1f, 0f, 0f, 0f),
	THROWABLE("g.event.combat.grenadepin", 2f, 1.0f, 0f, 0.256f, 0f),
	GRENADE_BOOM("g.event.combat.grenade", 12f, 1f, 0f, 0.4f, 0f),
	EMP("g.event.combat.emp", 12f, 1f, 0f, 0f, 0f),
	ABILITY_DOWN("g.event.ability.teleport", 1f, 1.7f, 0f, 0f, 0f),
	GRENADE_BOOM_DULL("g.event.combat.grenadedull", 0.4f, 1f, 0f, 0f, 0f),
	UI_FAIL("g.event.ui.fail", 2f, 1f, 0f, 0f, 0f),
	COMBAT_INJECT("g.event.combat.inject", 2f, 1f, 0f, 0f, 0f),
	COMBAT_KILLED("g.event.kill", 2f, 1f, 0f, 0f, 0f),
	UI_ACTION("g.event.ui.action", 2f, 1f, 0f, 0f, 0f),
	HUD_TELEPORT("g.event.hud.teleport", 2f, 1f, 0f, 0f, 0f),
	REGION_LOST("g.event.region.lost", 2f, 1f, 0f, 0f, 0f),
	REGION_CAPTURE("g.event.region.capture", 2f, 1f, 0f, 0f, 0f),
	GRAPPLE(Sound.SHOOT_ARROW, 2f, 1.6f, 0f, 0f, 0f),
	EXPERIENCE_EARN("g.event.experience.earn", 2f, 1f, 0f, 0f, 0f),
	EXPERIENCE_RANKUP("g.event.experience.rankup", 2f, 1f, 0f, 0f, 0f);
	
	private float volume;
	private float pitch;
	private float randomVolume;
	private float randomPitch;
	private float randomArea;
	private String source;
	private Sound iSource;
	
	private Audio(String source, float volume, float pitch, float randomVolume, float randomPitch, float randomArea)
	{
		this.source = source;
		this.volume = volume;
		this.pitch = pitch;
		this.randomVolume = Math.abs(randomVolume);
		this.randomPitch = Math.abs(randomPitch);
		this.randomArea = Math.abs(randomArea);
	}
	
	private Audio(Sound iSource, float volume, float pitch, float randomVolume, float randomPitch, float randomArea)
	{
		this.iSource = iSource;
		this.volume = volume;
		this.pitch = pitch;
		this.randomVolume = Math.abs(randomVolume);
		this.randomPitch = Math.abs(randomPitch);
		this.randomArea = Math.abs(randomArea);
	}
	
	public static void clear(Player p)
	{
		
	}
	
	public static void loadAll(final Player p, GPlugin pl)
	{
		
	}
	
	public static void loada(Player p)
	{
		MUSIC_COUNTDOWN.play(p);
		clear(p);
	}
	
	public static void loadb(Player p)
	{
		MUSIC_STARTER.play(p);
		clear(p);
	}
	
	public void playGlobal(Location l)
	{
		Random r = new Random();
		
		Float volume = r.nextBoolean() ? this.volume + (r.nextFloat() * randomVolume) : this.volume - (r.nextFloat() * randomVolume);
		Float pitch = r.nextBoolean() ? this.pitch + (r.nextFloat() * randomPitch) : this.pitch - (r.nextFloat() * randomPitch);
		Vector v = new Vector(r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea), r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea), r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea));
		GSound s;
		
		if(source == null)
		{
			s = new GSound(iSource, volume, pitch);
		}
		
		else
		{
			s = new GSound(source, volume, pitch);
		}
		
		s.play(l.clone().add(v));
	}
	
	public void play(Player p)
	{
		if(GlacialPlugin.instance().gpd(p) != null && !GlacialPlugin.instance().gpd(p).getCustomSounds())
		{
			return;
		}
		
		Random r = new Random();
		
		Float volume = r.nextBoolean() ? this.volume + (r.nextFloat() * randomVolume) : this.volume - (r.nextFloat() * randomVolume);
		Float pitch = r.nextBoolean() ? this.pitch + (r.nextFloat() * randomPitch) : this.pitch - (r.nextFloat() * randomPitch);
		Vector v = new Vector(r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea), r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea), r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea));
		GSound s;
		
		if(source == null)
		{
			s = new GSound(iSource, volume, pitch);
		}
		
		else
		{
			s = new GSound(source, volume, pitch);
		}
		
		s.play(p, v);
	}
	
	public void play(Player p, Location l)
	{
		if(GlacialPlugin.instance().gpd(p) != null && !GlacialPlugin.instance().gpd(p).getCustomSounds())
		{
			return;
		}
		
		Random r = new Random();
		
		Float volume = r.nextBoolean() ? this.volume + (r.nextFloat() * randomVolume) : this.volume - (r.nextFloat() * randomVolume);
		Float pitch = r.nextBoolean() ? this.pitch + (r.nextFloat() * randomPitch) : this.pitch - (r.nextFloat() * randomPitch);
		Vector v = new Vector(r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea), r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea), r.nextBoolean() ? 1f + (r.nextFloat() * randomArea) : 1f - (r.nextFloat() * randomArea));
		GSound s;
		
		if(source == null)
		{
			s = new GSound(iSource, volume, pitch);
		}
		
		else
		{
			s = new GSound(source, volume, pitch);
		}
		
		s.play(p, l.clone().add(v));
	}
}
