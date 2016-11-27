package com.glacialrush.api.game.object;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.phantomapi.util.C;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.Title;
import com.glacialrush.api.game.GameController;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.object.GList;
import com.glacialrush.packet.resourcepack.ResourcePackAcceptedEvent;
import com.glacialrush.packet.resourcepack.ResourcePackDeclinedEvent;
import com.glacialrush.packet.resourcepack.ResourcePackLoadedEvent;

@SuppressWarnings("deprecation")
public class ResourceLoader implements Listener
{
	private GameController gc;
	private Player p;
	private GlacialPlugin pl;
	private PlayerData pd;
	private PlayerObject po;
	private Boolean acc;
	private GList<Title> titles;
	private Title loading;
	private Title loaded;
	private Title welcome;
	private Title faileda;
	private Title failedb;
	
	public ResourceLoader(Player p, GameController gc)
	{
		this.p = p;
		this.gc = gc;
		this.pl = this.gc.pl();
		this.pd = this.gc.gpd(p);
		this.acc = false;
		this.po = this.gc.gpo(p);
		this.titles = new GList<Title>();
		this.loading = new Title(ChatColor.AQUA + "Please Wait", ChatColor.DARK_GRAY + "Loading Resources", ChatColor.YELLOW + "If you get kicked, JOIN AGAIN. It works.", 0, 200, 100);
		this.loaded = new Title("", "", ChatColor.AQUA + "Loaded Resources");
		this.welcome = new Title(ChatColor.AQUA + "Welcome Back, " + p.getName() + "!", ChatColor.DARK_GRAY + "Feel the Rush!", 10, 30, 20);
		this.faileda = new Title(ChatColor.RED + "Hey There, " + p.getName() + "!", ChatColor.RED + "The Resource Pack Is Required.", 10, 30, 20);
		this.failedb = new Title(ChatColor.RED + "Please, ", ChatColor.RED + "Read the kick message before joining again!", 10, 30, 20);
		
		titles.add(new Title(ChatColor.AQUA + "Hey There, " + p.getName() + "!", ChatColor.GREEN + "Welcome to Glacial Rush!", 10, 30, 20));
		titles.add(new Title(ChatColor.AQUA + "Resource Pack Required", ChatColor.GREEN + "You won't be able to play without it.", 10, 90, 20));
		titles.add(new Title(ChatColor.AQUA + "Please Accept", ChatColor.GREEN + "Please Accept", 60, 500, 500));
		
		commit();
	}
	
	private boolean check()
	{
		if(p == null || !pl.onlinePlayers().contains(p))
		{
			return false;
		}
		
		return true;
	}
	
	private void commit()
	{
		pl.register(this);
		
		po.disable();
		
		if(pd.getResourcePackAccepted())
		{
			returning();
		}
		
		else
		{
			firstTime();
		}
		
		pl.scheduleSyncTask(1000, new Runnable()
		{
			@Override
			public void run()
			{
				if(check())
				{
					if(!po.getUsingPack())
					{
						p.kickPlayer(ChatColor.RED + "Resource pack was not accepted.\nPlease join again to play!");
					}
				}
			}
		});
	}
	
	private void firstTime()
	{
		titles.get(0).send(p);
		
		pl.scheduleSyncTask(60, new Runnable()
		{
			@Override
			public void run()
			{
				titles.get(1).send(p);
				
				pl.scheduleSyncTask(120, new Runnable()
				{
					@Override
					public void run()
					{
						titles.get(2).send(p);
						
						pl.scheduleSyncTask(70, new Runnable()
						{
							@Override
							public void run()
							{
								pl.getResourceController().send(p);
							}
						});
					}
				});
			}
		});
		
		pl.scheduleSyncTask(600, new Runnable()
		{
			public void run()
			{
				try
				{
					if(!acc)
					{
						p.kickPlayer(ChatColor.RED + "Pack Not Accepted or Loaded");
					}
				}
				
				catch(Exception e)
				{
					
				}
			}
		});
	}
	
