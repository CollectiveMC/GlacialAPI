package com.glacialrush.api.game.object;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.xapi.Area;

public class Electricity
{
	private Location start;
	private Integer maxPoints;
	private Double maxDistance;
	private Faction faction;
	private Random random;
	private RegionedGame game;
	
	public Electricity(RegionedGame game, Location start, Integer maxPoints, Double maxDistance, Faction faction)
	{
		this.game = game;
		this.start = start;
		this.maxPoints = maxPoints;
		this.maxDistance = maxDistance;
		this.faction = faction;
		this.random = new Random();
	}
	
	public GList<LivingEntity> shock()
	{
		Location s = start.clone();
		GList<LivingEntity> shocked = new GList<LivingEntity>();
		
		for(int i = 0; i < maxPoints; i++)
		{
			LivingEntity e = shock(s, shocked);
			
			if(e == null)
			{
				break;
			}
			
			else
			{
				shocked.add(e);
				s = e.getLocation();
				System.out.println("Shocked " + e.getEntityId());
			}
		}
		
		return shocked;
	}
	
	private LivingEntity shock(Location l, GList<LivingEntity> shocked)
	{
		LivingEntity m = null;
		Area a = new Area(l, maxDistance);
		Double d = Double.MAX_VALUE;
		Player p = null;
		IronGolem g = null;
		
		for(Player i : a.getNearbyPlayers())
		{
			Faction f = game.getFactionHandler().getFaction(i);
			
			if(!this.faction.equals(f) && !shocked.contains((LivingEntity) i))
			{
				Double dx = i.getLocation().distance(l);
				
				if(dx < d)
				{
					d = dx;
					p = i;
				}
			}
		}
		
		if(p != null)
		{
			los(p.getLocation(), l);
			m = p;
		}
		
		else
		{
			d = Double.MAX_VALUE;
			
			for(Entity i : a.getNearbyEntities())
			{
				if(i.getType().equals(EntityType.IRON_GOLEM))
				{
					IronGolem gg = (IronGolem) i;
					
					if(game.getPaladinHandler().getPaladins().containsKey(gg))
					{
						Faction f = game.getPaladinHandler().getPaladins().get(gg);
						
						if(!this.faction.equals(f) && !shocked.contains((LivingEntity) i))
						{
							Double dx = i.getLocation().distance(l);
							
							if(dx < d)
							{
								d = dx;
								g = gg;
							}
						}
					}
				}
			}
			
			if(g != null)
			{
				los(g.getLocation(), l);
				m = g;
			}
		}
		
		return m;
	}
	
	public void los(Location a, Location b)
	{
		Vector v = a.clone().subtract(b).toVector().clone().normalize();
		Location c = a.clone();
		ParticleEffect e = ParticleEffect.CRIT_MAGIC;
		Vector vm = new Vector((random.nextDouble() - 0.5) / 3, (random.nextDouble() - 0.5) / 3, (random.nextDouble() - 0.5) / 3);
		v.add(vm);
		
		for(int i = 0; i < a.distance(b); i++)
		{
			e.display(0f, 0f, 0f, 0.1f, 6, c, 48);
			c.subtract(v);
		}
	}
	
	public Location getStart()
	{
		return start;
	}
	
	public void setStart(Location start)
	{
		this.start = start;
	}
	
	public Integer getMaxPoints()
	{
		return maxPoints;
	}
	
	public void setMaxPoints(Integer maxPoints)
	{
		this.maxPoints = maxPoints;
	}
	
	public Double getMaxDistance()
	{
		return maxDistance;
	}
	
	public void setMaxDistance(Double maxDistance)
	{
		this.maxDistance = maxDistance;
	}
	
	public Faction getFaction()
	{
		return faction;
	}
	
	public void setFaction(Faction faction)
	{
		this.faction = faction;
	}
}
