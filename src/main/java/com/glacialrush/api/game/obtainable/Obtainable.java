package com.glacialrush.api.game.obtainable;

import com.glacialrush.api.game.obtainable.item.ObtainableType;

public class Obtainable
{
	private Integer cost;
	private String id;
	private String name;
	private String description;
	private ObtainableType obtainableType;
	private Boolean starter;
	private final ObtainableBank obtainableBank;
	
	public Obtainable(ObtainableBank obtainableBank)
	{
		this.obtainableBank = obtainableBank;
		this.cost = 0;
		this.id = IDGenerator.nextID(13, 37);
		this.name = "obtainable";
		this.description = "obtainable item";
		this.starter = false;
	}
	
	public ObtainableBank getObtainableBank()
	{
		return obtainableBank;
	}
	
	public Integer getCost()
	{
		return cost;
	}
	
	public void setCost(Integer cost)
	{
		this.cost = cost;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public ObtainableType getObtainableType()
	{
		return obtainableType;
	}
	
	public void setObtainableType(ObtainableType obtainableType)
	{
		this.obtainableType = obtainableType;
	}
	
	public Boolean getStarter()
	{
		return starter;
	}
	
	public void setStarter(Boolean starter)
	{
		this.starter = starter;
	}
}
