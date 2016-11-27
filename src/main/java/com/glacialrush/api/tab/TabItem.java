/*
 * Copyright 2015 Marvin Schäfer (inventivetalent). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package com.glacialrush.api.tab;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class TabItem
{
	
	protected UUID uuid;
	protected String name;
	protected String skin;
	protected int ping;
	protected GameMode gamemode;
	
	protected Object profile;
	
	/**
	 * Constructs a new TabItem
	 */
	public TabItem(UUID uuid, String name, String skin, int ping, GameMode mode)
	{
		if(name.length() > 16)
		{
			throw new IllegalArgumentException("name can only be 16 characters long");
		}
		if(skin != null && skin.length() > 16)
		{
			throw new IllegalArgumentException("skin can only be 16 characters long");
		}
		
		this.uuid = uuid;
		this.name = name;
		this.skin = skin;
		this.ping = ping;
		this.gamemode = mode;
	}
	
	/**
	 * Constructs a new TabItem
	 */
	public TabItem(String name, String skin, int ping, GameMode mode)
	{
		this(UUID.randomUUID(), name, skin, ping, mode);
	}
	
	/**
	 * Constructs a new TabItem
	 */
	public TabItem(String name, int ping, GameMode mode)
	{
		this(name, name, ping, mode);
	}
	
	/**
	 * Constructs a new TabItem
	 */
	public TabItem(String name)
	{
		this(name, 0, Bukkit.getDefaultGameMode());
	}
	
	protected Object toPacket(final Tab tab, final int action)
	{
		if(profile == null)
		{
			try
			{
				profile = NMSClass.GameProfile.getConstructor(UUID.class, String.class).newInstance(this.uuid, this.name);
				
				if(skin != null)
				{
					Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("TabListAPI"), new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								Object cache = NMSClass.TileEntitySkull.getDeclaredField("skinCache").get(null);
								
								Object skinProfile = NMSClass.LoadingCache.getDeclaredMethod("getUnchecked", Object.class).invoke(cache, (Object) skin.toLowerCase());
								
								AccessUtil.setAccessible(NMSClass.GameProfile.getDeclaredField("id")).set(skinProfile, uuid);
								AccessUtil.setAccessible(NMSClass.GameProfile.getDeclaredField("name")).set(skinProfile, name);
								
								profile = skinProfile;
								
								TabAPI.updateTab(tab.player);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					});
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(Reflection.getVersion().contains("1_7"))
		{
			try
			{
				Object packet = NMSClass.PacketPlayOutPlayerInfo.newInstance();
				AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerInfo.getDeclaredField("action")).set(packet, action);
				AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerInfo.getDeclaredField("username")).set(packet, name);
				AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerInfo.getDeclaredField("gamemode")).set(packet, gamemode.ordinal());
				AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerInfo.getDeclaredField("ping")).set(packet, ping);
				AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerInfo.getDeclaredField("player")).set(packet, profile);
				return packet;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Object gMode = NMSClass.EnumGamemode.getDeclaredMethod("getById", int.class).invoke(null, gamemode.ordinal());
				Object packet = NMSClass.PlayerInfoData.getConstructor(NMSClass.PacketPlayOutPlayerInfo, NMSClass.GameProfile, int.class, NMSClass.EnumGamemode, NMSClass.IChatBaseComponent).newInstance(null, profile, ping, gMode, Util.serializeChat(TabAPI.convertJSON(name)));
				return packet;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}
