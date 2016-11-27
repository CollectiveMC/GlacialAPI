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

/**
 * © Copyright 2015 inventivetalent
 *
 * @author inventivetalent
 */
public class NMSClass
{
	
	private static boolean init = false;
	protected static int version = 170;
	
	public static Class<?> PacketPlayOutPlayerListHeaderFooter;
	
	public static Class<?> IChatBaseComponent;
	public static Class<?> ChatSerializer;
	
	public static Class<?> PacketPlayOutPlayerInfo;
	public static Class<?> PlayerInfoData;
	protected static Class<?> EnumPlayerInfoAction;
	
	public static Class<?> GameProfile;
	public static Class<?> EnumGamemode;
	
	public static Class<?> TileEntitySkull;
	
	public static Class<?> LoadingCache;
	
	static
	{
		if(!init)
		{
			if(Reflection.getVersion().contains("1_7"))
			{
				version = 170;
			}
			if(Reflection.getVersion().contains("1_8"))
			{
				version = 180;
			}
			if(Reflection.getVersion().contains("1_8_R1"))
			{
				version = 181;
			}
			if(Reflection.getVersion().contains("1_8_R2"))
			{
				version = 182;
			}
			if(Reflection.getVersion().contains("1_8_R3"))
			{
				version = 183;
			}
			
			if(version >= 180)
			{
				PacketPlayOutPlayerListHeaderFooter = Reflection.getNMSClass("PacketPlayOutPlayerListHeaderFooter");
			}
			
			IChatBaseComponent = Reflection.getNMSClass("IChatBaseComponent");
			if(version < 181)
			{
				ChatSerializer = Reflection.getNMSClass("ChatSerializer");
			}
			else
			{
				ChatSerializer = Reflection.getNMSClass("IChatBaseComponent$ChatSerializer");
			}
			
			PacketPlayOutPlayerInfo = Reflection.getNMSClass("PacketPlayOutPlayerInfo");
			if(version >= 180)
			{
				PlayerInfoData = Reflection.getNMSClass("PacketPlayOutPlayerInfo$PlayerInfoData");
			}
			
			if(version <= 181)
			{
				EnumPlayerInfoAction = Reflection.getNMSClass("EnumPlayerInfoAction");
			}
			else
			{
				EnumPlayerInfoAction = Reflection.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
			}
			
			try
			{
				if(version < 180)
				{
					GameProfile = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
				}
				else
				{
					GameProfile = Class.forName("com.mojang.authlib.GameProfile");
				}
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			
			if(version < 182)
			{
				EnumGamemode = Reflection.getNMSClass("EnumGamemode");
			}
			else
			{
				EnumGamemode = Reflection.getNMSClass("WorldSettings$EnumGamemode");
			}
			
			TileEntitySkull = Reflection.getNMSClass("TileEntitySkull");
			
			try
			{
				if(version < 180)
				{
					LoadingCache = Class.forName("net.minecraft.util.com.google.common.cache.LoadingCache");
				}
				else
				{
					LoadingCache = Class.forName("com.google.common.cache.LoadingCache");
				}
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			
			init = true;
		}
	}
	
}
