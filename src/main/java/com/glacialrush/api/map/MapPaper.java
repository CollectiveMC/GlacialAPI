package com.glacialrush.api.map;

import java.awt.image.BufferedImage;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.map.region.LinkedRegion;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.paper.Paper;
import com.glacialrush.api.paper.PaperColor;

public class MapPaper
{
	private Map map;
	private BufferedImage image;
	private Integer width;
	private Integer height;
	private Integer ox;
	private Integer oz;
	private Paper paper;
	private Chunklet min;
	private GMap<Region, GList<Chunklet>> chunklets;
	
	public MapPaper(Map map)
	{
		this.map = map;
		this.chunklets = new GMap<Region, GList<Chunklet>>();
		this.paper = new Paper();
	}
	
	public void build()
	{
		width = map.getWidth();
		height = map.getHeight();
		chunklets = new GMap<Region, GList<Chunklet>>();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		min = map.getChunkletMin();
		ox = -min.getX();
		oz = -min.getZ();
		
		for(Region i : map.regions)
		{
			chunklets.put(i, new GList<Chunklet>());
			
			for(Chunklet j : i.chunklets)
			{
				chunklets.get(i).add(j);
			}
		}
		
		renderAll();
	}
	
	public void renderAll()
	{
		for(Region i : map.regions)
		{
			render(i);
		}
	}
	
	public void render(Region region)
	{
		int c = 0;
		
		if(region.getType().equals(RegionType.TERRITORY) || region.getType().equals(RegionType.VILLAGE))
		{
			c = ((LinkedRegion) region).getFaction().getDyeColor().getColor().asRGB();
		}
		
		else
		{
			c = Faction.neutral().getDyeColor().getColor().asRGB();
		}
		
		for(Chunklet i : chunklets.get(region))
		{
			try
			{
				image.setRGB(i.getX() + ox, i.getZ() + oz, c);
			}
			
			catch(Exception e)
			{
				
			}
		}
		
		paper.setImage(PaperColor.resizeImage(image));
	}
	
	public Paper toPaper()
	{
		return paper;
	}
}
