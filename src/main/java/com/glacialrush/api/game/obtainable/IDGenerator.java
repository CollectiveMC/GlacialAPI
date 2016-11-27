package com.glacialrush.api.game.obtainable;

import java.util.Random;

public class IDGenerator
{
	private static final char[] symbols;
	private static Random r;
	
	static
	{
		StringBuilder tmp = new StringBuilder();
		
		for(char ch = '0'; ch <= '9'; ++ch)
		{
			tmp.append(ch);
		}
		
		for(char ch = 'a'; ch <= 'z'; ++ch)
		{
			tmp.append(ch);
		}
		
		symbols = tmp.toString().toCharArray();
		r = new Random();
	}
			
	public static String gen(int length)
	{
		r.setSeed(r.nextLong());
		
		char[] buf = new char[length];
		
		for(int idx = 0; idx < buf.length; ++idx)
		{
			buf[idx] = symbols[r.nextInt(symbols.length)];
		}
		
		return new String(buf);
	}
	
	public static String nextID(int... bounds)
	{
		String s = "";
		
		for(int i : bounds)
		{
			s = s + "-" + gen(i);
		}
		
		return s.substring(1);
	}
}