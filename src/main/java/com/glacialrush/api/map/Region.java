package com.glacialrush.api.map;

import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.thread.GlacialThread;
import org.bukkit.ChatColor;

public class Region implements Buildable
{
	protected final GlacialPlugin pl;
	protected final RegionType type;
	protected Game game;
	protected GList<Chunklet> chunklets;
	protected String name;
	protected Map map;
	protected GList<Location> accents;
	protected GList<Region> borders;
	
	public Region(Map map, String name, RegionType type, Game game)
	{
		this.pl = GlacialPlugin.instance();
		this.map = map;
		this.type = type;
		this.name = name;
		this.chunklets = new GList<Chunklet>();
		this.game = game;
		this.accents = new GList<Location>();
		this.borders = new GList<Region>();
	}
	
	public boolean contains(Chunk chunk)
	{
		int cx = 0;
		
		for(Chunklet i : chunklets)
		{
			if(cx == 0)
			{
				if(i.contains(chunk))
				{
					return true;
				}
				
				cx = 3;
			}
			
			else
			{
				cx--;
			}
		}
		
		return false;
	}
	
	public void addChunklets(GList<Chunklet> chunklets)
	{
		boolean c = false;
		
		for(Chunklet i : chunklets)
		{
			if(connected(i))
			{
				c = true;
			}
		}
		
		if(!c && !this.chunklets.isEmpty())
		{
			return;
		}
		
		for(Chunklet i : chunklets)
		{
			addChunklet(i);
		}
	}
	
	public void delChunklets(GList<Chunklet> chunklets)
	{
		if(this.chunklets.isEmpty())
		{
			return;
		}
		
		for(Chunklet i : chunklets)
		{
			if(contains(i))
			{
				delChunklet(i);
			}
		}
	}
	
	public void fillCheck(final Player p)
	{
		final GList<Chunklet> chunklets = new GList<Chunklet>();
		Chunklet start = this.chunklets.pickRandom();
		chunklets.add(start);
		
		game.getThreadHandler().newThread(new GlacialThread("Fill Check")
		{
			public void run()
			{
				if(chunklets.size() != Region.this.chunklets.size())
				{
					boolean acted = false;
					
					for(Chunklet i : chunklets.copy())
					{
						for(Direction d : Direction.news())
						{
							if(contains(i.getRelative(d)) && !chunklets.contains(i.getRelative(d)))
							{
								acted = true;
								chunklets.add(i.getRelative(d));
							}
						}
					}
					
					if(!acted)
					{
						p.sendMessage(ChatColor.RED + "[LiquidAPI]: " + ChatColor.WHITE + "Build FAILED for " + name);
						p.sendMessage(ChatColor.RED + "[LiquidAPI]: " + ChatColor.WHITE + "- Ensure that this region is only one piece, not cut in half.");
						stop();
						return;
					}
				}
				
				else
				{
					stop();
					return;
				}
			}
		});
	}
	
	public void addChunklet(Chunklet chunklet)
	{
		for(Region i : map.getRegions())
		{
			if(i.chunklets.contains(chunklet))
			{
				return;
			}
		}
		
		chunklets.add(chunklet);
	}
	
	public void delChunklet(Chunklet chunklet)
	{
		if(contains(chunklet))
		{
			chunklets.remove(chunklet);
		}
	}
	
	public boolean connected(Chunklet chunklet)
	{
		for(Chunklet i : chunklet.getNeighbors())
		{
			if(contains(i))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean connected(GList<Chunklet> chunklets)
	{
		for(Chunklet i : chunklets)
		{
			if(contains(i))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean contains(Chunklet chunklet)
	{
		for(Chunklet i : chunklets)
		{
			if(i.getX() == chunklet.getX() && i.getZ() == chunklet.getZ() && i.getWorld().equals(chunklet.getWorld()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void build(Block block)
	{
		if(block.getType().equals(Material.WOOL) || block.getType().equals(Material.STAINED_GLASS) || block.getType().equals(Material.STAINED_GLASS_PANE) || block.getType().equals(Material.STAINED_CLAY) || block.getType().equals(Material.BANNER) || block.getType().equals(Material.STANDING_BANNER) || block.getType().equals(Material.WALL_BANNER) || block.getType().equals(Material.CARPET))
		{
			if(block.getType().equals(Material.STAINED_CLAY))
			{
				if(!map.getMapConfiguration().isUseStainedClay())
				{
					return;
				}
			}
			
			accents.add(block.getLocation());
		}
	}
	
	@Override
	public void preBuild()
	{
		accents.clear();
		borders.clear();
	}
	
	@Override
	public void postBuild()
	{
		for(Chunklet i : chunklets)
		{
			for(Direction j : Direction.news())
			{
				if(!contains(i.getRelative(j)))
				{
					for(Region r : map.getRegions())
					{
						if(r.equals(this))
						{
							continue;
						}
						
						if(r.contains(i.getRelative(j)))
						{
							if(!borders.contains(r))
							{
								borders.add(r);
							}
						}
					}
				}
			}
		}
		
	}
	
	public GList<Region> getBorders()
	{
		return borders;
	}
	
	public void setBorders(GList<Region> borders)
	{
		this.borders = borders;
	}
	
	public void check(Player p)
	{
		fillCheck(p);
	}
	
	public boolean contains(Location location)
	{
		return contains(new Chunklet(location));
	}
	
	public boolean contains(LivingEntity e)
	{
		return contains(e.getLocation());
	}
	
	public GList<Location> getOutline(int level)
	{
		GList<Location> ls = new GList<Location>();
		
		for(Chunklet i : chunklets)
		{
			for(Direction j : Direction.news())
			{
				if(!contains(i.getRelative(j)))
				{
					Direction dd = j;
					
					if(dd.equals(Direction.N))
					{
						dd = Direction.E;
					}
					
					else if(dd.equals(Direction.W))
					{
						dd = Direction.N;
					}
					
					else if(dd.equals(Direction.S))
					{
						dd = Direction.W;
					}
					
					else
					{
						dd = Direction.S;
					}
					
					ls.add(i.getBorder(level, dd));
				}
			}
		}
		
		return ls;
	}
	
	public GList<Player> getPlayers()
	{
		GList<Player> players = new GList<Player>();
		
		for(Player i : game.players())
		{
			if(!i.getGameMode().equals(GameMode.ADVENTURE))
			{
				continue;
			}
			
			if(contains(i))
			{
				players.add(i);
			}
		}
		
		return players;
	}
	
	public void accent(DyeColor color)
	{
		game.getMapHandler().accent(this, color);
	}
	
	public GList<Chunklet> getChunklets()
	{
		return chunklets;
	}
	
	public void setChunklets(GList<Chunklet> chunklets)
	{
		this.chunklets = chunklets;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Map getMap()
	{
		return map;
	}
	
	public void setMap(Map map)
	{
		this.map = map;
	}
	
	public RegionType getType()
	{
		return type;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public void setGame(Game game)
	{
		this.game = game;
	}
	
	public GList<Location> getAccents()
	{
		return accents;
	}
	
	public void setAccents(GList<Location> accents)
	{
		this.accents = accents;
	}
}
