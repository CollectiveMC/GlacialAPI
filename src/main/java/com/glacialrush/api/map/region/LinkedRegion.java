package com.glacialrush.api.map.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.object.GList;

public class LinkedRegion extends Region
{
	protected Faction faction;
	protected GList<Location> spawns;
	
	public LinkedRegion(Map map, String name, RegionType type, Game game)
	{
		super(map, name, type, game);
		
		faction = Faction.neutral();
		spawns = new GList<Location>();
	}
	
	public Faction getFaction()
	{
		return faction;
	}
	
	public GList<Location> getSpawns()
	{
		return spawns;
	}
	
	public void setSpawns(GList<Location> spawns)
	{
		this.spawns = spawns;
	}
	
	public void setFaction(Faction faction)
	{
		this.faction = faction;
	}
	
	public Location centerSpawn()
	{
		Vector v = new Vector();
		
		for(Location i : getSpawns())
		{
			v.add(i.toVector());
		}
		
		v.setX(v.getX() / (double) getSpawns().size());
		v.setY(v.getY() / (double) getSpawns().size());
		v.setZ(v.getZ() / (double) getSpawns().size());
		
		return v.toLocation(map.getWorld());
	}
	
	public void preBuild()
	{
		super.preBuild();
		spawns.clear();
	}
	
	public void build(Block block)
	{
		super.build(block);
		
		if(block.getType().equals(Material.CARPET))
		{
			spawns.add(block.getLocation());
		}
	}
	
	public void spawn(Player p)
	{
		p.teleport(spawns.pickRandom());
	}
}
