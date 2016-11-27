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

import org.bukkit.entity.Player;

/**
 *
 * © Copyright 2015 inventivetalent
 *
 * @author inventivetalent
 *		
 */
public class Util
{
	
	public static Object serializeChat(String msg)
	{
		try
		{
			return NMSClass.ChatSerializer.getDeclaredMethod("a", String.class).invoke(null, msg);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void sendPacket(Player p, Object packet) throws Exception
	{
		if(p == null || packet == null)
			return;
		Object handle = Reflection.getHandle(p);
		Object connection = AccessUtil.setAccessible(handle.getClass().getDeclaredField("playerConnection")).get(handle);
		Reflection.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, new Object[] {packet});
	}
	
}
