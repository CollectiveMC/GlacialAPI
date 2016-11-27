package com.glacialrush.api.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ClassUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.object.GList;
import com.glacialrush.xapi.Reflector;

public class StaticConfiguration
{
	private GlacialPlugin pl;
	private Class<? extends Configuration> type;
	private File file;
	
	public StaticConfiguration(GlacialPlugin pl, Class<? extends Configuration> type, File file)
	{
		this.pl = pl;
		this.type = type;
		this.file = file;
		
		verifyFile(file);
	}
	
	public Object load()
	{
		FileConfiguration fc = new YamlConfiguration();
		Object object = null;
		
		try
		{
			fc.load(file);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		try
		{
			object = type.newInstance();
			
			if(object == null)
			{
				return null;
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		for(Field i : type.getDeclaredFields())
		{
			if(isSafe(i))
			{
				if(i.getType().equals(GList.class))
				{
					List<?> list = fc.getList(i.getName());
					GList<String> slist = new GList<String>();
					
					for(Object j : list)
					{
						slist.add(j.toString());
					}
					
					set(object, slist, i);
				}
				
				else
				{
					if(i.getType().equals(String.class))
					{
						set(object, fc.getString(i.getName()), i);
					}
					
					else if(i.getType().equals(Boolean.class))
					{
						set(object, fc.getBoolean(i.getName()), i);
					}
					
					else if(i.getType().equals(Integer.class))
					{
						set(object, fc.getInt(i.getName()), i);
					}
					
					else if(i.getType().equals(Double.class))
					{
						set(object, fc.getDouble(i.getName()), i);
					}
					
					else if(i.getType().equals(Float.class))
					{
						set(object, new Double(fc.getDouble(i.getName())).floatValue(), i);
					}
					
					else if(i.getType().equals(Long.class))
					{
						set(object, fc.getLong(i.getName()), i);
					}
					
					else if(i.getType().equals(Byte.class))
					{
						set(object, Byte.parseByte(fc.getString(i.getName())), i);
					}
					
					else if(i.getType().equals(Short.class))
					{
						set(object, Short.parseShort(fc.getString(i.getName())), i);
					}
				}
			}
		}
		
		return object;
	}
	
	public void save(Object o)
	{
		for(Method i : type.getDeclaredMethods())
		{
			pl.s(i.getName());
		}
		
		if(!o.getClass().equals(type))
		{
			pl.f("Failed to save " + file.getPath() + " !! " + o.getClass().toString() + " to the type: " + type.toString());
			return;
		}
		
		FileConfiguration fc = new YamlConfiguration();
		
		for(Field i : o.getClass().getDeclaredFields())
		{
			if(isSafe(i))
			{
				if(i.getType().equals(GList.class))
				{
					List<String> list = new ArrayList<String>();
					
					for(Object j : (GList<?>) get(o, i))
					{
						list.add(j.toString());
					}
					
					fc.set(i.getName(), list);
				}
				
				else
				{
					fc.set(i.getName(), get(o, i));
				}
			}
		}
		
		try
		{
			fc.save(file);
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Object get(Object o, Field field)
	{
		Reflector r = new Reflector();
		
		try
		{
			Method getter = r.getGetter(field);
			
			return getter.invoke(o);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void set(Object o, Object v, Field field)
	{
		Reflector r = new Reflector();
		
		try
		{
			Method setter = r.getSetter(field);
			
			pl.w(field.getName());
			
			setter.invoke(o, v);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isSafe(Field field)
	{
		Class<?> t = field.getType();
				
		if(t.isArray())
		{
			pl.f(type.getSimpleName() + ": " + t.getSimpleName() + " " + field.getName() + " Use GList<?> not arrays.");
			return false;
		}
		
		if(ClassUtils.isPrimitiveOrWrapper(t) || t.equals(String.class))
		{
			if(ClassUtils.isPrimitiveWrapper(t) || t.equals(String.class))
			{
				if(hasGettersSetters(field))
				{
					return true;
				}
				
				else
				{
					return false;
				}
			}
			
			else
			{
				pl.f(type.getSimpleName() + ": " + t.getSimpleName() + " " + field.getName() + " is primitave!");
				return false;
			}
		}
		
		else if(t.equals(GList.class))
		{
			if(hasGettersSetters(field))
			{
				return true;
			}
			
			else
			{
				return false;
			}
		}
		
		else
		{
			pl.f(type.getSimpleName() + ": " + t.getSimpleName() + " " + field.getName() + " is not savable!");
			return false;
		}
	}
	
	public boolean hasGettersSetters(Field field)
	{
		Reflector r = new Reflector();
		
		Method getter = r.getGetter(field);
		
		if(getter == null)
		{
			pl.f(type.getSimpleName() + ": " + field.getType().getSimpleName() + " " + field.getName() + " no getter found!");
			return false;
		}
		
		Method setter = r.getSetter(field);
		
		if(setter == null)
		{
			pl.f(type.getSimpleName() + ": " + field.getType().getSimpleName() + " " + field.getName() + " no setter found!");
			return false;
		}
		
		return true;
	}
	
	public void verify(File file)
	{
		if(!file.exists())
		{
			file.mkdirs();
		}
	}
	
	public void verifyFile(File file)
	{
		if(!file.exists())
		{
			verify(file.getParentFile());
			
			try
			{
				file.createNewFile();
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
