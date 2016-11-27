package com.glacialrush.api.gui;

import org.bukkit.Material;
import com.glacialrush.api.object.GList;

public class Shortcut
{
	private String title;
	private Material material;
	private Byte data;
	private GList<String> lore;
	private Integer x;
	private Integer y;
	private Integer slot;
	private Integer ix;
	private Integer iy;
	private Integer islot;
	private ShortcutLaunchListener shortcutLaunchListener;
	
	public Shortcut(String title, Material material, Integer x, Integer y)
	{
		this.title = title;
		this.material = material;
		this.data = 0;
		this.lore = new GList<String>();
		this.x = x;
		this.y = y;
		this.ix = x;
		this.iy = y;
		this.slot = getPosition(x, y);
		this.islot = slot;
	}
	
	public Shortcut onShortcutLaunch(ShortcutLaunchListener shortcutLaunchListener)
	{
		this.shortcutLaunchListener = shortcutLaunchListener;
		return this;
	}
	
	public void launch(UI ui)
	{
		if(shortcutLaunchListener != null)
		{
			shortcutLaunchListener.run(new Pane(ui, title));
		}
	}
	
	private Integer getPosition(Integer x, Integer y)
	{
		if(x > 4)
		{
			x = 4;
			this.x = 4;
		}
		
		if(x < -4)
		{
			x = -4;
			this.x = -4;
		}
		
		if(y > 9)
		{
			y = 9;
			this.y = 9;
		}
		
		if(y < 1)
		{
			y = 1;
			this.y = 1;
		}
		
		return ((y - 1) * 9) + (x + 4);
	}
	
	private Integer getIPosition(Integer x, Integer y)
	{
		if(x > 4)
		{
			x = 4;
			this.ix = 4;
		}
		
		if(x < -4)
		{
			x = -4;
			this.ix = -4;
		}
		
		if(y > 4)
		{
			y = 4;
			this.iy = 4;
		}
		
		if(y < 1)
		{
			y = 1;
			this.iy = 1;
		}
		
		return ((y - 1) * 9) + (x + 4);
	}
	
	public Integer getSlot()
	{
		return slot;
	}
	
	public Integer getX()
	{
		return x;
	}
	
	public Shortcut setX(Integer x)
	{
		this.x = x;
		this.slot = getPosition(x, y);
		return this;
	}
	
	public Integer getY()
	{
		return y;
	}
	
	public Shortcut setY(Integer y)
	{
		this.y = y;
		this.slot = getPosition(x, y);
		return this;
	}
	
	public Integer getIX()
	{
		return ix;
	}
	
	public Shortcut setIX(Integer x)
	{
		this.ix = x;
		this.islot = getIPosition(x, iy);
		return this;
	}
	
	public Integer getIY()
	{
		return iy;
	}
	
	public Shortcut setIY(Integer y)
	{
		this.iy = y;
		this.islot = getIPosition(ix, y);
		return this;
	}
	
	public Shortcut setData(Byte data)
	{
		this.data = data;
		return this;
	}
	
	public Integer getISlot()
	{
		return islot;
	}

	public Shortcut addLore(String lore)
	{
		this.lore.add(lore);
		return this;
	}
	
	public Shortcut clearLore()
	{
		lore.clear();
		return this;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public Shortcut setTitle(String title)
	{
		this.title = title;
		return this;
	}
	
	public GList<String> getLore()
	{
		return lore;
	}
	
	public Shortcut setLore(GList<String> lore)
	{
		this.lore = lore;
		return this;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public Shortcut setMaterial(Material material)
	{
		this.material = material;
		return this;
	}
	
	public Byte getData()
	{
		return data;
	}
}
