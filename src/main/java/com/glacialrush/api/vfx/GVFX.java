package com.glacialrush.api.vfx;

import java.util.Random;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.object.GMap;

public class GVFX
{
	private static GMap<Location, Faction> buffer = new GMap<Location, Faction>();
	
	public static void particle(Faction f, Location l)
	{
		Color c = f.getDyeColor().getColor();
		org.phantomapi.vfx.ParticleEffect.OrdinaryColor oc = new org.phantomapi.vfx.ParticleEffect.OrdinaryColor(c.getRed(), c.getGreen(), c.getBlue());
		org.phantomapi.vfx.ParticleEffect.REDSTONE.display(oc, l, 128);
	}
	
	public static void tick()
	{
		for(Location i : buffer.keySet())
		{
			fireworkBoom(i, buffer.get(i));
		}
		
		buffer.clear();
	}
	
	public static void firework(Location l, Faction f)
	{
		buffer.put(l, f);
	}
	
	public static void fireworkBoom(Location l, Faction f)
	{
		if(l.getWorld().getTime() < 14000)
		{
			return;
		}
		
		Color c = Color.AQUA;
		
		if(f.equals(Faction.enigma()))
		{
			c = Color.RED;
		}
		
		else if(f.equals(Faction.omni()))
		{
			c = Color.PURPLE;
		}
		
		else if(f.equals(Faction.cryptic()))
		{
			c = Color.YELLOW;
		}
		
		Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		Random r = new Random();
		Type type = Type.BALL_LARGE;
		Color c1 = c;
		Color c2 = c;
		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
		fwm.addEffect(effect);
		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);
		fw.setFireworkMeta(fwm);
	}
}
