package com.glacialrush.api.component;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.gui.Shortcut;
import com.glacialrush.api.gui.UI;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import org.bukkit.ChatColor;

public class UIController extends Controller
{
	private GMap<Player, UI> uis;
	private GList<Shortcut> shortcuts;
	
	public UIController(GlacialPlugin pl)
	{
		super(pl);
		
		uis = new GMap<Player, UI>();
		this.shortcuts = new GList<Shortcut>();
	}
	
	public void addShortcut(Shortcut shortcut)
	{
		shortcuts.add(shortcut);
	}
	
	@SuppressWarnings("deprecation")
	public void addShortcuts(Player p)
	{
		for(Shortcut i : shortcuts)
		{
			ItemStack stack = new ItemStack(i.getMaterial(), 1, (short) 0, i.getData());
			ItemMeta itMeta = stack.getItemMeta();
			itMeta.setDisplayName(i.getTitle());
			itMeta.setLore(i.getLore());
			stack.setItemMeta(itMeta);
			p.getInventory().setItem(i.getISlot(), new ItemStack(Material.AIR));
			p.getInventory().setItem(i.getSlot(), stack);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void moveShortcuts(Player p, boolean ingame)
	{
		if(ingame)
		{
			for(Shortcut i : shortcuts)
			{
				ItemStack stack = new ItemStack(i.getMaterial(), 1, (short) 0, i.getData());
				ItemMeta itMeta = stack.getItemMeta();
				itMeta.setDisplayName(i.getTitle());
				itMeta.setLore(i.getLore());
				stack.setItemMeta(itMeta);
				p.getInventory().setItem(i.getSlot(), new ItemStack(Material.AIR));
				p.getInventory().setItem(i.getISlot(), stack);
			}
		}
		
		else
		{
			for(Shortcut i : shortcuts)
			{
				ItemStack stack = new ItemStack(i.getMaterial(), 1, (short) 0, i.getData());
				ItemMeta itMeta = stack.getItemMeta();
				itMeta.setDisplayName(i.getTitle());
				itMeta.setLore(i.getLore());
				stack.setItemMeta(itMeta);
				p.getInventory().setItem(i.getISlot(), new ItemStack(Material.AIR));
				p.getInventory().setItem(i.getSlot(), stack);
				p.getInventory().setHeldItemSlot(4);
			}
		}
	}
	
	public void preEnable()
	{
		for(Player i : pl.onlinePlayers())
		{
			uis.put(i, new UI(pl, i));
		}
	}
	
	public void postDisable()
	{
		for(Player i : pl.onlinePlayers())
		{
			pl.unRegister(uis.get(i));
			uis.remove(i);
		}
	}
	
	@EventHandler
	public void onPlayer(PlayerJoinEvent e)
	{
		uis.put(e.getPlayer(), new UI(pl, e.getPlayer()));
		addShortcuts(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayer(PlayerGameModeChangeEvent e)
	{
		if(e.getNewGameMode().equals(GameMode.ADVENTURE))
		{
			e.getPlayer().getInventory().clear();
			addShortcuts(e.getPlayer());
		}
		
		else
		{
			e.getPlayer().getInventory().clear();
		}
	}
	
	@EventHandler
	public void onPlayer(PlayerQuitEvent e)
	{
		pl.unRegister(uis.get(e.getPlayer()));
		uis.remove(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onGuiClick(InventoryClickEvent e)
	{
		if(!GlacialPlugin.instance().getServerDataComponent().isProduction())
		{
			return;
		}
		
        if(e.getSlotType().equals(SlotType.ARMOR) && !e.getCurrentItem().getType().equals(Material.AIR))
		{
			e.setCancelled(true);
			return;
		}
		
		if(!e.getView().getPlayer().getGameMode().equals(GameMode.ADVENTURE) && !e.getView().getPlayer().getGameMode().equals(GameMode.SPECTATOR))
		{
			e.setCancelled(true);
			return;
		}
			
		if(shortcuts == null)
		{
			e.setCancelled(true);
			return;
		}
		
		if(e.getCurrentItem() == null)
		{
			e.setCancelled(true);
			return;
		}
		
		if(!e.getCurrentItem().hasItemMeta())
		{
			e.setCancelled(true);
			return;
		}
		
		if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLACK + "   "))
		{
			e.setCancelled(true);
			return;
		}
		
		for(Shortcut i : shortcuts)
		{
			if((e.getSlot() == i.getSlot() || e.getSlot() == i.getISlot()) && i.getTitle().equals(e.getCurrentItem().getItemMeta().getDisplayName()))
			{
				e.setCancelled(true);
				
				i.launch(get((Player) e.getWhoClicked()));
			}
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e)
	{
		if(!e.getPlayer().getGameMode().equals(GameMode.ADVENTURE))
		{
			return;
		}
		
		if(e.getPlayer().getItemInHand() == null)
		{
			return;
		}
		
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			for(Shortcut i : shortcuts)
			{
				if(e.getItem() != null && e.getItem().hasItemMeta())
				{
					if(e.getPlayer().getInventory().getHeldItemSlot() == i.getSlot() && i.getTitle().equals(e.getItem().getItemMeta().getDisplayName()))
					{
						e.setCancelled(true);
						
						i.launch(get((Player) e.getPlayer()));
					}
				}
			}
		}
	}
	
	public UI get(Player p)
	{
		return uis.get(p);
	}
}
