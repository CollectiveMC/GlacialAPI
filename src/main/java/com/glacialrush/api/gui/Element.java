package com.glacialrush.api.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import com.glacialrush.api.object.GList;

public class Element
{
	private final Pane pane;
	private String title;
	private Material material;
	private Byte data;
	private GList<String> lore;
	private Integer x;
	private Integer y;
	private Integer slot;
	private ElementClickListener onLeftClickListener;
	private ElementClickListener onRightClickListener;
	
	public Element(Pane pane, String title, Material material, Integer x, Integer y)
	{
		this.pane = pane;
		this.title = title;
		this.material = material;
		this.data = 0;
		this.lore = new GList<String>();
		this.x = x;
		this.y = y;
		this.slot = getPosition(x, y);
		
		this.pane.add(this);
	}
	
	public Element(Pane pane, String title, Material material, Integer slot)
	{
		this.pane = pane;
		this.title = title;
		this.material = material;
		this.data = 0;
		this.lore = new GList<String>();
		
		if(slot > (9 * 9) - 1)
		{
			slot = (9 * 9) - 1;
		}
		
		this.slot = slot;
		this.y = (int) ((slot / 9) + 1);
		this.x = (slot - ((y - 1) * 9)) - 4;
		this.slot = slot;
		
		this.pane.add(this);
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
	
	public ElementClickListener getOnLeftClickListener()
	{
		return onLeftClickListener;
	}

	public Element setOnLeftClickListener(ElementClickListener onLeftClickListener)
	{
		this.onLeftClickListener = onLeftClickListener;
		return this;
	}

	public ElementClickListener getOnRightClickListener()
	{
		return onRightClickListener;
	}

	public Element setOnRightClickListener(ElementClickListener onRightClickListener)
	{
		this.onRightClickListener = onRightClickListener;
		return this;
	}

	public Integer getSlot()
	{
		return slot;
	}

	public Integer getX()
	{
		return x;
	}
	
	public Element setX(Integer x)
	{
		this.x = x;
		this.slot = getPosition(x, y);
		return this;
	}
	
	public Integer getY()
	{
		return y;
	}
	
	public Element setY(Integer y)
	{
		this.y = y;
		this.slot = getPosition(x, y);
		return this;
	}
	
	public void remove()
	{
		this.pane.remove(this);
	}
	
	public Element setData(Byte data)
	{
		this.data = data;
		return this;
	}
	
	public Element addLore(String lore)
	{
		if(lore.length() > 64)
		{
			String format = ChatColor.getLastColors(lore);
			
			for(String i : StringUtils.split(WordUtils.wrap(lore, 24), '\n'))
			{
				this.lore.add(format + i.replace('\r', ' '));
			}
			
			return this;
		}
		
		this.lore.add(lore);
		return this;
	}
	
	public Element clearLore()
	{
		lore.clear();
		return this;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public Element setTitle(String title)
	{
		this.title = title;
		return this;
	}
	
	public GList<String> getLore()
	{
		return lore;
	}
	
	public Element setLore(GList<String> lore)
	{
		this.lore = lore;
		return this;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public Element setMaterial(Material material)
	{
		this.material = material;
		return this;
	}
	
	public Byte getData()
	{
		return data;
	}
	
	public Pane getPane()
	{
		return pane;
	}
}
