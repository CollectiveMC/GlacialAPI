package com.glacialrush.api.game.handler;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.object.Job;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.object.GBiset;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;

public class WorldHandler extends GlacialHandler
{
	private GMap<Location, GBiset<Material, Byte>> log;
	
	public WorldHandler(Game game)
	{
		super(game);
		
		this.log = new GMap<Location, GBiset<Material, Byte>>();
	}
	
	public boolean pass(Block block)
	{
		if(game.getType().equals(GameType.REGIONED))
		{
			if(!((RegionedGame)game).getMap().contains(block))
			{
				return false;
			}
		}
		
		if(block.getType().equals(Material.GLASS) || block.getType().equals(Material.THIN_GLASS))
		{
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void destroy(Location location)
	{
		if(!pass(location.getBlock()))
		{
			return;
		}
		
		log.put(location, new GBiset<Material, Byte>(location.getBlock().getType(), location.getBlock().getData()));
		location.getWorld().playEffect(location, Effect.STEP_SOUND, location.getBlock().getType(), 32);
		location.getBlock().setType(Material.AIR);
		
		destroy(location.getBlock().getRelative(BlockFace.DOWN).getLocation());
		destroy(location.getBlock().getRelative(BlockFace.UP).getLocation());
		destroy(location.getBlock().getRelative(BlockFace.NORTH).getLocation());
		destroy(location.getBlock().getRelative(BlockFace.SOUTH).getLocation());
		destroy(location.getBlock().getRelative(BlockFace.EAST).getLocation());
		destroy(location.getBlock().getRelative(BlockFace.WEST).getLocation());
	}
	
	public void restore(Region region)
	{
		Job j = new Job("World Resore Operation RG: " + region.getName(), game);
		
		for(Location i : new GList<Location>(log.keySet()))
		{
			if(region.contains(i))
			{
				j.queue(i, log.get(i).getA(), log.get(i).getB());
				log.remove(i);
			}
		}
		
		j.flush();
	}
	
	public void restore()
	{
		Job j = new Job("World Resore Operation", game);
		
		for(Location i : log.keySet())
		{
			j.queue(i, log.get(i).getA(), log.get(i).getB());
		}
		
		j.flush();
		log.clear();
	}
}
