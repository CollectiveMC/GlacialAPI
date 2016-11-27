package com.glacialrush.api.game.event;

import com.glacialrush.api.map.region.Edge;

public class EdgeEvent extends RegionEvent
{
	private final Edge edge;
	
	public EdgeEvent(Edge edge)
	{
		super(edge);
		
		this.edge = edge;
	}
	
	public Edge getEdge()
	{
		return edge;
	}
}
