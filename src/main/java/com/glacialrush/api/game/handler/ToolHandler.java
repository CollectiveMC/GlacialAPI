package com.glacialrush.api.game.handler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.event.CombatEvent;
import com.glacialrush.api.game.event.GameQuitEvent;
import com.glacialrush.api.game.object.EMP;
import com.glacialrush.api.game.object.Grapple;
import com.glacialrush.api.game.object.Grenade;
import com.glacialrush.api.game.obtainable.item.Tool;
import com.glacialrush.api.game.obtainable.item.ToolType;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;

public class ToolHandler extends GlacialHandler
{
	private GMap<Player, Integer> cooldowns;
	private int cooldown;
	
	public ToolHandler(Game game)
	{
		super(game);
		
		cooldowns = new GMap<Player, Integer>();
		cooldown = 0;
	}
	
	public void tick(GameState state)
	{
		if(cooldown < 20)
		{
			cooldown++;
			return;
		}
		
		else
		{
			cooldown = 0;
		}
				
		for(Player i : new GList<Player>(cooldowns.keySet()))
		{
			cooldowns.put(i, cooldowns.get(i) - 1);
			
			if(cooldowns.get(i) < 1)
			{
				cooldowns.remove(i);
				game.getGameController().gpo(i).updateTool(ChatColor.GREEN + "Ready!", true);
				
				if(gameController.gpo(i).getGrapple() != null)
				{
					gameController.gpo(i).getGrapple().unhook();
					gameController.gpo(i).setGrapple(null);
				}
			}
			
			else
			{
				game.getGameController().gpo(i).updateTool(ChatColor.RED + "Waiting, " + ChatColor.YELLOW + cooldowns.get(i) + "s", false);
			}
		}
	}
	
	public Tool getTool(Player p)
	{
		return game.getGameController().gpo(p).getTool();
	}
	
	public boolean hasTool(Player p)
	{
		return game.getGameController().gpo(p).hasTool();
	}
	
	public boolean isHolding(Player p)
	{
		return hasTool(p) && game.getGameController().gpo(p).isHoldingTool();
	}
	
	public boolean isCooled(Player p)
	{
		return hasTool(p) && !cooldowns.containsKey(p);
	}
	
	public void cooldown(Player p)
	{
		if(hasTool(p))
		{
			if(getTool(p).getToolType().equals(ToolType.GRAPPLE))
			{
				game.getGameController().gpo(p).dehook();
			}
			
			if(getTool(p).getToolType().equals(ToolType.GRENADE))
			{
				return;
			}
			
			if(getTool(p).getToolType().equals(ToolType.EMP))
			{
				return;
			}
			
			if(getTool(p).getToolType().equals(ToolType.NOVA_GRENADE))
			{
				return;
			}
			
			cooldowns.put(p, getTool(p).getCooldown());
		}
	}
	
	public void fireTool(final Player p, final Tool t)
	{
		if(isCooled(p))
		{
			if(t.getToolType().equals(ToolType.GRAPPLE))
			{
				if(game.pl().target(p).getBlock().getType().equals(Material.AIR))
				{
					Audio.UI_FAIL.play(p);
					return;
				}
			}
			
			if(t.getToolType().equals(ToolType.GRENADE))
			{
				new Grenade(p);
			}
			
			if(t.getToolType().equals(ToolType.EMP))
			{
				new EMP(p);
			}
			
			cooldowns.put(p, t.getCooldown());
			game.getGameController().gpo(p).updateTool(ChatColor.RED + "Waiting, " + ChatColor.YELLOW + cooldowns.get(p) + "s", false);
			
			if(t.getToolType().equals(ToolType.GRAPPLE))
			{
				grapple(p);
				
				game.pl().scheduleSyncTask(10, new Runnable()
				{
					@Override
					public void run()
					{
						if(gameController.gpo(p).getGrapple() != null)
						{
							gameController.gpo(p).getGrapple().unhook();
							gameController.gpo(p).setGrapple(null);
						}
					}
				});
			}
			
			p.getInventory().setHeldItemSlot(0);
		}
		
		else
		{
			if(t.getToolType().equals(ToolType.GRAPPLE))
			{
				if(gameController.gpo(p).getGrapple() != null)
				{
					gameController.gpo(p).getGrapple().unhook();
					gameController.gpo(p).setGrapple(null);
					p.getInventory().setHeldItemSlot(0);
				}
			}
		}
	}
	
	public void grapple(Player p)
	{
		Grapple g = new Grapple(p);
		gameController.gpo(p).setGrapple(g);
	}
	
	@EventHandler
	public void leave(GameQuitEvent e)
	{
		cooldowns.remove(e.getPlayer());
	}
	
	@EventHandler
	public void combat(CombatEvent e)
	{
		cooldown(e.getPlayer());
		cooldown(e.getDamager());
	}
}
