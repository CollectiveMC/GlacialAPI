package com.glacialrush.api.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import com.glacialrush.api.GlacialPlugin;

public class HubController extends Controller
{
	public HubController(GlacialPlugin pl)
	{
		super(pl);
	}
	
	public void postEnable()
	{
		
	}
	
	public void postDisable()
	{
		
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(pl.getGameControl().getGame(e.getPlayer()) == null)
			{
				if(e.getPlayer().getItemInHand().getType().equals(Material.GHAST_TEAR))
				{
					
				}
			}
		}
	}
	
	public void useCrate(Player p)
	{
		
	}
}
