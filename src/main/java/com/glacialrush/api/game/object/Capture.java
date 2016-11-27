package com.glacialrush.api.game.object;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.phantomapi.world.Area;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.map.region.Village;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.GVFX;
import com.glacialrush.xapi.FastMath;

public class Capture implements Influenced
{
	private Territory territory;
	private Location location;
	private Integer progress;
	private Faction offense;
	private Faction faction;
	private Faction last;
	private Float range;
	private GMap<Player, Integer> influence;
	
	public static final Integer offendCount = 1;
	public static final Integer blocks = 20;
	
	public Capture(Territory territory, Location location)
	{
		this.territory = territory;
		this.location = location;
		this.progress = 100;
		this.range = 9f;
		this.faction = territory.getFaction();
		this.influence = new GMap<Player, Integer>();
		this.offense = null;
		this.last = null;
	}
	
	public Float getRange()
	{
		return range;
	}
	
	public void setRange(Float range)
	{
		this.range = range;
	}
	
	public GMap<Player, Integer> getInfluence()
	{
		return influence;
	}
	
	public void setInfluence(GMap<Player, Integer> influence)
	{
		this.influence = influence;
	}
	
	public static Integer getOffendcount()
	{
		return offendCount;
	}
	
	public static Integer getBlocks()
	{
		return blocks;
	}
	
	public String captureGraph()
	{
		String graph = "";
		
		if(offense != null)
		{
			graph = graph + faction.getColor() + ChatColor.STRIKETHROUGH;
			
			int prg = progress / (100 / blocks);
			
			for(int i = 0; i < prg; i++)
			{
				graph = graph + "-";
			}
			
			graph += "-[";
			
			graph = graph + offense.getColor() + ChatColor.STRIKETHROUGH;
			
			graph += "]-";
			
			for(int i = 0; i < blocks - prg; i++)
			{
				graph = graph + "-";
			}
		}
		
		else
		{
			graph = graph + faction.getColor() + ChatColor.STRIKETHROUGH;
			
			for(int i = 0; i < blocks; i++)
			{
				graph = graph + "-";
			}
		}
		
		return graph;
	}
	
	public void offend(Faction f)
	{
		Area a = new Area(getLocation(), 3.3);
		
		for(int i = 0; i < 32; i++)
		{
			GVFX.particle(f, a.random());
		}
		
		int k = 1;
		
		for(Player i : getPlayers())
		{
			if(Faction.get(i).equals(f))
			{
				k++;
			}
		}
		
		if(f.equals(faction))
		{
			progress += (offendCount * k);
			
			if(progress >= 100)
			{
				reset(f);
			}
			
			return;
		}
		
		if(offense == null)
		{
			offense = f;
		}
		
		if(!offense.equals(f))
		{
			progress += (offendCount * k);
			
			if(progress >= 100)
			{
				reset(offense);
			}
			
			accentRandom(offense);
		}
		
		else
		{
			progress -= (offendCount * k);
			
			if(progress <= 0)
			{
				reset(offense);
			}
			
			accentRandom(offense);
		}
	}
	
