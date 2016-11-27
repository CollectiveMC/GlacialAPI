package com.glacialrush.api.component;

import com.glacialrush.api.GPlugin;

@Deprecated
public class GlacialComponent implements Component
{
	protected GPlugin pl;
	private boolean enabled;
	
	public GlacialComponent(GPlugin pl)
	{
		this.pl = pl;
		this.enabled = false;
	}
	
	@Override
	public void preEnable()
	{
		
	}

	@Override
	public void postEnable()
	{
		this.enabled = true;
	}

	@Override
	public void preDisable()
	{
		
	}

	@Override
	public void postDisable()
	{
		this.enabled = false;
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
		return this.getClass().getSimpleName();
	}
}
