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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class Tab
{
	
	protected static Object EMPTY_COMPONENT;
	
	static
	{
		try
		{
			EMPTY_COMPONENT = Reflection.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance("");
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected final Player player;
	
	// Header + Footer
	protected String[] header;
	protected String[] footer;
	
	// Items
	protected List<TabItem> items = new ArrayList<TabItem>();
	
	protected Tab(Player p)
	{
		this.player = p;
	}
	
	public void clearItems()
	{
		updateItems(4);
	}
	
	public void updateItems()
	{
		updateItems(0);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void updateItems(int action)
	{
		try
		{
			List<Object> itemPackets = new ArrayList<Object>();
			for(TabItem item : items)
			{
				itemPackets.add(item.toPacket(this, action));
			}
			
			List<Object> packets = new ArrayList<Object>();
			
			if(NMSClass.version < 180)
			{
				packets.addAll(itemPackets);
			}
			else
			{
				Object playerInfoPacket = NMSClass.PacketPlayOutPlayerInfo.newInstance();
				AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerInfo.getDeclaredField("a")).set(playerInfoPacket, NMSClass.EnumPlayerInfoAction.getEnumConstants()[action]);
				List<Object> dataList = (List) AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerInfo.getDeclaredField("b")).get(playerInfoPacket);
				dataList.addAll(itemPackets);
				packets.add(playerInfoPacket);
			}
			
			for(Object packet : packets)
			{
				Util.sendPacket(player, packet);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateHeaderFooter()
	{
		if(header == null && footer == null)
		{
			return;
		}
		try
		{
			Object packet = NMSClass.PacketPlayOutPlayerListHeaderFooter.getConstructor(NMSClass.IChatBaseComponent).newInstance(new Object[] {null});
			
			Object serHeader = header == null ? EMPTY_COMPONENT : Util.serializeChat(mergeJSON(header));
			Object serFooter = footer == null ? EMPTY_COMPONENT : Util.serializeChat(mergeJSON(footer));
			
			AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerListHeaderFooter.getDeclaredField("a")).set(packet, serHeader);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutPlayerListHeaderFooter.getDeclaredField("b")).set(packet, serFooter);
			
			Util.sendPacket(player, packet);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	private String mergeJSON(String... json)
	{
		String merged = "";
		for(String s : json)
		{
			merged += s + ",";
		}
		merged = merged.substring(0, merged.length() - 1);
		return String.format("{\"text\":\"\",\"extra\":[%s]}", merged);
	}
	
}
