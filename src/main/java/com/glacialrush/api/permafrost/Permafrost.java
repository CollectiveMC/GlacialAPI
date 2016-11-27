package com.glacialrush.api.permafrost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.component.Controller;

public class Permafrost extends Controller
{
	private static Permafrost inst;
	
	public Permafrost(GlacialPlugin pl)
	{
		super(pl);
		
		inst = this;
	}
	
	public static ArrayList<String> handle(File base, String request, String address)
	{
		ArrayList<String> rs = new ArrayList<String>();
		
		inst.o("Request from " + address + " >> " + request);
		File t = new File(base, request);
		
		if(t.exists())
		{
			FileInputStream fis;
			try
			{
				fis = new FileInputStream(t);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			 
				String line = null;
				while ((line = br.readLine()) != null) 
				{
					rs.add(line);
				}
				
				br.close();
			}
			
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return rs;
	}
}
