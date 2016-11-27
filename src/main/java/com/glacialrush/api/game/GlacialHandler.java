package com.glacialrush.api.game;

import org.bukkit.event.Listener;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.Dispatcher;
import com.glacialrush.api.dispatch.InnerDispatcher;

public class GlacialHandler implements GameHandler, Listener
{
	protected final GameController gameController;
	protected final Game game;
	protected final InnerDispatcher d;
	
	public GlacialHandler(Game game)
	{
		this.game = game;
		this.gameController = game.getGameController();
		this.d = new InnerDispatcher(GlacialPlugin.instance(), getName());
		game.registerHandler(this);
		game.pl.register(this);
	}
	
	@Override
	public void start(GameState state)
	{
	
	}
	
	@Override
	public void tick(GameState state)
	{
	
	}
	
	@Override
	public void stop(GameState state)
	{
	
	}
	
	public boolean equals(Object o)
	{
		if(o != null)
		{
			if(o instanceof GlacialHandler)
			{
				if(((GlacialHandler)o).getName().equals(getName()))
				{
					return true;
				}
			}
		}
		
		return false;
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
	
	public GameController getGameController()
	{
		return gameController;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public InnerDispatcher getD()
	{
		return d;
	}
}
