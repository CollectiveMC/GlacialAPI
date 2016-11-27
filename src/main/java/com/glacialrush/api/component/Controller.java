package com.glacialrush.api.component;

import org.bukkit.event.Listener;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.Dispatcher;
import com.glacialrush.api.dispatch.InnerDispatcher;

public class Controller implements Component, Listener
{
	protected GlacialPlugin pl;
	protected InnerDispatcher d;
	protected boolean enabled;
	
	public Controller(GlacialPlugin pl)
	{
		this.pl = pl;
		this.d = new InnerDispatcher(pl, getName());
		
		pl.cregister(this);
	}
	
	public void preEnable()
	{
		
	}

	public void preDisable()
	{
		pl.unRegister(this);
	}
	
	public void postEnable()
	{
		pl.register(this);
	}
	
	public void postDisable()
	{
		
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public String getName()
	{
		return getClass().getSimpleName();
	}
	
	public Dispatcher getDispatcher()
	{
		return d;
	}
	
	public void i(String... o)
	{
		d.info(o);
	}
	
	public void s(String... o)
	{
		d.success(o);
	}
	
	public void f(String... o)
	{
		d.failure(o);
	}
	
	public void w(String... o)
	{
		d.warning(o);
	}
	
	public void v(String... o)
	{
		d.verbose(o);
	}
	
	public void o(String... o)
	{
		d.overbose(o);
	}
	
	public void si(String... o)
	{
		d.sinfo(o);
	}
	
	public void ss(String... o)
	{
		d.ssuccess(o);
	}
	
	public void sf(String... o)
	{
		d.sfailure(o);
	}
	
	public void sw(String... o)
	{
		d.swarning(o);
	}
	
	public void sv(String... o)
	{
		d.sverbose(o);
	}
	
	public void so(String... o)
	{
		d.soverbose(o);
	}
}
