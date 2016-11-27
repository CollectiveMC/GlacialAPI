package com.glacialrush.api.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.InnerDispatcher;
import com.glacialrush.api.object.GMap;

public class DataComponent<T>
{
	private File base;
	private GMap<File, T> cache;
	private String targetExtention;
	private InnerDispatcher d;
	
	public DataComponent(GlacialPlugin pl, File base, String targetExtention)
	{
		this.base = base;
		this.cache = new GMap<File, T>();
		this.targetExtention = targetExtention.toLowerCase();
		this.d = new InnerDispatcher(pl, "DataComponent<" + targetExtention + ">");
		verify(base);
	}
	
	public void loadAll()
	{
		for(File i : base.listFiles())
		{
			if(i.getName().endsWith(targetExtention))
			{
				try
				{
					load(i);
				}
				
				catch(IOException e)
				{
					d.failure("File could not be loaded.");
				}
			}
		}
	}
	
	public void saveAll()
	{
		flush();
		clearCache();
	}
	
	public void flush()
	{
		for(File i : cache.keySet())
		{
			try
			{
				save(i, cache.get(i));
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void track(File f, T t)
	{
		cache.put(f, t);
	}
	
	public void clearCache()
	{
		cache.clear();
	}
	
	public void save(File f, T t) throws IOException
	{
		verifyFile(f);
		
		FileOutputStream fos = new FileOutputStream(f);
		GZIPOutputStream gzo = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gzo);
		
		oos.writeObject(t);
		
		oos.close();
	}
	
	public void load(File f) throws IOException
	{
		FileInputStream fin = new FileInputStream(f);
		GZIPInputStream gzi = new GZIPInputStream(fin);
		ObjectInputStream ois = new ObjectInputStream(gzi);
		
		try
		{
			Object object = ois.readObject();
			
			if(object != null)
			{
				@SuppressWarnings("unchecked")
				T t = (T) object;
				
				if(t != null)
				{
					cache.put(f, t);
					d.success("Loaded " + f.getPath());
				}
			}
			
			else
			{
				d.failure("Failed to load " + f.getPath() + ", OBJECT IS NULL");
			}
		}
		
		catch(Exception e)
		{
			d.failure("Failed to load " + f.getPath() + ", " + e.getClass().getSimpleName());
			e.printStackTrace();
		}
		
		ois.close();
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
		verify(file.getParentFile());
		
		if(!file.exists())
		{
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

	public File getBase()
	{
		return base;
	}

	public void setBase(File base)
	{
		this.base = base;
	}

	public GMap<File, T> getCache()
	{
		return cache;
	}

	public void setCache(GMap<File, T> cache)
	{
		this.cache = cache;
	}

	public String getTargetExtention()
	{
		return targetExtention;
	}

	public void setTargetExtention(String targetExtention)
	{
		this.targetExtention = targetExtention;
	}

	public InnerDispatcher getD()
	{
		return d;
	}

	public void setD(InnerDispatcher d)
	{
		this.d = d;
	}
}
