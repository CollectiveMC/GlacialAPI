package com.glacialrush.api.object;

public enum BuycraftPackage
{
	TEST("Test Package", "8c7097430f6f", "", new PlayerRunnable()
	{
		public void run()
		{
			getPlayer().sendMessage("Tested!");
		}
	});
	
	private String name;
	private String id;
	private String url;
	private PlayerRunnable runnable;
	
	private BuycraftPackage(String name, String id, String url, PlayerRunnable runnable)
	{
		this.name = name;
		this.id = id;
		this.url = url;
		this.runnable = runnable;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public PlayerRunnable getRunnable()
	{
		return runnable;
	}
}
