package com.glacialrush.api.map;

import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.data.MapConfiguration;
import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.Influenced;
import com.glacialrush.api.map.region.LinkedRegion;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.map.region.Village;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.xapi.FastMath;
import org.bukkit.ChatColor;

public class Map implements Listener, Influenced
{
	protected final GlacialPlugin pl;
	protected String name;
	protected GList<Region> regions;
	protected Game game;
	protected MapDrawManager mdm;
	protected boolean locked;
	protected boolean modified;
	protected World world;
	protected GMap<Player, Integer> influence;
	protected MapConfiguration mapConfiguration;
	
	public Map(String name, Game game, World world)
	{
		this.pl = GlacialPlugin.instance();
		this.name = name;
		this.regions = new GList<Region>();
		this.game = game;
		this.mdm = new MapDrawManager(this);
		this.locked = false;
		this.modified = false;
		this.world = world;
		this.influence = new GMap<Player, Integer>();
		this.mapConfiguration = new MapConfiguration();
		pl.register(this);
	}
	
	public GlacialPlugin getPl()
	{
		return pl;
	}
	
	public Chunklet getChunkletMin()
	{
		int x = Integer.MAX_VALUE;
		int z = Integer.MAX_VALUE;
		
		for(Region i : regions)
		{
			for(Chunklet j : i.chunklets)
			{
				if(j.getX() < x)
				{
					x = j.getX();
				}
				
				if(j.getZ() < z)
				{
					z = j.getZ();
				}
			}
		}
		
		return new Chunklet(x, z, getWorld());
	}
	
	public Chunklet getChunkletMax()
	{
		int x = Integer.MIN_VALUE;
		int z = Integer.MIN_VALUE;
		
		for(Region i : regions)
		{
			for(Chunklet j : i.chunklets)
			{
				if(j.getX() > x)
				{
					x = j.getX();
				}
				
				if(j.getZ() > z)
				{
					z = j.getZ();
				}
			}
		}
		
		return new Chunklet(x, z, getWorld());
	}
	
	public int getWidth()
	{
		return Math.abs(getChunkletMax().getX() - getChunkletMin().getX());
	}
	
	public int getHeight()
	{
		return Math.abs(getChunkletMax().getZ() - getChunkletMin().getZ());
	}
	
