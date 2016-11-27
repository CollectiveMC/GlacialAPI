package com.glacialrush.api.game;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.handler.MapHandler;
import com.glacialrush.api.game.handler.ThreadHandler;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.scoreboard.BoardController;
import com.glacialrush.api.thread.GlacialThread;
import org.bukkit.ChatColor;

public class Game implements Listener
{
	protected final GlacialPlugin pl;
	protected final GameState state;
	protected final GameController gameController;
	protected final GList<GlacialHandler> handlers;
	protected final MapHandler mapHandler;
	protected BoardController boardController;
	protected GameType type;
	protected UUID uuid;
	
	protected ThreadHandler threadHandler;
	
	protected boolean running;
	
	public Game(GameController gameController)
	{
		this.pl = GlacialPlugin.instance();
		this.state = new GameState(this);
		this.gameController = gameController;
		this.handlers = new GList<GlacialHandler>();
		this.boardController = new BoardController(ChatColor.AQUA + "Glacial Rush", pl);
		this.uuid = UUID.randomUUID();
		
		running = false;
		
		threadHandler = new ThreadHandler(this);
		mapHandler = new MapHandler(this);
		
		pl.register(this);
	}
	
	public GlacialPlugin getPl()
	{
		return pl;
	}
	
	public UUID getUuid()
	{
		return uuid;
	}
	
	public boolean equals(Object o)
	{
		if(o == null)
		{
			return false;
		}
		
		if(o instanceof Game)
		{
			if(((Game) o).getState().getPlayers().equals(state.players))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public GlacialPlugin pl()
	{
		return pl;
	}
	
	public void join(Player p)
	{
		if(!state.getPlayers().contains(p))
		{
			if(gameController.gpo(p).getDisabled())
			{
				return;
			}
			
			state.getPlayers().add(p);
		}
	}
	
	public void leave(Player p)
	{
		if(state.getPlayers().contains(p))
		{
			state.getPlayers().remove(p);
		}
		
		boardController.remove(p);
		gameController.left(p, this);
	}
	
	public boolean contains(Player p)
	{
		return state.getPlayers().contains(p);
	}
	
	public GList<Player> players()
	{
		return state.getPlayers();
	}
	
	public void start()
	{
		running = true;
		
		for(GlacialHandler i : handlers)
		{
			i.start(state);
		}
		
		threadHandler.newThread(new GlacialThread("Game Handler Clock")
		{
			@Override
			public void run()
			{
				for(GlacialHandler i : handlers)
				{
					try
					{
						i.tick(state);
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public BoardController getBoardController()
	{
		return boardController;
	}
	
	public void setBoardController(BoardController boardController)
	{
		this.boardController = boardController;
	}
	
	public GameType getType()
	{
		return type;
	}
	
	public void setType(GameType type)
	{
		this.type = type;
	}
	
	public void stop()
	{
		running = false;
		
		for(GlacialHandler i : handlers)
		{
			i.stop(state);
		}
		
		handlers.clear();
	}
	
	public void registerHandler(GlacialHandler handler)
	{
		for(GlacialHandler i : handlers.copy())
		{
			if(i.getName().equals(handler.getName()))
			{
				handlers.remove(i);
				System.out.println("Removed old handler (" + i.getName() + ")");
			}
		}
		
		handlers.add(handler);
	}
	
	public GameController getGameController()
	{
		return gameController;
	}
	
	public GameState getState()
	{
		return state;
	}
	
	public GList<GlacialHandler> getHandlers()
	{
		return handlers;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public ThreadHandler getThreadHandler()
	{
		return threadHandler;
	}
	
	public MapHandler getMapHandler()
	{
		return mapHandler;
	}
	
	public void setThreadHandler(ThreadHandler threadHandler)
	{
		this.threadHandler = threadHandler;
	}
}
