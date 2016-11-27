package com.glacialrush.api.game.timer;

import org.bukkit.entity.Player;
import org.phantomapi.world.Area;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.dispatch.notification.NotificationPriority;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.object.Capture;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;
import com.glacialrush.api.vfx.GVFX;

public class CaptureTimer extends GameTimer
{
	public CaptureTimer(RegionedGame g)
	{
		super(g, 3, "CaptureTimer");
	}
	
	public void tick()
	{
		super.tick();
		
		for(Territory i : g.getMap().getTerritories())
		{
			if(!i.getPlayers().isEmpty())
			{
				for(Capture j : i.getCaptures())
				{
					if(!j.getPlayers().isEmpty())
					{
						handleCapture(j);
					}
					
					j.accent();
				}
			}
			
			for(Capture j : i.getCaptures())
			{
				if(j.getPlayers().isEmpty())
				{
					if(j.getProgress() < 100)
					{
						j.setProgress(j.getProgress() + 1);
						j.accentRandom(j.getFaction());
						
						if(j.getProgress() == 100)
						{
							j.reset(j.getFaction());
							j.accent(true);
						}
						
						if(j.getProgress() > 100)
						{
							j.setProgress(100);
						}
					}
				}
			}
		}
	}
	
	public void handleCapture(Capture c)
	{
		GMap<Faction, GList<Player>> cappers = g.getFactionHandler().sort(c.getPlayers());
		GList<Player> players = c.getPlayers().copy();
		Boolean contested = false;
		
		for(Territory i : g.getMap().getTerritories())
		{
			if(!i.getCaptures().isEmpty())
			{
				Capture cxx = i.getCaptures().copy().pickRandom();
				
				if(cxx.getOffense() != null && !cxx.getPlayers().isEmpty() && (cxx.getProgress() == 100 || cxx.getProgress() == 0))
				{
					cxx.accent(true);
				}
			}
		}
		
		for(Faction i : Faction.all())
		{
			if(cappers.containsKey(i))
			{
				if(!c.canCapture(i))
				{
					for(Player j : cappers.get(i))
					{
						Faction cf = c.getFaction();
						g.getNotificationHandler().queue(j, NotificationPreset.CAPTURE_NO_CONNECTED_REGION.format(null, null, new Object[] {cf.getColor() + cf.getName()}));
					}
					
					cappers.remove(i);
				}
				
				else
				{
					for(Player j : cappers.get(i))
					{
						players.add(j);
					}
				}
			}
		}
		
		Faction strongest = g.getFactionHandler().strongest(players);
		Faction defense = c.getFaction();
		
		if(strongest != null)
		{
			for(Faction i : cappers.keySet())
			{
				if(!i.equals(strongest))
				{
					contested = true;
					break;
				}
			}
			
			if(!c.canCapture(strongest))
			{
				return;
			}
			
			if(!contested)
			{
				c.offend(strongest);
				
				if(!g.getGameController().pl().getServerDataComponent().isProduction())
				{
					c.setFaction(strongest);
				}
			}
			
			for(Player i : cappers.get(strongest))
			{
				if(!strongest.equals(c.getFaction()))
				{
					c.influence(i, 1);
				}
			}
			
			if(!defense.equals(c.getFaction()))
			{
				if(strongest.equals(c.getFaction()))
				{
					c.setFaction(strongest);
					c.accent();
					Audio.CAPTURE_CAPPED.playGlobal(c.getLocation());
					
					Area a = new Area(c.getLocation(), 3.3);
					
					for(int i = 0; i < 32; i++)
					{
						GVFX.particle(strongest, a.random());
					}
					
					Notification n = NotificationPreset.CAPTURE_TOOK.format(null, null, new Object[]{strongest.getColor() + strongest.getName(), c.getTerritory().getFaction().getColor() + c.getTerritory().getName()});
					
					n.setPriority(NotificationPriority.LOWEST);
					
					for(Player i : g.getMap().getPlayers())
					{
						n.show(i);
					}
					
					for(Player i : cappers.get(strongest))
					{
						Statistic.TERRITORY_POINTS.add(i);
						
						if(c.getInfluenceMap().containsKey(i))
						{
							long influence = (long) (c.getInfluence(i) * 32);
							
							if(influence > 0)
							{
								((RegionedGame) g).getExperienceHandler().giveXp(i, 20 + influence, Experience.INFLUENCE_POINT);
								g.pl().getMarketController().chanceShard(i);
							}
						}
						
						else
						{
							((RegionedGame) g).getExperienceHandler().giveXp(i, (long) 20, Experience.INFLUENCE_POINT);
						}
					}
					
					((RegionedGame)g).getDropletHandler().dropSomethings(c.getLocation().add(0, 4, 0), 40, Experience.INFLUENCE_POINT, 3);
					
					GVFX.firework(c.getLocation().add(0, 3, 0), c.getFaction());
					
					for(Player i : c.getTerritory().getInfluenceMap().keySet())
					{
						d.overbose(i.getName() + ": " + c.getTerritory().getInfluenceMap().get(i));
					}
				}
			}
			
			for(Player i : players)
			{
				if(c.getOffense() == null)
				{
					g.getNotificationHandler().queue(i, NotificationPreset.CAPTURE_SECURED.format(null, null, new Object[] {c.captureGraph()}));
				}
				
				else
				{
					if(contested)
					{
						g.getNotificationHandler().queue(i, NotificationPreset.CAPTURE_CONTESTED.format(null, null, new Object[] {c.captureGraph()}));
					}
					
					else
					{
						g.getNotificationHandler().queue(i, NotificationPreset.CAPTURE_CAPTURING.format(null, null, new Object[] {c.captureGraph()}));
					}
				}
			}
		}
	}
}
