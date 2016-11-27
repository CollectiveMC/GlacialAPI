package com.glacialrush.api.game.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.data.BountyData;
import com.glacialrush.api.game.event.DeathEvent;
import com.glacialrush.api.game.event.GameJoinEvent;
import com.glacialrush.api.game.event.GameQuitEvent;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;

public class BountyHandler extends GlacialHandler
{
	private GMap<Player, GList<Player>> hunters;
	
	public BountyHandler(Game game)
	{
		super(game);
		hunters = new GMap<Player, GList<Player>>();
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public BountyData get()
	{
		return game.pl().getServerDataComponent().getBountyData();
	}
	
	public void removePlayer(Player p)
	{
		get().getBounty().remove(p.getUniqueId().toString());
	}
	
	public void take(Player p, Player h)
	{
		if(Faction.get(h).equals(Faction.get(p)) || !hunters.containsKey(p) || hunters.get(p).contains(h))
		{
			return;
		}
		
		hunters.get(p).add(h);
		((RegionedGame)game).getNotificationHandler().queue(p, NotificationPreset.BOUNTY_HUNTING.format(null, new Object[]{h.getName()}, null));
		((RegionedGame)game).getNotificationHandler().queue(h, NotificationPreset.BOUNTY_TAKEN.format(null, new Object[]{p.getName()}, null));
	}
	
	public void place(Player p, int i)
	{
		if(get().getBounty().containsKey(p.getUniqueId().toString()))
		{
			return;
		}
		
		get().getBounty().put(p.getUniqueId().toString(), i);
		hunters.put(p, new GList<Player>());
		
		for(Player j : game.players())
		{
			if(Faction.get(j).equals(Faction.get(p)))
			{
				continue;
			}
			
			((RegionedGame)game).getNotificationHandler().queue(j, NotificationPreset.BOUNTY_PLACED.format(null, new Object[]{Faction.get(p).getColor() + p.getName()}, null));
		}
	}
	
	public void drop(Player p)
	{
		if(get().getBounty().containsKey(p.getUniqueId().toString()))
		{
			get().getBounty().remove(p.getUniqueId().toString());
			hunters.remove(p);
		}
	}
	
	public void dec(Player p, int i)
	{
		if(get().getBounty().containsKey(p.getUniqueId().toString()))
		{
			get().getBounty().put(p.getUniqueId().toString(), get().getBounty().get(p.getUniqueId().toString()) - i);
		}
	}
	
	public void inc(Player p, int i)
	{
		if(get().getBounty().containsKey(p.getUniqueId().toString()))
		{
			get().getBounty().put(p.getUniqueId().toString(), get().getBounty().get(p.getUniqueId().toString()) + i);
		}
	}
	
	public boolean hasBounty(Player p)
	{
		return get().getBounty().containsKey(p.getUniqueId().toString());
	}
	
	public int getBounty(Player p)
	{
		if(hasBounty(p))
		{
			return get().getBounty().get(p.getUniqueId().toString());
		}
		
		else
		{
			return 0;
		}
	}
	
	public void fulfilled(Player p, Player h)
	{
		int rw = getBounty(p);
		drop(p);
		
		hunters.remove(p);
		
		((RegionedGame)game).getNotificationHandler().queue(h, NotificationPreset.BOUNTY_FULFILLED.format(null, new Object[]{p.getName()}, new Object[]{rw + ""}));
		
		for(Player j : game.players())
		{
			if(Faction.get(j).equals(Faction.get(h)))
			{
				continue;
			}
			
			((RegionedGame)game).getNotificationHandler().queue(j, NotificationPreset.BOUNTY_FULFILLED.format(null, new Object[]{p.getName()}, new Object[]{rw + ""}));
		}
		
		((RegionedGame)game).getExperienceHandler().addSk(h, rw);
	}
	
	@EventHandler
	public void joinGame(GameJoinEvent e)
	{
		if(hasBounty(e.getPlayer()))
		{
			Player p = e.getPlayer();
			hunters.put(p, new GList<Player>());
			
			for(Player j : game.players())
			{
				if(Faction.get(j).equals(Faction.get(p)))
				{
					continue;
				}
				
				((RegionedGame)game).getNotificationHandler().queue(j, NotificationPreset.BOUNTY_PLACED.format(null, new Object[]{Faction.get(p).getColor() + p.getName()}, null));
			}
		}
	}
	
	@EventHandler
	public void quitGame(GameQuitEvent e)
	{
		hunters.remove(e.getPlayer());
		
		for(Player i : new GList<Player>(hunters.keySet()))
		{
			hunters.get(i).remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void deathEvent(DeathEvent e)
	{
		if(!e.suicide())
		{
			if(hunters.containsKey(e.getPlayer()) && hunters.get(e.getPlayer()).contains(e.getDamager()))
			{
				fulfilled(e.getPlayer(), e.getDamager());
			}
		}
	}
}
