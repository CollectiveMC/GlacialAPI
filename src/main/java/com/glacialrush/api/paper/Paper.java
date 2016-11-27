package com.glacialrush.api.paper;

import java.awt.image.BufferedImage;

public class Paper
{
	private byte[][] image;
	
	public Paper()
	{
		image = new byte[128][128];
		
		for(int y = 0; y < 128; y++)
		{
			for(int x = 0; x < 128; x++)
			{
				image[x][y] = PaperColor.WHITE;
			}
		}
	}
	
	public void set(int x, int y, byte color)
	{
		image[x][y] = color;
	}

	public byte[][] getImage()
	{
		return image;
	}

	public void setImage(byte[][] image)
	{
		this.image = image;
	}
	
	public void setImage(BufferedImage image)
	{
		for(int y = 0; y < 128; y++)
		{
			for(int x = 0; x < 128; x++)
			{
				this.image[x][y] = PaperColor.matchColor(PaperColor.getColor((byte) image.getRGB(x, y)));
			}
		}
	}
	
	public void setImage(byte[] image)
	{
		int ix = 0;
		
		for(int y = 0; y < 128; y++)
		{
			for(int x = 0; x < 128; x++)
			{
				this.image[x][y] = image[ix];
				ix++;
			}
		}
	}
}
