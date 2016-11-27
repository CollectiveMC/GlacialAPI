package com.glacialrush.api.component;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.RegionedGame;

public class ChunkController extends Controller
{
	
	public ChunkController(GlacialPlugin pl)
	{
		super(pl);
	}
	
	public boolean check(Chunk c)
	{
		for(Game i : pl.gameControl.getGames())
		{
			if(i.isRunning())
			{
				if(i.getType().equals(GameType.REGIONED))
				{
					if(((RegionedGame)i).getMap().contains(c))
					{
						return true;
					}
				}
			}
		}
		
		if(pl.getServerDataComponent().getHub() != null && pl.getServerDataComponent().getHub().getChunk().equals(c))
		{
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	public void chunkUnload(ChunkUnloadEvent e)
	{
		e.setCancelled(check(e.getChunk()));
	}
}
