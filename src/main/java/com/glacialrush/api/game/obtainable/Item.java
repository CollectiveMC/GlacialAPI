package com.glacialrush.api.game.obtainable;

import org.bukkit.Material;

import com.glacialrush.api.game.obtainable.item.ItemType;
import com.glacialrush.api.game.obtainable.item.ObtainableType;

public class Item extends Obtainable
{
	private ItemType itemType;
	private Material material;
	private Byte materialMeta;
	
	public Item(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setObtainableType(ObtainableType.ITEM);
		setMaterial(Material.AIR);
		setMaterialMeta((byte) 0);
	}
	
	public ItemType getItemType()
	{
		return itemType;
	}
	
	public void setItemType(ItemType itemType)
	{
		this.itemType = itemType;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public void setMaterial(Material material)
	{
		this.material = material;
	}
	
	public Byte getMaterialMeta()
	{
		return materialMeta;
	}
	
	public void setMaterialMeta(Byte materialMeta)
	{
		this.materialMeta = materialMeta;
	}
}