	public Region getRegion(Chunklet chunklet)
	{
		for(Region i : regions)
		{
			if(i.contains(chunklet))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public Region getRegion(Player player)
	{
		for(Region i : regions)
		{
			if(i.contains(player))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public LinkedRegion getCloseLinkedRegion(Player p)
	{
		int dist = Integer.MAX_VALUE;
		LinkedRegion r = null;
		
		for(LinkedRegion i : getLinkedRegions())
		{
			if(!i.getFaction().equals(((RegionedGame) game).getFactionHandler().getFaction(p)))
			{
				continue;
			}
			
			int dx = (int) FastMath.distance2D(p.getLocation(), i.getSpawns().pickRandom());
			
			if(dx < dist)
			{
				dist = dx;
				r = i;
			}
		}
		
		if(r != null)
		{
			return r;
		}
		
		return ((RegionedGame) game).getWarpGate(((RegionedGame) game).getFactionHandler().getFaction(p));
	}
	
	public LinkedRegion getCloseLinkedRegion(Location l, Faction f)
	{
		double dist = Double.MAX_VALUE;
		LinkedRegion r = null;
		
		for(LinkedRegion i : getLinkedRegions())
		{
			if(!i.getFaction().equals(f))
			{
				continue;
			}
						
			double dx = FastMath.distance2D(l, i.centerSpawn());
			
			if(dx < dist)
			{
				dist = dx;
				r = i;
			}
		}
		
		if(r != null)
		{
			return r;
		}
		
		return ((RegionedGame) game).getWarpGate(f);
	}
	
	public GMap<Player, Integer> getInfluence()
	{
		return influence;
	}
	
	public void setInfluence(GMap<Player, Integer> influence)
	{
		this.influence = influence;
	}
	
	public MapConfiguration getMapConfiguration()
	{
		return mapConfiguration;
	}
	
	public void setMapConfiguration(MapConfiguration mapConfiguration)
	{
		this.mapConfiguration = mapConfiguration;
	}
	
	public int factionTerritoryCount(Faction f)
	{
		int c = 0;
		
		for(Territory i : getTerritories())
		{
			if(i.getFaction().equals(f))
			{
				c++;
			}
		}
		
		return c;
	}
	
	public int factionCaptureCount(Faction f)
	{
		int c = 0;
		
		for(Territory i : getTerritories())
		{
			for(Capture j : i.getCaptures())
			{
				if(j.getFaction().equals(f))
				{
					c++;
				}
			}
		}
		
		return c;
	}
	
	public void neutralize()
	{
		for(Region i : regions)
		{
			if(i.getType().equals(RegionType.TERRITORY) || i.getType().equals(RegionType.VILLAGE))
			{
				((LinkedRegion) i).setFaction(Faction.neutral());
			}
		}
	}
	
	public boolean contains(Chunk chunk)
	{
		for(Region i : regions)
		{
			if(i.contains(chunk))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean modified()
	{
		return modified;
	}
	
	public void lock()
	{
		locked = true;
	}
	
	public void unlock()
	{
		locked = false;
	}
	
	public boolean locked()
	{
		return locked;
	}
	
	@Override
	public GMap<Player, Integer> getInfluenceMap()
	{
		return influence;
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
		for(Region i : regions)
		{
			if(i.getType().equals(RegionType.TERRITORY))
			{
				Territory t = (Territory) i;
				injectInfluence(t.popInfluenceMap());
			}
			
			if(i.getType().equals(RegionType.VILLAGE))
			{
				Village t = (Village) i;
				injectInfluence(t.popInfluenceMap());
			}
		}
		
		GMap<Player, Integer> ic = influence.copy();
		resetInfluenceMap();
		return ic;
	}
	
	public void accentEvenley()
	{
		GList<Faction> frs = Faction.all().copy().shuffle();
		GMap<Faction, Region> cursor = new GMap<Faction, Region>();
		int fid = 0;
		int lock = 0;
		
		for(Territory i : getTerritories())
		{
			i.setFaction(Faction.neutral());
		}
		
		for(Territory i : getWarpgates())
		{
			i.setFaction(frs.get(fid));
			cursor.put(frs.get(fid), i);
			fid++;
		}
		
		if(fid != 3)
		{
			pl.f("Invalid warpgate count.");
			return;
		}
		
		while(hasNeutralLinks())
		{
			lock++;
			
			for(Faction i : cursor.keySet())
			{
				boolean broken = false;
				
				for(Region j : cursor.get(i).getBorders())
				{
					if(j.getType().equals(RegionType.TERRITORY))
					{
						Territory t = (Territory) j;
						
						if(t.getFaction().equals(Faction.neutral()))
						{
							t.setFaction(i);
							broken = true;
							cursor.put(i, t);
							break;
						}
					}
				}
				
				if(!broken)
				{
					for(Territory j : getTerritories())
					{
						if(j.getFaction().equals(Faction.neutral()))
						{
							for(Region k : j.getBorders())
							{
								if(k.getType().equals(RegionType.TERRITORY))
								{
									Territory l = (Territory) k;
									if(l.getFaction().equals(i))
									{
										j.setFaction(i);
										broken = true;
										cursor.put(i, j);
										break;
									}
								}
							}
							
							if(broken)
							{
								break;
							}
						}
					}
				}
			}
			
			if(lock > regions.size() * 2)
			{
				if(hasNeutralLinks())
				{
					pl.f("Failed to accent correctly. Cancelling");
					
					for(LinkedRegion i : getLinkedRegions())
					{
						i.setFaction(Faction.neutral());
					}
					
					for(Region i : regions)
					{
						i.accent(DyeColor.LIME);
					}
					
					return;
				}
			}
		}
		
		for(LinkedRegion i : getLinkedRegions())
		{
			i.accent(i.getFaction().getDyeColor());
		}
		
		for(Region i : regions)
		{
			if(i.getType().equals(RegionType.SCENERY) || i.getType().equals(RegionType.EDGE))
			{
				i.accent(Faction.neutral().getDyeColor());
			}
		}
	}
	
	public boolean hasNeutralLinks()
	{
		for(Territory i : getTerritories())
		{
			if(i.getFaction().equals(Faction.neutral()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public GList<Territory> getWarpgates()
	{
		GList<Territory> t = new GList<Territory>();
		
		for(Territory i : getTerritories())
		{
			if(i.getWarpgate())
			{
				t.add(i);
			}
		}
		
		return t;
	}
	
	public GList<Territory> getTerritories()
	{
		GList<Territory> tr = new GList<Territory>();
		
		for(Region i : regions)
		{
			if(i.getType().equals(RegionType.TERRITORY))
			{
				tr.add((Territory) i);
			}
		}
		
		return tr;
	}
	
	public GList<Village> getVillages()
	{
		GList<Village> tr = new GList<Village>();
		
		for(Region i : regions)
		{
			if(i.getType().equals(RegionType.VILLAGE))
			{
				tr.add((Village) i);
			}
		}
		
		return tr;
	}
	
	public GList<LinkedRegion> getLinkedRegions()
	{
		GList<LinkedRegion> tr = new GList<LinkedRegion>();
		
		for(Region i : regions)
		{
			if(i.getType().equals(RegionType.TERRITORY) || i.getType().equals(RegionType.VILLAGE))
			{
				tr.add((LinkedRegion) i);
			}
		}
		
		return tr;
	}
	
	public void check(Player p)
	{
		for(Region i : regions)
		{
			i.check(p);
		}
		
		int wgc = 0;
		
		for(Territory i : getTerritories())
		{
			if(i.getWarpgate())
			{
				wgc++;
			}
		}
		
		for(LinkedRegion i : getLinkedRegions())
		{
			if(i.getSpawns().isEmpty())
			{
				p.sendMessage(ChatColor.RED + "[LiquidAPI]: No Spawns at " + i.getName());
			}
		}
		
		if(wgc != 3)
		{
			if(wgc == 0)
			{
				p.sendMessage(ChatColor.RED + "[LiquidAPI]: No Gates Found. Use a diamondblock in region.");
			}
			
			else
			{
				p.sendMessage(ChatColor.RED + "[LiquidAPI]: Invalid Gate count. Must have three.");
			}
		}
		
		p.sendMessage(ChatColor.GREEN + "No errors means you're good gruh.");
	}
	
	public void safeLock(Player p)
	{
		int wgc = 0;
		
		for(Territory i : getTerritories())
		{
			if(i.getWarpgate())
			{
				wgc++;
			}
		}
		
		for(LinkedRegion i : getLinkedRegions())
		{
			if(i.getSpawns().isEmpty())
			{
				p.sendMessage(ChatColor.RED + "[LiquidAPI]: No Spawns at " + i.getName());
				return;
			}
		}
		
		if(wgc != 3)
		{
			if(wgc == 0)
			{
				p.sendMessage(ChatColor.RED + "[LiquidAPI]: No Gates Found. Use a diamondblock in region.");
			}
			
			else
			{
				p.sendMessage(ChatColor.RED + "[LiquidAPI]: Invalid Gate count. Must have three.");
			}
			
			return;
		}
		
		lock();
		p.sendMessage(ChatColor.GREEN + "Map Locked");
	}
	
	public GList<Location> getOutline(int level)
	{
		GList<Location> ls = new GList<Location>();
		
		for(Region i : regions)
		{
			ls.add(i.getOutline(level));
		}
		
		return ls;
	}
	
	public void draw(int level)
	{
		if(mdm.getDrawn())
		{
			mdm.update();
		}
		
		else
		{
			mdm.draw(level);
		}
	}
	
	public void undraw()
	{
		if(mdm.getDrawn())
		{
			mdm.undraw();
		}
	}
	
	public void build()
	{
		undraw();
		game.getMapHandler().build(this);
	}
	
	public void fastBuild()
	{
		undraw();
		game.getMapHandler().load(this);
	}
	
	public void accent(DyeColor color)
	{
		if(mdm.getDrawn())
		{
			return;
		}
		
		for(Region i : regions)
		{
			game.getMapHandler().accent(i, color);
		}
	}
	
	public boolean contains(Chunklet chunklet)
	{
		for(Region i : regions)
		{
			if(i.contains(chunklet))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean contains(Location location)
	{
		return contains(new Chunklet(location));
	}
	
	public boolean contains(Block block)
	{
		return contains(block.getLocation());
	}
	
	public boolean contains(LivingEntity e)
	{
		return contains(e.getLocation());
	}
	
	public boolean onAddChunklet(Chunklet chunklet)
	{
		for(Region i : regions)
		{
			if(i.contains(chunklet))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public GList<Player> getPlayers()
	{
		GList<Player> players = new GList<Player>();
		
		for(Player i : game.players())
		{
			if(contains(i))
			{
				players.add(i);
			}
		}
		
		return players;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public GList<Region> getRegions()
	{
		return regions;
	}
	
	public void setRegions(GList<Region> regions)
	{
		this.regions = regions;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public void setGame(Game game)
	{
		this.game = game;
		
		for(Region i : regions)
		{
			i.setGame(game);
		}
	}
	
	public MapDrawManager getMdm()
	{
		return mdm;
	}
	
	public void setMdm(MapDrawManager mdm)
	{
		this.mdm = mdm;
	}
	
	public void modify()
	{
		modified = true;
	}
	
	public void unmodify()
	{
		modified = false;
	}
	
	public boolean onMod(Block block)
	{
		if(contains(block))
		{
			if(locked())
			{
				return true;
			}
			
			else
			{
				modify();
			}
		}
		
		return false;
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
	
	@EventHandler
	public void onModification(BlockBreakEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
		
		if(onMod(e.getBlock()))
		{
			if(game.getType().equals(GameType.REGIONED))
			{
				((RegionedGame) game).blockHit(e.getBlock());
			}
		}
	}
	
	@EventHandler
	public void onModification(BlockPlaceEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	@EventHandler
	public void onModification(BlockExplodeEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	@EventHandler
	public void onModification(BlockFadeEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	@EventHandler
	public void onModification(BlockFormEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	@EventHandler
	public void onModification(BlockBurnEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	@EventHandler
	public void onModification(BlockGrowEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	@EventHandler
	public void onModification(BlockFromToEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	@EventHandler
	public void onModification(BlockIgniteEvent e)
	{
		e.setCancelled(onMod(e.getBlock()));
	}
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}
	
	public boolean isModified()
	{
		return modified;
	}
	
	public void setModified(boolean modified)
	{
		this.modified = modified;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}
}
