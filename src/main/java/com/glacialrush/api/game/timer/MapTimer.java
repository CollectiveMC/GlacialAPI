package com.glacialrush.api.game.timer;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.Squad;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.map.region.LinkedRegion;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.map.region.Village;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.vfx.GVFX;
import com.glacialrush.xapi.Duration;

public class MapTimer extends GameTimer
{
	private GMap<Territory, Integer> timers;
	private GMap<Faction, Boolean> alive;
	private int warpGateCheck;
	private boolean won;
	
	public MapTimer(RegionedGame g)
	{
		super(g, 20, "MapTimer");
		
		timers = new GMap<Territory, Integer>();
		warpGateCheck = 10;
		alive = new GMap<Faction, Boolean>();
		won = false;
		
		for(Faction i : Faction.all())
		{
			alive.put(i, true);
		}
	}
	
	public boolean alive(Faction f)
	{
		return alive.get(f);
	}
	
	public void tick()
	{
		super.tick();
		
		if(won)
		{
			return;
		}
		
		if(!g.getGameController().getGames().contains(g))
		{
			return;
		}
		
		if(g.getMap().getWarpgates().size() != 3)
		{
			g.getGameController().w("Invalid Warpgate Count. Only " + g.getMap().getWarpgates().size());
		}
		
		for(Player i : g.players())
		{
			Statistic.ACTIVE_HOURS.add(i, 0.00027777777);
		}
		
		warpGateCheck--;
		
		if(warpGateCheck <= 0)
		{
			for(Territory i : g.getMap().getWarpgates())
			{
				i.getGenerator().update(g);
			}
			
			warpGateCheck = 4;
		}
		
		for(LinkedRegion i : g.getMap().getLinkedRegions())
		{
			if(i.getType().equals(RegionType.VILLAGE))
			{
				Village v = (Village) i;
				Faction s = v.getFaction();
				boolean cap = true;
				Faction tv = null;
				
				for(Region j : v.getBorders())
				{
					if(j.getType().equals(RegionType.TERRITORY))
					{
						Territory t = (Territory) j;
						
						if(tv == null)
						{
							tv = t.getFaction();
						}
						
						else
						{
							if(!t.getFaction().equals(tv))
							{
								cap = false;
								break;
							}
						}
					}
				}
				
				if(!s.equals(tv))
				{
					if(cap && tv != null)
					{
						v.setFaction(tv);
						v.accent(tv.getDyeColor());
						
						v.pullInfluenceMap();
						
						Player hi = null;
						double ifl = 0.0;
						
						for(Player j : v.getInfluenceMap().keySet())
						{
							if(v.getInfluence(j) > ifl)
							{
								ifl = v.getInfluence(j);
								hi = j;
							}
						}
						
						for(Player j : v.getInfluenceMap().keySet())
						{
							long influence = (long) (v.getInfluence(j) * 100);
							
							if(g.getFactionHandler().getFaction(j).equals(tv))
							{
								Statistic.TERRITORY_CAPTURED.add(j);
								g.getNotificationHandler().queue(j, NotificationPreset.VILLAGE_CAPTURED.format(new Object[] {tv.getColor() + ""}, new Object[] {tv.getColor() + tv.getName() + " <> " + i.getName()}, new Object[] {tv.getColor() + "", hi.getName() + " <> " + (100 * ifl) + "%"}));
								((RegionedGame) g).getExperienceHandler().giveXp(j, (long) 128, Experience.CAPTURE_VILLAGE);
								
								for(Location k : v.getSpawns())
								{
									GVFX.firework(k, v.getFaction());
								}
								
								GlacialPlugin.instance().scheduleSyncTask(1, new Runnable()
								{
									@Override
									public void run()
									{
										for(Location k : i.getSpawns())
										{
											GVFX.firework(k, i.getFaction());
										}
									}
								});
							}
							
							else
							{
								Statistic.TERRITORY_LOST.add(j);
								g.getNotificationHandler().queue(j, NotificationPreset.VILLAGE_LOST.format(new Object[] {tv.getColor() + ""}, new Object[] {tv.getColor() + tv.getName() + " <> " + i.getName()}, new Object[] {tv.getColor() + "", hi.getName() + " <> " + (100 * ifl) + "%"}));
							}
							
							Statistic.TERRITORY_CLR.set(j, 100.0 * (Statistic.TERRITORY_CAPTURED.get(j) / (Statistic.TERRITORY_CAPTURED.get(j) + Statistic.TERRITORY_LOST.get(j))));
							
							if(influence > 0)
							{
								((RegionedGame) g).getExperienceHandler().giveXp(j, (long) influence, Experience.INFLUENCE_VILLAGE);
								g.pl().getMarketController().chanceShard(j);
							}
						}
						
						v.getMap().injectInfluence(v.popInfluenceMap());
					}
				}
			}
		}
		
		for(Territory i : g.getMap().getTerritories())
		{
			Faction secured = i.getFaction();
			
			if(i.getWarpgate())
			{
			
			}
			
			if(timers.containsKey(i))
			{
				Faction f = check(i);
				
				if(f == null)
				{
					f = i.getFaction();
				}
				
				timers.put(i, timers.get(i) - 1);
				
				if(!g.getGameController().pl().getServerDataComponent().isProduction())
				{
					timers.put(i, timers.get(i) - 9);
				}
				
				if(timers.get(i) <= 0)
				{
					Faction fx = check(i);
					
					if(fx != null)
					{
						timers.remove(i);
						i.setFaction(fx);
						i.accent(fx.getDyeColor());
						
						for(Capture j : i.getCaptures())
						{
							j.reset(fx);
							j.accent();
						}
						
						i.pullInfluenceMap();
						
						Player hi = null;
						double ifl = 0.0;
						
						for(Player j : i.getInfluenceMap().keySet())
						{
							if(i.getInfluence(j) > ifl)
							{
								ifl = i.getInfluence(j);
								hi = j;
							}
						}
						
						Squad sq = g.getSquadHandler().getSquad(hi);
												
						for(Player j : i.getInfluenceMap().keySet())
						{
							long influence = (long) (i.getInfluence(j) * 1337);
							
							if(g.getFactionHandler().getFaction(j).equals(fx))
							{
								Statistic.TERRITORY_CAPTURED.add(j);
								
								for(Location k : i.getSpawns())
								{
									GVFX.firework(k, i.getFaction());
								}
								
								for(Capture k : i.getCaptures())
								{
									GVFX.firework(k.getLocation(), i.getFaction());
								}
								
								GlacialPlugin.instance().scheduleSyncTask(1, new Runnable()
								{
									@Override
									public void run()
									{
										for(Location k : i.getSpawns())
										{
											GVFX.firework(k, i.getFaction());
										}
										
										for(Capture k : i.getCaptures())
										{
											GVFX.firework(k.getLocation(), i.getFaction());
										}
									}
								});
								
								if(sq != null)
								{
									g.getNotificationHandler().queue(j, NotificationPreset.TERRITORY_CAPTURED.format(new Object[] {fx.getColor() + ""}, new Object[] {fx.getColor() + fx.getName() + " <> " + i.getName()}, new Object[] {fx.getColor() + "", sq.getColor() + sq.getGreek().fName() + " <> " + (100 * ifl) + "%"}));
								}
								
								else
								{
									g.getNotificationHandler().queue(j, NotificationPreset.TERRITORY_CAPTURED.format(new Object[] {fx.getColor() + ""}, new Object[] {fx.getColor() + fx.getName() + " <> " + i.getName()}, new Object[] {fx.getColor() + "", hi.getName() + " <> " + (100 * ifl) + "%"}));
								}
								
								((RegionedGame) g).getExperienceHandler().giveXp(j, (long) 128, Experience.CAPTURE_TERRITORY);
							}
							
							else
							{
								Statistic.TERRITORY_LOST.add(j);
								
								g.getNotificationHandler().queue(j, NotificationPreset.TERRITORY_LOST.format(new Object[] {fx.getColor() + ""}, new Object[] {fx.getColor() + fx.getName() + " <> " + i.getName()}, new Object[] {fx.getColor() + "", hi.getName() + " <> " + (100 * ifl) + "%"}));
							}
							
							Statistic.TERRITORY_CLR.set(j, 100.0 * (Statistic.TERRITORY_CAPTURED.get(j) / (Statistic.TERRITORY_CAPTURED.get(j) + Statistic.TERRITORY_LOST.get(j))));
							
							if(influence > 0)
							{
								((RegionedGame) g).getExperienceHandler().giveXp(j, (long) influence, Experience.INFLUENCE_TERRITORY);
								g.pl().getMarketController().chanceShard(j);
							}
						}
						
						i.getMap().injectInfluence(i.popInfluenceMap());
					}
					
					else
					{
						timers.put(i, 1);
					}
				}
			}
			
			else
			{
				for(Capture j : i.getCaptures())
				{
					if(!j.getFaction().equals(secured))
					{
						timers.put(i, 60 + (i.getCaptures().size() * 23));
						continue;
					}
				}
			}
			
			for(Capture j : i.getCaptures())
			{
				j.soundOff();
			}
		}
		
		GList<Faction> accounted = new GList<Faction>();
		
		for(Territory i : g.getMap().getWarpgates())
		{
			accounted.add(i.getFaction());
		}
		
		accounted.removeDuplicates();
		
		for(Faction i : Faction.all())
		{
			if(!accounted.contains(i))
			{
				if(alive(i))
				{
					for(Player j : g.getFactionHandler().getPlayers(i))
					{
						g.getNotificationHandler().queue(j, NotificationPreset.FACTION_DEFEAT.format(new Object[] {i.getColor() + ""}, new Object[] {i.getColor() + ""}, null));
						g.pl().getMarketController().chanceShard(j);
					}
					
					for(Faction j : accounted)
					{
						for(Player k : g.getFactionHandler().getPlayers(j))
						{
							g.getNotificationHandler().queue(k, NotificationPreset.FACTION_ELIMINATED.format(new Object[] {i.getColor() + "" + i.getName()}, new Object[] {i.getColor() + "" + i.getName()}, null));
							g.pl().getMarketController().chanceShard(k);
						}
					}
					
					for(Territory j : g.getMap().getTerritories())
					{
						if(j.getFaction().equals(i))
						{
							j.accent(i.getDarkDye());
						}
					}
					
					for(Village j : g.getMap().getVillages())
					{
						if(j.getFaction().equals(i))
						{
							j.setFaction(Faction.neutral());
							j.accent(DyeColor.CYAN);
						}
					}
					
					alive.put(i, false);
					
					for(Player j : g.getFactionHandler().getPlayers(i).copy())
					{
						g.pl().getMarketController().awardShards(j);
						g.leave(j);
					}
				}
			}
		}
		
		int al = 0;
		
		for(Faction i : alive.keySet())
		{
			if(alive.get(i))
			{
				al++;
			}
		}
		
		if(al <= 1)
		{
			Faction winner = Faction.neutral();
			
			for(Territory i : g.getMap().getWarpgates())
			{
				winner = i.getFaction();
				break;
			}
			
			if(!won)
			{
				for(Player i : g.getMap().getPlayers())
				{
					g.notificationHandler.queue(i, NotificationPreset.FACTION_VICTORIOUS.format(new Object[] {winner.getColor() + ""}, new Object[] {winner.getColor() + "" + winner.getName()}, null));
				}
				
				won = true;
				g.win(winner);
			}
		}
		
		for(Player i : g.players())
		{
			g.updateBoard(i);
		}
	}
	
