package com.glacialrush.api.game.object;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Droplet
{
	public void setLocation(Location l);
	public Location getLocation();
	public void reward(Player p);
	public void animate();
	public Entity getBoundEntity();
}
