package com.glacialrush.api.map;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;
import com.glacialrush.api.game.object.Job;
import com.glacialrush.api.object.GBiset;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;

public class MapDrawManager
{
	private Map map;
	private GMap<Location, GBiset<Material, Byte>> origin;
	private Boolean drawn;
	private Integer level;
	
	public MapDrawManager(Map map)
	{
		this.map = map;
		this.origin = new GMap<Location, GBiset<Material, Byte>>();
		this.drawn = false;
		this.level = -1;
	}
	
	public void pdraw()
	{
		if(drawn)
		{
			return;
		}
		
		if(level < 0)
		{
			return;
		}
		
		draw(level);
	}
	
	public void pundraw()
	{
		if(!drawn)
		{
			return;
		}
		
		int level = this.level;
		
		undraw();
		
		this.level = level;
	}
	
	@SuppressWarnings("deprecation")
	public void draw(int level)
	{
		if(drawn)
		{
			return;
		}
		
		this.level = level;
		Job j = new Job("Map " + map.getName() + " Draw Task", map.getGame());
		
		for(Region i : map.getRegions())
		{
			for(Location l : i.getOutline(level))
			{
				origin.put(l, new GBiset<Material, Byte>(l.getBlock().getType(), l.getBlock().getData()));
				j.queue(l, i.getType().material());
			}
		}
		
		j.flush(new Runnable()
		{
			@Override
			public void run()
			{
				drawn = true;
			}
		});
	}
	
	public void undraw()
	{
		if(!drawn)
		{
			return;
		}
		
		Job j = new Job("Map " + map.getName() + " Un-Draw Task", map.getGame());
		
		for(Location i : origin.keySet())
		{
			j.queue(i, origin.get(i).getA(), origin.get(i).getB());
		}
		
		origin.clear();
		level = -1;
		j.flush(new Runnable()
		{
			@Override
			public void run()
			{
				drawn = false;
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	public void update()
	{
		if(!drawn)
		{
			return;
		}
		
		Job j = new Job("Map " + map.getName() + " Up-Draw Task", map.getGame());
		
		for(Region i : map.getRegions())
		{
			GList<Location> outline = i.getOutline(level);
			
			for(Location l : outline)
			{
				if(!origin.containsKey(l))
				{
					origin.put(l, new GBiset<Material, Byte>(l.getBlock().getType(), l.getBlock().getData()));
					j.queue(l, i.getType().material());
				}
			}
		}
		
		GList<Location> outline = map.getOutline(level);
		Iterator<Location> it = origin.keySet().iterator();
		
		while(it.hasNext())
		{
			Location l = it.next();
			
			if(!outline.contains(l))
			{
				j.queue(l, origin.get(l).getA(), origin.get(l).getB());
				origin.remove(l);
			}
		}
		
		j.flush();
	}

	public Map getMap()
	{
		return map;
	}

	public void setMap(Map map)
	{
		this.map = map;
	}

	public GMap<Location, GBiset<Material, Byte>> getOrigin()
	{
		return origin;
	}

	public void setOrigin(GMap<Location, GBiset<Material, Byte>> origin)
	{
		this.origin = origin;
	}

	public Boolean getDrawn()
	{
		return drawn;
	}

	public void setDrawn(Boolean drawn)
	{
		this.drawn = drawn;
	}

	public Integer getLevel()
	{
		return level;
	}

	public void setLevel(Integer level)
	{
		this.level = level;
	}
}
