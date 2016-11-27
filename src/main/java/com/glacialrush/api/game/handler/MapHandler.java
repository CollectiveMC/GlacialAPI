package com.glacialrush.api.game.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.data.BuildCache;
import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.Generator;
import com.glacialrush.api.game.object.Job;
import com.glacialrush.api.game.object.Squad;
import com.glacialrush.api.map.Chunklet;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.map.region.Edge;
import com.glacialrush.api.map.region.LinkedRegion;
import com.glacialrush.api.map.region.Scenery;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.map.region.Village;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GLocation;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.thread.GlacialThread;
import com.glacialrush.xapi.FastMath;
import org.bukkit.ChatColor;

public class MapHandler extends GlacialHandler
{
	private GMap<Map, Integer> building;
	private GMap<Region, Integer> accenting;
	private File buildCache;
	
	public MapHandler(Game game)
	{
		super(game);
		
		building = new GMap<Map, Integer>();
		accenting = new GMap<Region, Integer>();
		buildCache = new File(game.pl().getDataFolder(), "cache");
		
		if(!buildCache.exists())
		{
			buildCache.mkdirs();
		}
	}
	
	public boolean hasCache(Map map)
	{
		File f = new File(buildCache, map.getName().toLowerCase().replace(' ', '-') + ".mcache");
		
		return f.exists();
	}
	
