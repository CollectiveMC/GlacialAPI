package com.glacialrush.api.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class TrainingGame extends Game
{
	public TrainingGame(GameController gameController)
	{
		super(gameController);
	}
	
	public void start()
	{
		pl.scheduleSyncRepeatingTask(0, 20, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player i : TrainingGame.this.players())
				{
					updateBoard(i);
				}
			}
		});
	}
	
	public void stop()
	{
		
	}
	
	public void join(Player p)
	{
		super.join(p);
		p.setAllowFlight(false);
		p.getInventory().clear();
		p.setGameMode(GameMode.SPECTATOR);
		gameController.gpo(p).disable();
	}
	
	public void leave(Player p)
	{
		super.leave(p);
		p.getInventory().clear();
		pl.getUiController().moveShortcuts(p, false);
		p.setGameMode(GameMode.ADVENTURE);
		p.teleport(pl.getServerDataComponent().getHub());
		gameController.gpo(p).enable();
	}
	
	public void updateBoard(Player p)
	{
		boardController.remove(p);
		
		boardController.update(p);
	}
}
