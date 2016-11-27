package com.glacialrush.api.dispatch.notification;

import java.util.UUID;
import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;

public class NotificationHandler extends GlacialHandler
{
	private GMap<Player, GList<Notification>> queue;
	private GMap<Player, Notification> ongoing;
	private GMap<Player, Integer> cooldown;
	
	public NotificationHandler(Game game)
	{
		super(game);
	}
	
	@Override
	public void start(GameState state)
	{
		super.start(state);
		
		queue = new GMap<Player, GList<Notification>>();
		ongoing = new GMap<Player, Notification>();
		cooldown = new GMap<Player, Integer>();
	}
	
	@Override
	public void tick(GameState state)
	{
		super.tick(state);
		
		for(Player i : queue.keySet())
		{
			process(i);
		}
	}
	
	@Override
	public void stop(GameState state)
	{
		super.stop(state);
		
		queue = new GMap<Player, GList<Notification>>();
		ongoing = new GMap<Player, Notification>();
		cooldown = new GMap<Player, Integer>();
	}
	
	public void addPlayer(Player p)
	{
		queue.put(p, new GList<Notification>());
		cooldown.put(p, 0);
	}
	
	public void queue(Player p, Notification n)
	{
		if(n == null)
		{
			return;
		}
		
		if(!queue.containsKey(p))
		{
			addPlayer(p);
		}
		
		if(n.isOngoing() != null && n.isOngoing())
		{
			ongoing.put(p, n);
		}
		
		else
		{
			if(n.getNoDupe())
			{
				UUID tag = n.getDupeTag();
				
				for(Notification i : queue.get(p))
				{
					if(i.getNoDupe())
					{
						if(i.getDupeTag().equals(tag))
						{
							return;
						}
					}
				}
			}
			
			queue.get(p).add(n);
		}
	}
	
	public void resetOngoing(Player p)
	{
		ongoing.remove(p);
	}
	
	public void removePlayer(Player p)
	{
		queue.remove(p);
		cooldown.remove(p);
		ongoing.remove(p);
	}
	
	public void process(Player p)
	{
		Notification o = ongoing.get(p);

		if(cooldown.get(p) > 0)
		{
			cooldown.put(p, cooldown.get(p) - 1);
			
			if(o != null)
			{
				o.show(p);
			}
			
			return;
		}
		
		Notification n = nextNotification(p);
		
		if(n != null)
		{
			if(o != null)
			{
				if(canMerge(n, o))
				{
					if(legitimateLine(o.getTitlea()))
					{
						n.setTitlea(o.getTitlea());
					}
					
					if(legitimateLine(o.getTitleb()))
					{
						n.setTitleb(o.getTitleb());
					}
					
					if(legitimateLine(o.getTitlec()))
					{
						n.setTitlec(o.getTitlec());
					}
					
					cooldown.put(p, n.getDelay() + n.getDisplay());
					n.show(p);
					queue.get(p).remove(nextNotification(p));
				}
				
				else
				{
					o.show(p);
				}
			}
			
			else
			{
				cooldown.put(p, n.getDelay() + n.getDisplay());
				n.show(p);
				queue.get(p).remove(nextNotification(p));
			}
		}
		
		else if(o != null)
		{
			o.show(p);
		}
	}
	
	public Notification nextNotification(Player p)
	{
		for(NotificationPriority i : NotificationPriority.topDown())
		{
			for(Notification j : queue.get(p))
			{
				if(j.getPriority().equals(i))
				{
					return j;
				}
			}
		}
		
		return null;
	}
	
	public boolean canMerge(Notification a, Notification b)
	{
		return canMerge(a.getTitlea(), b.getTitlea()) && canMerge(a.getTitleb(), b.getTitleb()) && canMerge(a.getTitlec(), b.getTitlec());
	}
	
	public boolean canMerge(String a, String b)
	{
		return (legitimateLine(a) != legitimateLine(b)) || (!legitimateLine(a) && !legitimateLine(b));
	}
	
	public boolean legitimateLine(String s)
	{
		return s != null && !s.equals("") && !s.equals(" ");
	}
}
