package com.glacialrush.api.game.obtainable;

import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.InnerDispatcher;
import com.glacialrush.api.object.GList;

public class ObtainableBank
{
	private final GList<Obtainable> items;
	private final InnerDispatcher d;
	private final GlacialPlugin pl;
	private final ObtainableFilter obtainableFilter;
	private final ObtainableDataManager obtainableDataManager;
	
	public ObtainableBank(GlacialPlugin pl)
	{
		this.pl = pl;
		this.items = new GList<Obtainable>();
		this.d = new InnerDispatcher(pl, "ItemBank");
		this.obtainableFilter = new ObtainableFilter(this);
		this.obtainableDataManager = new ObtainableDataManager(pl, obtainableFilter);
		
		obtainableDataManager.load();
		obtainableDataManager.generateDemo();
		
		for(Obtainable o : items)
		{
			d.overbose("Register: " + o.getId());
		}
	}
	
	public Obtainable resolve(String id)
	{
		for(Obtainable i : items)
		{
			if(i.getId().equals(id))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void stop()
	{
		obtainableDataManager.save();
	}
	
	public ObtainableDataManager getObtainableDataManager()
	{
		return obtainableDataManager;
	}
	
	public void add(Obtainable o)
	{
		items.add(o);
	}
	
	public ObtainableFilter getObtainableFilter()
	{
		return obtainableFilter;
	}
	
	public GList<Obtainable> getItems()
	{
		return items;
	}
	
	public InnerDispatcher getD()
	{
		return d;
	}
	
	public GlacialPlugin getPl()
	{
		return pl;
	}
}
