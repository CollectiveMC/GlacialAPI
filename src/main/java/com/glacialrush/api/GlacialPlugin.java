package com.glacialrush.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import com.glacialrush.api.component.BuycraftController;
import com.glacialrush.api.component.ChunkController;
import com.glacialrush.api.component.ComponentManager;
import com.glacialrush.api.component.Controller;
import com.glacialrush.api.component.MarketController;
import com.glacialrush.api.component.PlayerDataComponent;
import com.glacialrush.api.component.ServerDataComponent;
import com.glacialrush.api.component.ThreadComponent;
import com.glacialrush.api.component.UIController;
import com.glacialrush.api.dispatch.DispatchListener;
import com.glacialrush.api.dispatch.Dispatcher;
import com.glacialrush.api.dispatch.LogController;
import com.glacialrush.api.game.GameController;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.host.GAPIHost;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.permafrost.Permafrost;
import com.glacialrush.api.security.SecurityController;
import com.glacialrush.api.thread.GlacialTask;
import com.glacialrush.packet.resourcepack.ResourceController;
import com.glacialrush.xapi.Format;

public class GlacialPlugin extends GPlugin
{
	protected Dispatcher dispatcher;
	protected UIController uiController;
	protected DispatchListener dispatchListener;
	protected ThreadComponent threadComponent;
	protected ComponentManager componentManager;
	protected LogController logController;
	protected SecurityController securityController;
	protected ResourceController resourceController;
	protected BuycraftController buycraftController;
	protected ServerDataComponent serverDataComponent;
	protected ChunkController chunkController;
	protected MarketController marketController;
	protected Permafrost permafrost;
	protected GAPIHost host;
	public PlayerDataComponent pdc;
	public GameController gameControl;
	
	protected static GlacialPlugin inst;
	private long ms;
	
	protected boolean update;
	
	public static GlacialPlugin instance()
	{
		return inst;
	}
	
	public GAPIHost host()
	{
		return host;
	}
	
	@Override
	public void enable()
	{
		ms = System.currentTimeMillis();
		inst = this;
		
		update = false;
		
		dispatcher = new Dispatcher((GlacialPlugin) this);
		componentManager = new ComponentManager(this);
		uiController = new UIController((GlacialPlugin) this);
		dispatchListener = new DispatchListener();
		threadComponent = new ThreadComponent((GlacialPlugin) this);
		securityController = new SecurityController(this);
		logController = new LogController(this);
		serverDataComponent = new ServerDataComponent(this);
		buycraftController = new BuycraftController(this);
		resourceController = new ResourceController(this);
		chunkController = new ChunkController(this);
		marketController = new MarketController(this);
		permafrost = new Permafrost(this);
		host = new GAPIHost(this);
	}
	
	public void calc()
	{
		o("||||||||||||||||||||||||||||||||");
		o("================================");
		v("FINISHED IN: " + Format.f(((double) ((double) System.currentTimeMillis() - (double) ms) / 1000.0), 2) + "s");
		o("================================");
		o("||||||||||||||||||||||||||||||||");
	}
	
	@Override
	public void register(Listener listener)
	{
		getServer().getPluginManager().registerEvents(listener, this);
		s(ChatColor.DARK_GREEN + "REG: " + ChatColor.BLUE + listener.getClass().getSimpleName());
	}
	
	@Override
	public void unRegister(Listener listener)
	{
		HandlerList.unregisterAll(listener);
		s(ChatColor.DARK_RED + "UN-REG: " + ChatColor.BLUE + listener.getClass().getSimpleName());
	}
	
	public ChunkController getChunkController()
	{
		return chunkController;
	}
	
	public MarketController getMarketController()
	{
		return marketController;
	}
	
	public GameController getGameControl()
	{
		return gameControl;
	}
	
	public BuycraftController getBuycraftController()
	{
		return buycraftController;
	}
	
	public ServerDataComponent getServerDataComponent()
	{
		return serverDataComponent;
	}
	
	public SecurityController getSecurityController()
	{
		return securityController;
	}
	
	public ResourceController getResourceController()
	{
		return resourceController;
	}
	
	public PlayerDataComponent getPdc()
	{
		return pdc;
	}
	
	public static GlacialPlugin getInst()
	{
		return inst;
	}
	
	public boolean isUpdate()
	{
		return update;
	}
	
	public PlayerData gpd(Player p)
	{
		return pdc.get(p);
	}
	
	public LogController getLogController()
	{
		return logController;
	}
	
	public void startComponents()
	{
		componentManager.enable();
	}
	
	@Override
	public void disable()
	{
		componentManager.disable();
	}
	
	public void cregister(Controller controller)
	{
		componentManager.register(controller);
	}
	
	public ComponentManager getComponentManager()
	{
		return componentManager;
	}
	
	public void verify(File file)
	{
		if(!file.exists())
		{
			file.mkdirs();
		}
		
		o("Verifying Directory: " + ChatColor.RED + file.getPath());
	}
	
	public void verifyFile(File file)
	{
		verify(file.getParentFile());
		
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
				o("Created File: " + ChatColor.RED + file.getPath());
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void saveLogs(GMap<String, GList<String>> log)
	{
		for(String i : log.keySet())
		{
			try
			{
				File f = new File(new File(getDataFolder(), "logs"), i.toLowerCase().replace(' ', '-') + ".txt");
				verifyFile(f);
				PrintWriter p = new PrintWriter(f);
				
				for(String j : log.get(i))
				{
					p.write(j);
				}
				
				p.close();
			}
			
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void newThread(GlacialTask runnable)
	{
		threadComponent.addTask(runnable);
	}
	
	public UIController getUiController()
	{
		return uiController;
	}
	
	public DispatchListener getDispatchListener()
	{
		return dispatchListener;
	}
	
	public void si(String... o)
	{
		dispatcher.sinfo(o);
	}
	
	public void ss(String... o)
	{
		dispatcher.ssuccess(o);
	}
	
	public void sf(String... o)
	{
		dispatcher.sfailure(o);
	}
	
	public void sw(String... o)
	{
		dispatcher.swarning(o);
	}
	
	public void sv(String... o)
	{
		dispatcher.sverbose(o);
	}
	
	public void so(String... o)
	{
		dispatcher.soverbose(o);
	}
	
	public ThreadComponent getThreadComponent()
	{
		return threadComponent;
	}
}
