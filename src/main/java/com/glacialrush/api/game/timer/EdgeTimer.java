package com.glacialrush.api.game.timer;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.sfx.Audio;

public class EdgeTimer extends GameTimer
{
	public EdgeTimer(RegionedGame g)
	{
		super(g, 20, "Edge Timer");
	}
	
	public void tick()
	{
		for(Player i : new GList<Player>(g.getTospectating().keySet()))
		{
			if(g.getTospectating().get(i) < 1)
			{
				g.getTospectating().remove(i);
				g.getSpectating().add(i);
				g.getNotificationHandler().resetOngoing(i);
				i.setGameMode(GameMode.SPECTATOR);
			}
			
			else
			{
				g.getTospectating().put(i, g.getTospectating().get(i) - 1);
				g.getNotificationHandler().queue(i, NotificationPreset.RESPAWN_SPECTATE.format(null, null, new Object[] {g.getTospectating().get(i)}));
				Audio.UI_ACTION.play(i);
			}
		}
		
		for(Player i : g.players())
		{
			if(g.getDeploying().contains(i))
			{
				continue;
			}
			
			Region r = g.getMap().getRegion(i);
			
			if(r == null)
			{
				if(i.getGameMode().equals(GameMode.SPECTATOR))
				{
					i.setHealth(i.getMaxHealth());
					i.teleport(g.getWarpGate(g.getFactionHandler().getFaction(i)).getSpawns().pickRandom());
				}
				
				else
				{
					if(!g.getDeploying().contains(i))
					{
						g.kill(i);
					}
				}
			}
		}
		
		for(Region j : g.getMap().getRegions())
		{
			if(j.getType().equals(RegionType.EDGE))
			{
				for(Player i : j.getPlayers())
				{
					double d = 0;
					
					g.getNotificationHandler().queue(i, NotificationPreset.WARNING_LEAVE.format(null, null, null));
					d = 2.0;
					
					if(g.getOutsiders().containsKey(i))
					{
						g.getOutsiders().put(i, g.getOutsiders().get(i) + 1);
						d += (2.0 * g.getOutsiders().get(i));
					}
					
					else
					{
						g.getOutsiders().put(i, 1);
					}
					
					if(i.getHealth() - d < 0)
					{
						g.kill(i);
					}
					
					else
					{
						i.damage(d);
					}
				}
			}
		}
		
		for(Region i : g.getMap().getRegions())
		{
			for(Player j : new GList<Player>(g.getOutsiders().keySet()))
			{
				if(i.contains(j))
				{
					if(!i.getType().equals(RegionType.EDGE))
					{
						g.getOutsiders().remove(j);
					}
				}
			}
		}
	}
}
