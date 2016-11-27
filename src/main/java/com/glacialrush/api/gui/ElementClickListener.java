package com.glacialrush.api.gui;

import org.bukkit.entity.Player;

public class ElementClickListener implements Runnable
{
	private Player player;
	private Element element;
	private Pane pane;
	private UI ui;
	
	public void close()
	{
		ui.close();
	}
	
	public void open(Pane pane)
	{
		ui.open(pane);
	}
	
	public void update()
	{
		ui.update();
	}
	
	public void run(Element element)
	{
		this.element = element;
		this.pane = element.getPane();
		this.ui = pane.getUi();
		this.player = ui.getPlayer();
		
		run();
	}
	
	@Override
	public void run()
	{
		
	}

	public Player getPlayer()
	{
		return player;
	}

	public Element getElement()
	{
		return element;
	}

	public Pane getPane()
	{
		return pane;
	}

	public UI getUi()
	{
		return ui;
	}
}
