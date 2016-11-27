package com.glacialrush.api.component;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.object.GList;

public class ComponentManager
{
	private GList<Component> components;
	private GlacialPlugin pl;
	
	public ComponentManager(GlacialPlugin pl)
	{
		this.pl = pl;
		this.components = new GList<Component>();
	}
	
	public void register(Component c)
	{
		components.add(c);
	}
	
	public void enable()
	{
		for(Component i : components)
		{
			i.preEnable();
		}
		
		for(Component i : components)
		{
			i.postEnable();
			pl.o("Enabled " + i.getClass().getSimpleName());
		}
	}
	
	public void disable()
	{
		for(Component i : components)
		{
			i.preDisable();
		}
		
		for(Component i : components)
		{
			i.postDisable();
			pl.o("Disabled " + i.getClass().getSimpleName());
		}
	}
}