	private void returning()
	{
		welcome.send(p);
		
		pl.scheduleSyncTask(60, new Runnable()
		{
			@Override
			public void run()
			{
				if(!check())
				{
					pl.unRegister(ResourceLoader.this);
					return;
				}
				
				loading.send(p);
				
				pl.scheduleSyncTask(10, new Runnable()
				{
					@Override
					public void run()
					{
						if(!check())
						{
							pl.unRegister(ResourceLoader.this);
							return;
						}
						
						pl.getResourceController().send(p);
					}
				});
			}
		});
	}
	
	@EventHandler
	public void onResource(ResourcePackAcceptedEvent e)
	{
		if(!check())
		{
			pl.unRegister(ResourceLoader.this);
			return;
		}
		
		loading.send(p);
		po.setUsingPack(true);
		acc = true;
	}
	
	@EventHandler
	public void onResource(ResourcePackDeclinedEvent e)
	{
		if(!check())
		{
			pl.unRegister(ResourceLoader.this);
			return;
		}
		
		pd.setResourcePackAccepted(false);
		faileda.send(p);
		
		pl.scheduleSyncTask(60, new Runnable()
		{
			@Override
			public void run()
			{
				if(!check())
				{
					pl.unRegister(ResourceLoader.this);
					return;
				}
				
				failedb.send(p);
				
				pl.scheduleSyncTask(60, new Runnable()
				{
					@Override
					public void run()
					{
						if(!check())
						{
							pl.unRegister(ResourceLoader.this);
							return;
						}
						
						p.kickPlayer(ChatColor.RED + "To Join, Read the following.\n" + ChatColor.GREEN + "1). Edit this server on your server list.\n" + ChatColor.GREEN + "2). Set 'Server Resource Packs: " + ChatColor.YELLOW + "PROMPT'");
						pl.unRegister(ResourceLoader.this);
					}
				});
			}
		});
	}
	
	@EventHandler
	public void onResource(ResourcePackLoadedEvent e)
	{
		if(!check())
		{
			pl.unRegister(ResourceLoader.this);
			return;
		}
		
		if(!pd.getResourcePackAccepted())
		{
			pl.scheduleSyncTask(60, new Runnable()
			{
				@Override
				public void run()
				{
					if(!check())
					{
						pl.unRegister(ResourceLoader.this);
						return;
					}
					
					pd.setResourcePackAccepted(true);
					p.setBedSpawnLocation(pl.getServerDataComponent().getHub(), true);
					
					gc.onJoin(e.getPlayer());
					po.enable();
					loaded.send(p);
					p.teleport(pl.getServerDataComponent().getHub());
					pl.unRegister(ResourceLoader.this);
					p.sendMessage(C.GREEN + "Click the center Games icon to join the fight!");
					p.getInventory().setHeldItemSlot(4);
				}
			});
		}
		
		else
		{
			pl.scheduleSyncTask(60, new Runnable()
			{
				@Override
				public void run()
				{
					if(!check())
					{
						pl.unRegister(ResourceLoader.this);
						return;
					}
					
					gc.onJoin(e.getPlayer());
					po.enable();
					loaded.send(p);
					p.teleport(pl.getServerDataComponent().getHub());
					pl.unRegister(ResourceLoader.this);
				}
			});
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerChatEvent e)
	{
		if(e.getPlayer().equals(p))
		{
			if(!acc)
			{
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "Relog to accept the pack.");
				p.kickPlayer(ChatColor.RED + "Please accept the pack");
			}
		}
	}
	
	@EventHandler
	public void on(PlayerCommandPreprocessEvent e)
	{
		if(e.getPlayer().equals(p))
		{
			if(!acc)
			{
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "Relog to accept the pack.");
				p.kickPlayer(ChatColor.RED + "Please accept the pack");
			}
		}
	}
	
	@EventHandler
	public void on(PlayerMoveEvent e)
	{
		if(e.getPlayer().equals(p))
		{
			if(!acc)
			{
				e.setCancelled(true);
			}
		}
	}
}
