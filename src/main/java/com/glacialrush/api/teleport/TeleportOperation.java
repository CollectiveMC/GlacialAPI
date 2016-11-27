package com.glacialrush.api.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.event.CombatEvent;
import com.glacialrush.api.sfx.Audio;

public class TeleportOperation implements Listener
{
	private Player p;
	private Integer ticks;
	private Location destination;
	private TeleportTick teleportTick;
	private Runnable teleportCancelled;
	private Runnable teleported;
	private Boolean cancelOnCombat;
	private Boolean combatted;
	private Audio audio;
	private Boolean destroy;
	
	public TeleportOperation(Player p, Integer ticks, Location destination)
	{
		this.p = p;
		this.ticks = Math.abs(ticks);
		this.destination = destination;
		this.cancelOnCombat = false;
		this.audio = null;
		this.destroy = false;
		this.combatted = false;
	}
	
	public TeleportOperation onTeleported(Runnable runnable)
	{
		this.teleported = runnable;
		return this;
	}
	
	public TeleportOperation onTeleportCancelled(Runnable runnable)
	{
		this.teleportCancelled = runnable;
		return this;
	}
	
	public TeleportOperation onTeleportTick(TeleportTick tick)
	{
		this.teleportTick = tick;
		return this;
	}
	
	public TeleportOperation cancelOnCombat()
	{
		cancelOnCombat = true;
		return this;
	}
	
	public TeleportOperation tickAudio(Audio audio)
	{
		this.audio = audio;
		return this;
	}
	
	public void abort()
	{
		cancel();
	}
	
	public void destroy()
	{
		destroy = true;
	}
	
	public void commit()
	{
		if(cancelOnCombat)
		{
			GlacialPlugin.instance().register(this);
		}
		
		final int[] left = new int[] {ticks.intValue(), 0};
		
		left[1] = GlacialPlugin.instance().scheduleSyncRepeatingTask(0, 0, new Runnable()
		{
			@Override
			public void run()
			{
				if(destroy)
				{
					if(teleportCancelled != null)
					{
						teleportCancelled.run();
					}
					
					destination = null;
					
					GlacialPlugin.instance().cancelTask(left[1]);
					GlacialPlugin.instance().unRegister(TeleportOperation.this);
					
					return;
				}
				
				onTick(left[0] + 1);
				
				left[0]--;
				
				if(combatted && cancelOnCombat)
				{
					GlacialPlugin.instance().cancelTask(left[0]);
					GlacialPlugin.instance().unRegister(TeleportOperation.this);
					
					if(teleportCancelled != null)
					{
						teleportCancelled.run();
					}
					
					return;
				}
				
				if(left[0] <= 5)
				{
					if(p.getOpenInventory().getType().equals(InventoryType.CRAFTING))
					{
						onTeleport();
						GlacialPlugin.instance().cancelTask(left[1]);
						GlacialPlugin.instance().unRegister(TeleportOperation.this);
					}
					
					else
					{
						left[0] = 29;
					}
					
					return;
				}
			}
		});
	}
	
	public void onTick(int timeLeft)
	{
		if((double)timeLeft % 20.0 == 0.0)
		{
			if(audio != null)
			{
				audio.play(p);
			}
		}
		
		if(teleportTick != null)
		{
			teleportTick.run(timeLeft);
		}
	}
	
	public void onTeleport()
	{
		if(destination == null)
		{
			return;
		}
		
		p.teleport(destination);
		
		if(teleported != null)
		{
			teleported.run();
		}
	}
	
	public void cancel()
	{
		if(combatted)
		{
			return;
		}
		
		combatted = true;
		destroy = true;
	}
	
	@EventHandler
	public void onCombat(CombatEvent e)
	{
		if(e.getDamager().equals(p) || e.getPlayer().equals(p))
		{
			cancel();
		}
	}
}
