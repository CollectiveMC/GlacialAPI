package com.glacialrush.api.gui;

import com.glacialrush.api.object.GList;

public class Pane
{
	private final UI ui;
	private String title;
	private GList<Element> elements;
	
	public Pane(UI ui, String title)
	{
		this.ui = ui;
		this.title = title;
		this.elements = new GList<Element>();
		
		this.ui.add(this);
	}
	
	public Pane add(Element element)
	{
		elements.add(element);
		return this;
	}
	
	public Pane remove(Element element)
	{
		elements.remove(element);
		return this;
	}

	public String getTitle()
	{
		return title;
	}

	public Pane setTitle(String title)
	{
		this.title = title;
		return this;
	}

	public GList<Element> getElements()
	{
		return elements;
	}

	public Pane setElements(GList<Element> elements)
	{
		this.elements = elements;
		return this;
	}

	public UI getUi()
	{
		return ui;
	}
}
