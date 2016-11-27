package com.glacialrush.api.gui;

import org.bukkit.entity.Player;

public class ShortcutLaunchListener implements Runnable
{
	private Pane pane;
	private Player player;
	private Boolean cancelled;
	
	public void run(Pane pane)
	{
		this.pane = pane;
		this.cancelled = false;
		this.player = pane.getUi().getPlayer();
		getPane().getUi().close();
		getPane().getUi().getPanes().clear();
		
		run();
		
		if(isCancelled())
		{
			return;
		}
		
		getPane().getUi().open(getPane());
	}
	
	public void cancel()
	{
		cancelled = true;
	}
	
	public Boolean isCancelled()
	{
		return cancelled;
	}

	public void setCancelled(Boolean cancelled)
	{
		this.cancelled = cancelled;
	}

	public void update()
	{
		getPane().getUi().update();
	}
	
	public void run()
	{
		
	}

	public Pane getPane()
	{
		return pane;
	}

	public Player getPlayer()
	{
		return player;
	}
}
