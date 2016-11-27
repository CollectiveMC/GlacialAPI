package com.glacialrush.api.game.handler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.packet.EntityHider;
import com.glacialrush.packet.EntityHider.Policy;
import com.glacialrush.packet.NMS;

public class PlayerHandler extends GlacialHandler
{
	private EntityHider eh;
	private int cooldown;
	private GList<Player> invisible;
	private GMap<Player, Integer> lagging;
	
	public PlayerHandler(Game game)
	{
		super(game);
		eh = new EntityHider(game.pl(), Policy.BLACKLIST);
		cooldown = 0;
		lagging = new GMap<Player, Integer>();
		invisible = new GList<Player>();
	}
	
	public void start(GameState state)
	{
	
	}
	
	public void stop(GameState state)
	{
	
	}
	
	public void tick(GameState state)
	{
		cooldown++;
		
		if(cooldown >= 4)
		{
			cooldown = 0;
			
			tickPing();
		}
	}
	
	public void tickPing()
	{
		for(Player i : game.players())
		{
			if(getPing(i) > 150)
			{
				if(!lagging.containsKey(i))
				{
					lagging.put(i, 1);
				}
				
				lagging.put(i, lagging.get(i) + 1);
				
				if(lagging.get(i) == 150)
				{
					for(Player j : game.players())
					{
						j.sendMessage(ChatColor.GOLD + i.getName() + " is lagging.");
					}
				}
			}
			
			else
			{
				lagging.remove(i);
			}
		}
	}
	
	public int getPing(Player p)
	{
		return NMS.ping(p);
	}
	
	public void reset(Player p)
	{
		for(Player i : game.pl().onlinePlayers())
		{
			eh.showEntity(p, i);
			eh.showEntity(i, p);
		}
	}

	public EntityHider getEh()
	{
		return eh;
	}

	public int getCooldown()
	{
		return cooldown;
	}

	public GList<Player> getInvisible()
	{
		return invisible;
	}
}
