package com.glacialrush.api.dispatch;

import org.phantomapi.util.D;
import com.glacialrush.api.GlacialPlugin;

public class InnerDispatcher extends Dispatcher
{
	protected String name;
	private D d;
	
	public InnerDispatcher(GlacialPlugin pl, String name)
	{
		super(pl);
		
		this.name = name;
		this.d = new D(name);
	}

	@Override
	public void log(String s, String... o)
	{
		
	}
	
	public void info(String... o)
	{
		d.i(o);
	}

	public void success(String... o)
	{
		d.s(o);
	}

	public void failure(String... o)
	{
		d.f(o);
	}

	public void warning(String... o)
	{
		d.w(o);
	}

	public void verbose(String... o)
	{
		d.v(o);
	}

	public void overbose(String... o)
	{
		d.o(o);
	}

	public void sinfo(String... o)
	{
		d.i(o);
	}

	public void ssuccess(String... o)
	{
		d.s(o);
	}

	public void sfailure(String... o)
	{
		d.f(o);
	}

	public void swarning(String... o)
	{
		d.w(o);
	}

	public void sverbose(String... o)
	{
		d.v(o);
	}

	public void soverbose(String... o)
	{
		d.o(o);
	}
}