	public void cache(Map map)
	{
		BuildCache bc = new BuildCache(map.getName());
		GList<GLocation> captures = new GList<GLocation>();
		GList<GLocation> accents = new GList<GLocation>();
		GList<GLocation> spawns = new GList<GLocation>();
		GList<GLocation> warpgates = new GList<GLocation>();
		File f = new File(buildCache, map.getName().toLowerCase().replace(' ', '-') + ".mcache");
		
		for(Region i : map.getRegions())
		{
			w("Cache: " + i.getName());
			
			for(Location j : i.getAccents())
			{
				accents.add(new GLocation(j));
			}
			
			s("  Cache: " + i.getAccents().size() + " Accents @ " + i.getName());
			
			if(i.getType().equals(RegionType.TERRITORY) || i.getType().equals(RegionType.VILLAGE))
			{
				LinkedRegion t = (LinkedRegion) i;
				
				for(Location j : t.getSpawns())
				{
					spawns.add(new GLocation(j));
					s("  Cache: Spawn @ " + j.toString());
				}
			}
			
			if(i.getType().equals(RegionType.TERRITORY))
			{
				Territory t = (Territory) i;
				
				for(Capture j : t.getCaptures())
				{
					captures.add(new GLocation(j.getLocation()));
					s("  Cache: Capture @ " + j.getLocation().toString());
				}
				
				if(t.getWarpgate())
				{
					warpgates.add(new GLocation(t.getGenerator().getLocation()));
					s("  Cache: WARPGATE @ " + t.getGenerator().getLocation());
				}
			}
		}
		
		bc.setAccents(accents);
		bc.setCaptures(captures);
		bc.setSpawns(spawns);
		bc.setWarpgates(warpgates);
		
		s("---- Cache Results for " + map.getName() + " ----");
		s("  Accents: " + accents.size());
		s("   Spawns: " + spawns.size());
		s("Warpgates: " + warpgates.size());
		s(" Captures: " + captures.size());
		
		try
		{
			FileOutputStream fos = new FileOutputStream(f);
			GZIPOutputStream gzo = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gzo);
			
			oos.writeObject(bc);
			oos.close();
		}
		
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void load(final Map map)
	{
		if(hasCache(map))
		{
			File f = new File(buildCache, map.getName().toLowerCase().replace(' ', '-') + ".mcache");
			
			try
			{
				FileInputStream fin = new FileInputStream(f);
				GZIPInputStream gzi = new GZIPInputStream(fin);
				ObjectInputStream ois = new ObjectInputStream(gzi);
				
				try
				{
					Object o = ois.readObject();
					ois.close();
					
					if(o != null)
					{
						BuildCache bc = (BuildCache) o;
						
						for(Region i : map.getRegions())
						{
							if(i.getType().equals(RegionType.TERRITORY))
							{
								((Territory) i).preBuild();
							}
							
							else if(i.getType().equals(RegionType.VILLAGE))
							{
								((Village) i).preBuild();
							}
							
							else if(i.getType().equals(RegionType.SCENERY))
							{
								((Scenery) i).preBuild();
							}
							
							else if(i.getType().equals(RegionType.EDGE))
							{
								((Edge) i).preBuild();
							}
						}
						
						for(Region i : map.getRegions())
						{
							int ac = 0;
							int sc = 0;
							int cc = 0;
							
							for(GLocation j : bc.getAccents())
							{
								if(i.contains(j.toLocation()))
								{
									ac++;
									i.getAccents().add(j.toLocation());
								}
							}
							
							s("FASTBUILD: Added " + ac + " accents to " + i.getName());
							
							if(i.getType().equals(RegionType.TERRITORY) || i.getType().equals(RegionType.VILLAGE))
							{
								LinkedRegion l = (LinkedRegion) i;
								
								for(GLocation j : bc.getSpawns())
								{
									if(l.contains(j.toLocation()))
									{
										sc++;
										l.getSpawns().add(j.toLocation());
									}
								}
							}
							
							s("FASTBUILD: Added " + sc + " spawns to " + i.getName());
							
							if(i.getType().equals(RegionType.TERRITORY))
							{
								Territory t = (Territory) i;
								
								for(GLocation j : bc.getCaptures())
								{
									if(t.contains(j.toLocation()))
									{
										cc++;
										t.getCaptures().add(new Capture(t, j.toLocation()));
									}
								}
								
								for(GLocation j : bc.getWarpgates())
								{
									if(t.contains(j.toLocation()))
									{
										t.setWarpgate(true);
										t.setGenerator(new Generator(j.toLocation(), t));
										s("-- Warpgate --");
									}
								}
							}
							
							s("FASTBUILD: Added " + cc + " captures to " + i.getName());
						}
						
						for(Region i : map.getRegions())
						{
							if(i.getType().equals(RegionType.TERRITORY))
							{
								((Territory) i).postBuild();
							}
							
							else if(i.getType().equals(RegionType.VILLAGE))
							{
								((Village) i).postBuild();
							}
							
							else if(i.getType().equals(RegionType.SCENERY))
							{
								((Scenery) i).postBuild();
							}
							
							else if(i.getType().equals(RegionType.EDGE))
							{
								((Edge) i).postBuild();
							}
						}
					}
				}
				
				catch(ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		else
		{
			fastBuild(map);
		}
	}
	
	public void fastBuild(final Map map)
	{
		if(building(map))
		{
			return;
		}
		
		if(map.getMdm().getDrawn())
		{
			return;
		}
		
		building.put(map, 0);
		o("Building " + map.getName());
		final int[] prog = new int[] {0, 0};
		
		for(Region i : map.getRegions())
		{
			prog[0] += i.getChunklets().size();
			
			if(i.getType().equals(RegionType.TERRITORY))
			{
				((Territory) i).preBuild();
			}
			
			else if(i.getType().equals(RegionType.VILLAGE))
			{
				((Village) i).preBuild();
			}
			
			else if(i.getType().equals(RegionType.SCENERY))
			{
				((Scenery) i).preBuild();
			}
			
			else if(i.getType().equals(RegionType.EDGE))
			{
				((Edge) i).preBuild();
			}
		}
		
		for(final Region i : map.getRegions())
		{
			final Iterator<Chunklet> it = i.getChunklets().iterator();
			
			boolean rn = true;
			
			while(rn)
			{
				if(it.hasNext())
				{
					Chunklet c = it.next();
					prog[1]++;
					int pr = (int) (100 * (double) ((double) prog[1] / (double) prog[0]));
					building.put(map, pr);
					
					Iterator<Block> itb = c.iterator();
					
					while(itb.hasNext())
					{
						if(i.getType().equals(RegionType.TERRITORY))
						{
							((Territory) i).build(itb.next());
						}
						
						else if(i.getType().equals(RegionType.VILLAGE))
						{
							((Village) i).build(itb.next());
						}
						
						else if(i.getType().equals(RegionType.SCENERY))
						{
							((Scenery) i).build(itb.next());
						}
						
						else if(i.getType().equals(RegionType.EDGE))
						{
							((Edge) i).build(itb.next());
						}
					}
				}
				
				else
				{
					rn = false;
					
					if(i.getType().equals(RegionType.TERRITORY))
					{
						((Territory) i).postBuild();
					}
					
					else if(i.getType().equals(RegionType.VILLAGE))
					{
						((Village) i).postBuild();
					}
					
					else if(i.getType().equals(RegionType.SCENERY))
					{
						((Scenery) i).postBuild();
					}
					
					else if(i.getType().equals(RegionType.EDGE))
					{
						((Edge) i).postBuild();
					}
				}
			}
			
			
			s("Built Region " + i.getName());
		}
		
		building.remove(map);
		
		if(!hasCache(map))
		{
			s("Caching");
			cache(map);
		}
	}
	
	public Location getGenerator(Faction faction)
	{
		for(Territory i : ((RegionedGame) game).getMap().getWarpgates())
		{
			if(i.getFaction().equals(faction))
			{
				Generator w = i.getGenerator();
				
				return w.getLocation();
			}
		}
		
		return null;
	}
	
	public boolean factionStable(Faction faction)
	{
		for(Territory i : ((RegionedGame) game).getMap().getWarpgates())
		{
			if(i.getFaction().equals(faction))
			{
				Generator w = i.getGenerator();
				
				return w == null ? true : w.isStable();
			}
		}
		
		return true;
	}
	
	public Location nearestObject(Player p, Region r)
	{
		if(r.getType().equals(RegionType.TERRITORY))
		{
			Territory t = (Territory) r;
			
			double dist = Double.MAX_VALUE;
			Location tg = null;
			
			for(Capture i : t.getCaptures())
			{
				double d = FastMath.distance2D(i.getLocation(), p.getLocation());
				
				if(d < dist)
				{
					dist = d;
					tg = i.getLocation();
				}
			}
			
			if(tg == null)
			{
				tg = t.getSpawns().pickRandom();
			}
			
			return tg;
		}
		
		return r.getChunklets().get(0).getMin();
	}
	
	public void updateObjectives()
	{
		for(Player i : game.players())
		{
			updateObjective(i);
		}
	}
	
	public void updateObjective(Player p)
	{
		if(!game.getType().equals(GameType.REGIONED))
		{
			return;
		}
		
		game.getGameController().gpo(p).updateArmor();
		RegionedGame rg = (RegionedGame) game;
		Faction f = rg.getFactionHandler().getFaction(p);
		Region r = rg.getMap().getCloseLinkedRegion(p);
		Boolean stable = factionStable(f);
		
		String m = "Objective";
		Location o = null;
		Squad s = rg.getSquadHandler().getSquad(p);
		
		if(rg.getSquadHandler().inSquad(p) && s.getBeacon() != null)
		{
			o = s.getBeacon().getMax().clone();
			o.setY(p.getLocation().getY());
			m = s.getColor() + s.getGreek().symbol() + " > " + s.getObjective();
		}
		
		else if(!stable)
		{
			o = getGenerator(f);
			m = ChatColor.RED + "Defend Your Fortress!";
		}
		
		else if(r != null)
		{
			if(r.getType().equals(RegionType.TERRITORY))
			{
				Territory t = (Territory) r;
				Faction df = t.getFaction();
				
				if(!t.getWarpgate())
				{
					if(f.equals(df))
					{
						for(Capture i : t.getCaptures())
						{
							if(!i.getFaction().equals(f))
							{
								o = i.getLocation();
								m = i.getFaction().getColor() + "Capture " + i.getTerritory().getName();
								break;
							}
							
							else if(i.getProgress() < 100)
							{
								o = i.getLocation();
								m = i.getFaction().getColor() + "Defend " + i.getTerritory().getName() + ChatColor.BLUE + "" + (((int)(100.0 * (double)((double)(i.getProgress()) / (double)(100))))) + "%";
								break;
							}
						}
					}
					
					else if(t.canCapture(f))
					{
						for(Capture i : t.getCaptures())
						{
							if(!i.getFaction().equals(f))
							{
								o = i.getLocation();
								m = i.getFaction().getColor() + "Capture " + i.getTerritory().getName();
								break;
							}
							
							else if(i.getProgress() < 100)
							{
								o = i.getLocation();
								m = i.getFaction().getColor() + "Defend " + i.getTerritory().getName() + ChatColor.BLUE + "" + (((int)(100.0 * (double)((double)(i.getProgress()) / (double)(100))))) + "%";
								break;
							}
						}
					}
				}
			}
		}
		
		if(o == null)
		{
			for(LinkedRegion i : rg.getMap().getLinkedRegions())
			{
				if(i.getType().equals(RegionType.TERRITORY))
				{
					Territory t = (Territory) i;
					
					if(i.getFaction().equals(f))
					{
						if(rg.getGameStateHandler().getMapTimer().hasTimer(t))
						{
							boolean fo = false;
							
							for(Capture j : t.getCaptures())
							{
								if(!j.getFaction().equals(f))
								{
									o = j.getLocation();
									m = j.getFaction().getColor() + "Capture " + j.getTerritory().getName();
									fo = true;
									break;
								}
								
								else if(j.getProgress() < 100)
								{
									o = j.getLocation();
									m = j.getFaction().getColor() + "Defend " + j.getTerritory().getName() + ChatColor.BLUE + "" + (((int)(100.0 * (double)((double)(j.getProgress()) / (double)(100))))) + "%";
									fo = true;
									break;
								}
							}
							
							if(fo)
							{
								break;
							}
						}
					}
					
					else
					{
						if(t.canCapture(f))
						{
							boolean fo = false;
							
							for(Capture j : t.getCaptures())
							{
								if(!j.getFaction().equals(f))
								{
									o = j.getLocation();
									m = j.getFaction().getColor() + "Capture " + j.getTerritory().getName();
									fo = true;
									break;
								}
								
								else if(j.getProgress() < 100)
								{
									o = j.getLocation();
									m = j.getFaction().getColor() + "Defend " + j.getTerritory().getName() + ChatColor.BLUE + "" + (((int)(100.0 * (double)((double)(j.getProgress()) / (double)(100))))) + "%";
									fo = true;
									break;
								}
							}
							
							if(fo)
							{
								break;
							}
						}
					}
				}
			}
		}
		
		if(o == null)
		{
			for(IronGolem i : ((RegionedGame)game).getPaladinHandler().getPaladins().keySet())
			{
				if(!((RegionedGame)game).getPaladinHandler().getPaladins().get(i).equals(f))
				{
					o = i.getLocation();
					m = ChatColor.AQUA + "Defeat the Paladins";
					break;
				}
			}
		}
		
		if(o == null)
		{
			o = p.getLocation();
			m = ChatColor.GREEN + "No Objective";
		}
		
		m = m + ChatColor.AQUA + " " + ((int) p.getLocation().distance(o)) + "m";
		game.getGameController().gpo(p).updateCompass(o, m);
	}
	
	public void build(final Map map)
	{
		if(building(map))
		{
			return;
		}
		
		if(map.getMdm().getDrawn())
		{
			return;
		}
		
		building.put(map, 0);
		o("Building " + map.getName());
		final int[] prog = new int[] {0, 0};
		
		for(Region i : map.getRegions())
		{
			prog[0] += i.getChunklets().size();
			
			if(i.getType().equals(RegionType.TERRITORY))
			{
				((Territory) i).preBuild();
			}
			
			else if(i.getType().equals(RegionType.VILLAGE))
			{
				((Village) i).preBuild();
			}
			
			else if(i.getType().equals(RegionType.SCENERY))
			{
				((Scenery) i).preBuild();
			}
			
			else if(i.getType().equals(RegionType.EDGE))
			{
				((Edge) i).preBuild();
			}
		}
		
		for(final Region i : map.getRegions())
		{
			final Iterator<Chunklet> it = i.getChunklets().iterator();
			
			game.getThreadHandler().newThread(new GlacialThread("Region Build: " + i.getName())
			{
				public void run()
				{
					for(int j = 0; j < 32; j++)
					{
						if(it.hasNext())
						{
							Chunklet c = it.next();
							prog[1]++;
							int pr = (int) (100 * (double) ((double) prog[1] / (double) prog[0]));
							building.put(map, pr);
							
							Iterator<Block> itb = c.iterator();
							
							while(itb.hasNext())
							{
								if(i.getType().equals(RegionType.TERRITORY))
								{
									((Territory) i).build(itb.next());
								}
								
								else if(i.getType().equals(RegionType.VILLAGE))
								{
									((Village) i).build(itb.next());
								}
								
								else if(i.getType().equals(RegionType.SCENERY))
								{
									((Scenery) i).build(itb.next());
								}
								
								else if(i.getType().equals(RegionType.EDGE))
								{
									((Edge) i).build(itb.next());
								}
							}
						}
						
						else
						{
							stop();
							
							if(i.getType().equals(RegionType.TERRITORY))
							{
								((Territory) i).postBuild();
							}
							
							else if(i.getType().equals(RegionType.VILLAGE))
							{
								((Village) i).postBuild();
							}
							
							else if(i.getType().equals(RegionType.SCENERY))
							{
								((Scenery) i).postBuild();
							}
							
							else if(i.getType().equals(RegionType.EDGE))
							{
								((Edge) i).postBuild();
							}
							
							building.remove(map);
							s("Finished Build");
							return;
						}
					}
				}
			});
		}
	}
	
	public void accent(final Region region, final DyeColor color)
	{
		if(accenting(region))
		{
			return;
		}
		
		if(region.getMap().getMdm().getDrawn())
		{
			return;
		}
		
		accenting.put(region, 0);
		final Iterator<Location> it = region.getAccents().iterator();
		final Job job = new Job("Region Accent", getGame());
		final int[] prog = new int[] {0, 0};
		
		for(Region i : accenting.keySet())
		{
			prog[0] += i.getAccents().size();
		}
		
		game.getThreadHandler().newThread(new GlacialThread("Region Build: " + region.getName())
		{
			public void run()
			{
				for(int j = 0; j < 256; j++)
				{
					if(it.hasNext())
					{
						job.queue(it.next(), color);
						prog[1]++;
						int pr = (int) (100 * (double) ((double) prog[1] / (double) prog[0]));
						accenting.put(region, pr);
					}
					
					else
					{
						stop();
						accenting.remove(region);
						job.flush();
						return;
					}
				}
			}
		});
	}
	
	public boolean building(Map map)
	{
		return building.containsKey(map);
	}
	
	public boolean accenting(Region region)
	{
		return accenting.containsKey(region);
	}
	
	public boolean building()
	{
		return !building.isEmpty();
	}
	
	public boolean accenting()
	{
		return !accenting.isEmpty();
	}
	
	public int buildProgress()
	{
		int prog = 0;
		
		for(Map i : building.keySet())
		{
			prog += building.get(i);
		}
		
		return prog / building.keySet().size();
	}
	
	public int accentProgress()
	{
		int prog = 0;
		
		for(Region i : accenting.keySet())
		{
			prog += accenting.get(i);
		}
		
		return prog / accenting.keySet().size();
	}
}