	public boolean canCapture(Faction f)
	{
		if(territory.getFaction().equals(f))
		{
			return true;
		}
		
		for(Region i : territory.getBorders())
		{
			if(i.getType().equals(RegionType.TERRITORY))
			{
				Territory t = (Territory) i;
				
				if(t.getFaction().equals(f))
				{
					return true;
				}
			}
			
			if(i.getType().equals(RegionType.VILLAGE))
			{
				Village t = (Village) i;
				
				if(t.getFaction().equals(f))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void reset(Faction f)
	{
		faction = f;
		offense = null;
		progress = 100;
	}
	
	public void build()
	{
		Job job = new Job("Capture Build", territory.getMap().getGame());
		Block b = location.getBlock();
		
		job.queue(b.getRelative(BlockFace.DOWN).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST).getLocation(), Material.IRON_BLOCK);
		job.queue(b.getRelative(BlockFace.UP).getLocation(), Material.STAINED_GLASS);
		job.queue(b.getRelative(BlockFace.UP).getLocation(), Material.STAINED_GLASS);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), Material.STAINED_CLAY);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), Material.STAINED_GLASS);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), Material.STAINED_GLASS);
		
		job.flush();
	}
	
	public void accentRandom(Faction nf)
	{
		if(nf == null)
		{
			return;
		}
		
		Job jobf = new Job("Capture Accent", territory.getMap().getGame());
		DyeColor dye = nf.getDyeColor();
		Block b = location.getBlock();
		
		jobf.queue(b.getRelative(BlockFace.UP).getLocation(), Material.STAINED_GLASS);
		jobf.queue(b.getRelative(BlockFace.UP).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), dye);
		jobf.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), dye);
		
		Job j = new Job("Capture Random Accent", territory.getMap().getGame());
		
		j.queue(jobf.getOperations().pickRandom());
		
		j.flush();
	}
	
	public void accent()
	{
		accent(false);
	}
	
	public void accent(boolean force)
	{
		if(faction.equals(last) && !force)
		{
			return;
		}
		
		last = faction;
		Job job = new Job("Capture Accent", territory.getMap().getGame());
		DyeColor dye = faction.getDyeColor();
		Block b = location.getBlock();
		
		job.queue(b.getRelative(BlockFace.UP).getLocation(), Material.STAINED_GLASS);
		job.queue(b.getRelative(BlockFace.UP).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), dye);
		job.queue(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), dye);
		
		job.flush();
	}
	
	public GList<Player> getPlayers()
	{
		GList<Player> pls = new GList<Player>();
		
		for(Player i : territory.getPlayers())
		{
			if(FastMath.isInRadius(i.getLocation(), location, range))
			{
				if(FastMath.losStrict(getLocation().clone().add(new Vector(0, 3, 0)), i.getLocation().add(new Vector(0, 1, 0))))
				{
					pls.add(i);
				}
			}
		}
		
		return pls;
	}
	
	public Territory getTerritory()
	{
		return territory;
	}
	
	public void setTerritory(Territory territory)
	{
		this.territory = territory;
	}
	
	public Location getLocation()
	{
		return location.clone();
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public Integer getProgress()
	{
		return progress;
	}
	
	public void setProgress(Integer progress)
	{
		this.progress = progress;
	}
	
	public Faction getOffense()
	{
		return offense;
	}
	
	public void setOffense(Faction offense)
	{
		this.offense = offense;
	}
	
	public Faction getFaction()
	{
		return faction;
	}
	
	public void setFaction(Faction faction)
	{
		this.faction = faction;
	}
	
	public Faction getLast()
	{
		return last;
	}
	
	public void setLast(Faction last)
	{
		this.last = last;
	}
	
	@Override
	public GMap<Player, Integer> getInfluenceMap()
	{
		return influence;
	}
	
	@Override
	public void resetInfluenceMap()
	{
		influence.clear();
	}
	
	@Override
	public void influence(Player p, Integer i)
	{
		influence.put(p, (influence.containsKey(p) ? influence.get(p) : 0) + i);
	}
	
	@Override
	public GMap<Player, Integer> popInfluenceMap()
	{
		GMap<Player, Integer> ic = influence.copy();
		resetInfluenceMap();
		return ic;
	}
	
	@Override
	public void injectInfluence(GMap<Player, Integer> influence)
	{
		for(Player p : influence.keySet())
		{
			influence(p, influence.get(p));
		}
	}
	
	@Override
	public double getInfluence(Player p)
	{
		int ti = 0;
		
		for(Player i : influence.keySet())
		{
			ti += influence.get(i);
		}
		
		if(!influence.containsKey(p))
		{
			return 0.0;
		}
		
		return (double) ((double) influence.get(p) / (double) ti);
	}

	public void soundOff()
	{
		for(Player i : getPlayers())
		{
			if(Faction.get(i).equals(getFaction()))
			{
				Audio.CAPTURE_AMBIENT_ALLY.play(i, getLocation());
			}
			
			else
			{
				Audio.CAPTURE_AMBIENT_ENEMY.play(i, getLocation());
			}
		}
	}
}
