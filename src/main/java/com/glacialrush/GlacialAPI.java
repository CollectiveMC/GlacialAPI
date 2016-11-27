package com.glacialrush;

import org.bukkit.event.Listener;
import com.glacialrush.api.GPlugin;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.host.GAPIHost;
import com.glacialrush.api.thread.MAPS;
import com.glacialrush.api.thread.TPS;

public class GlacialAPI extends GPlugin implements Listener
{
	public void onEnable()
	{
		super.onEnable();
		
		scheduleSyncRepeatingTask(0, 0, new TPS());
		scheduleSyncRepeatingTask(0, 20, new MAPS());
	}
	
	public void onDisable()
	{

	}
	
	public static GAPIHost getAPI()
	{
		return GlacialPlugin.instance().host();
	}
}