	public GList<String> getWarpgatePercentages()
	{
		GList<String> wgp = new GList<String>();
		
		for(Territory i : g.getMap().getWarpgates())
		{
			if(!i.getGenerator().isStable())
			{
				wgp.add(i.getFaction().getColor() + i.getFaction().getName() + ": " + i.getGenerator().getPercent() + "%");
			}
		}
		
		return wgp;
	}
	
	public Faction check(Territory t)
	{
		GMap<Faction, Integer> points = new GMap<Faction, Integer>();
		
		for(Capture i : t.getCaptures())
		{
			if(!points.containsKey(i.getFaction()))
			{
				points.put(i.getFaction(), 0);
			}
			
			points.put(i.getFaction(), points.get(i.getFaction()) + 1);
		}
		
		int c = Integer.MIN_VALUE;
		Faction f = null;
		
		for(Faction i : points.keySet())
		{
			if(points.get(i) > c)
			{
				c = points.get(i);
				f = i;
			}
		}
		
		if(points.get(f) < 1)
		{
			return null;
		}
		
		if(f != null)
		{
			for(Faction i : points.keySet())
			{
				if(i.equals(f))
				{
					continue;
				}
				
				if(points.get(i) == points.get(f))
				{
					f = null;
					break;
				}
			}
		}
		
		return f;
	}
	
	public boolean hasTimer(Territory t)
	{
		return timers.containsKey(t);
	}
	
	public String getTime(Territory t)
	{
		if(!hasTimer(t))
		{
			return "";
		}
		
		Duration d = new Duration(timers.get(t) * 1000);
		
		return d.getMinutes() + ":" + d.getSeconds();
	}
}
