package com.glacialrush.api.gui;

import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.glacialrush.api.GPlugin;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.sfx.Audio;

public class UI implements Listener
{
	private final GPlugin pl;
	private Player player;
	private GList<Pane> panes;
	private Pane defaultPane;
	private Inventory inventory;
	private Boolean open;
	private Pane openPane;
	private Boolean updating;
	private Audio audio;
	
	public UI(GPlugin pl, Player player)
	{
		this.pl = pl;
		this.player = player;
		this.panes = new GList<Pane>();
		this.open = false;
		this.updating = false;
		
		pl.register(this);
		
		pl.scheduleSyncRepeatingTask(0, 2, new Runnable()
		{
			@Override
			public void run()
			{
				if(audio != null)
				{
					audio.play(getPlayer());
					audio = null;
				}
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	public void open(Pane pane, boolean playSound)
	{
		inventory = pl.getServer().createInventory(null, 54, pane.getTitle());
		
		for(Element i : pane.getElements())
		{
			ItemStack stack = new ItemStack(i.getMaterial(), 1, (short) 0, i.getData());
			ItemMeta stMeta = stack.getItemMeta();
			stMeta.setDisplayName(i.getTitle());
			stMeta.setLore(i.getLore());
			stack.setItemMeta(stMeta);
			
			inventory.setItem(i.getSlot(), stack);
		}
		
		open = true;
		openPane = pane;
		
		if(playSound)
		{
			audio = Audio.UI_OPEN;
		}
		
		player.openInventory(inventory);
	}
	
	public void open(Pane pane)
	{
		open(pane, true);
	}
	
	public UI update()
	{
		return update(false);
	}
	
	public UI update(boolean sound)
	{
		if(open)
		{
			updating = true;
			open(getOpenPane(), sound);
			updating = false;
		}
		
		return this;
	}
	
	public UI closed()
	{
		if(updating)
		{
			return this;
		}
		
		open = false;
		inventory = null;
		openPane = null;
		audio = Audio.UI_CLOSE;
		
		for(Pane i : panes)
		{
			i.getElements().clear();
		}
		
		panes.clear();
		return this;
	}
	
	public UI close()
	{
		player.closeInventory();
		closed();
		return this;
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public UI setInventory(Inventory inventory)
	{
		this.inventory = inventory;
		return this;
	}
	
	public Boolean isOpen()
	{
		return open;
	}
	
	public UI setOpen(Boolean open)
	{
		this.open = open;
		return this;
	}
	
	public Pane getOpenPane()
	{
		return openPane;
	}
	
	public UI setOpenPane(Pane openPane)
	{
		this.openPane = openPane;
		return this;
	}
	
	public UI add(Pane pane)
	{
		panes.add(pane);
		return this;
	}
	
	public UI remove(Pane pane)
	{
		panes.remove(pane);
		return this;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public UI setPlayer(Player player)
	{
		this.player = player;
		return this;
	}
	
	public GList<Pane> getPanes()
	{
		return panes;
	}
	
	public UI setPanes(GList<Pane> panes)
	{
		this.panes = panes;
		return this;
	}
	
	public Pane getDefaultPane()
	{
		return defaultPane;
	}
	
	public UI setDefaultPane(Pane defaultPane)
	{
		this.defaultPane = defaultPane;
		return this;
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e)
	{
		if(e.getPlayer().equals(player) && open)
		{
			closed();
		}
	}
	
	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent e)
	{
		if(e.getPlayer().equals(player) && open)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e)
	{
		if(e.getPlayer().equals(player) && open)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e)
	{
		if(!GlacialPlugin.instance().getServerDataComponent().isProduction())
		{
			return;
		}
		
		if(e.getWhoClicked().equals(player) && open)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onGuiClick(InventoryClickEvent e)
	{
		if(!GlacialPlugin.instance().getServerDataComponent().isProduction())
		{
			return;
		}
		
		if(!(e.getWhoClicked().equals(player)))
		{
			return;
		}
		
		if(!open)
		{
			return;
		}
		
		if(e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta())
		{
			e.setCancelled(true);
			return;
		}
		
		Iterator<Element> it = openPane.getElements().copy().iterator();
		
		while(it.hasNext())
		{
			Element i = it.next();
			
			if(i.getSlot() == e.getSlot())
			{
				if(e.getAction().equals(InventoryAction.PICKUP_ALL))
				{
					if(i.getOnLeftClickListener() != null)
					{
						i.getOnLeftClickListener().run(i);
					}
				}
				
				if(e.getAction().equals(InventoryAction.PICKUP_HALF))
				{
					if(i.getOnRightClickListener() != null)
					{
						i.getOnRightClickListener().run(i);
					}
				}
			}
		}
		
		e.setCancelled(true);
	}
}
