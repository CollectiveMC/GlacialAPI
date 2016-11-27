package com.glacialrush.packet.resourcepack;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.component.Controller;

public class ResourceController extends Controller
{
	public static String address = "http://glacialrealms.com/resources/glacialrush/all.zip";
	private ProtocolManager protocolManager;
	
	public ResourceController(GlacialPlugin pl)
	{
		super(pl);
	}
	
	public void onDecline(Player p)
	{
		pl.callEvent(new ResourcePackDeclinedEvent(p));
	}
	
	public void onAccept(Player p)
	{
		pl.callEvent(new ResourcePackAcceptedEvent(p));
	}
	
	public void onLoaded(Player p)
	{
		pl.callEvent(new ResourcePackLoadedEvent(p));
	}
	
	public void onFailed(Player p)
	{
		pl.callEvent(new ResourcePackFailedEvent(p));
	}
	
	public void send(Player p)
	{
		pl.scheduleSyncTask(1, new Runnable()
		{
			@Override
			public void run()
			{
				o("Sent Resource Pack to " + p.getName());
				p.setResourcePack(address);
			}
		});
	}
	
	public void preEnable()
	{
		o("Started Resource Packet Listener");
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.protocolManager.addPacketListener((PacketListener) new PacketAdapter((Plugin) GlacialPlugin.instance(), ListenerPriority.NORMAL, new PacketType[] {PacketType.Play.Client.RESOURCE_PACK_STATUS})
		{
			public void onPacketReceiving(final PacketEvent event)
			{
				if(event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS)
				{
					EnumWrappers.ResourcePackStatus status = (EnumWrappers.ResourcePackStatus) event.getPacket().getResourcePackStatus().read(0);
					
					if(status.equals((Object) EnumWrappers.ResourcePackStatus.DECLINED))
					{
						// declined
						o(event.getPlayer() + " has declined the resource pack");
						onDecline(event.getPlayer());
					}
					
					else
					{
						if(status.equals((Object) EnumWrappers.ResourcePackStatus.ACCEPTED))
						{
							// accepted
							o(event.getPlayer() + " has accepted the resource pack");
							onAccept(event.getPlayer());
						}
						
						else if(status.equals((Object) EnumWrappers.ResourcePackStatus.SUCCESSFULLY_LOADED))
						{
							// loaded
							o(event.getPlayer() + " has loaded the resource pack clientside");
							onLoaded(event.getPlayer());
						}
						
						else if(status.equals((Object) EnumWrappers.ResourcePackStatus.FAILED_DOWNLOAD))
						{
							// failed
							o(event.getPlayer() + " failed to download the resource pack (download error)");
							onDecline(event.getPlayer());
						}
					}
				}
			}
		});
	}
}
