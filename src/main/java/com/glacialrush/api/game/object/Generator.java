package com.glacialrush.api.game.object;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.map.RegionType;
import com.glacialrush.api.map.region.Territory;
import com.glacialrush.api.sfx.Audio;

public class Generator
{
	private Location location;
	private Territory territory;
	private Map map;
	private Game game;
	private boolean stable;
	private int percent;
	private DyeColor col;
	
	public Generator(Location location, Territory territory)
	{
		this.location = location;
		this.territory = territory;
		this.map = territory.getMap();
		this.game = map.getGame();
		this.stable = true;
		this.percent = 100;
	}
	
	public void build()
	{
		Job j = new Job("Warpgate Generator Build", game);
		
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getLocation(), Material.DRAGON_EGG));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.NORTH).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.SOUTH).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), Material.STAINED_GLASS));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), Material.STAINED_GLASS_PANE));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), Material.STAINED_GLASS_PANE));
		
		j.flush();
	}
	
	public void stabilize()
	{
		stable = true;
		color(DyeColor.CYAN);
	}
	
	public void destabilize()
	{
		stable = false;
		color(DyeColor.LIGHT_BLUE);
	}
	
	public void color(DyeColor d)
	{
		if(col != null && col.equals(d))
		{
			return;
		}
		
		if(col == null)
		{
			col = d;
		}
		
		col = d;
		
		Job j = new Job("Warpgate Generator Color " + territory.getName(), game);
		
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.NORTH).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.SOUTH).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.EAST).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.WEST).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation(), d));
		j.queue(new Operation(location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation(), d));
		
		j.flush();
	}
	
	public void update(RegionedGame game)
	{
		if(stable)
		{
			color(DyeColor.CYAN);
			
			Faction c = territory.getFaction();
			Boolean s = false;
			int not = 0;
			int is = 0;
			
			for(Region i : territory.getBorders())
			{
				if(i.getType().equals(RegionType.TERRITORY))
				{
					if(((Territory) i).getFaction().equals(c))
					{
						is++;
					}
					
					else
					{
						not++;
					}
				}
			}
			
			if(is > not)
			{
				s = true;
			}
			
			if(!s)
			{
				destabilize();
				
				game.pl().scheduleSyncTask(20, new Runnable()
				{
					@Override
					public void run()
					{
						for(int j = 0; j < 1 + (game.getFactionHandler().getPlayers(territory.getFaction()).size() / 2); j++)
						{
							game.getPaladinHandler().releasePaladin(territory.getFaction(), territory);
							game.pl().s("Spawning Paladin for " + territory.getFaction() + " #" + territory.getName());
						}
						
						Notification n = NotificationPreset.PALADINS.format(null, new Object[] {territory.getFaction().getColor() + "" + territory.getFaction().getName()}, null);
						
						for(Player j : game.players())
						{
							game.getNotificationHandler().queue(j, n);
						}
					}
				});
			}
		}
		
		else
		{
			Faction c = territory.getFaction();
			Boolean s = false;
			Faction f = Faction.neutral();
			int not = 0;
			int is = 0;
			
			for(Region i : territory.getBorders())
			{
				if(i.getType().equals(RegionType.TERRITORY))
				{
					if(((Territory) i).getFaction().equals(c))
					{
						is++;
					}
					
					else
					{
						not++;
						
						if(f.equals(Faction.neutral()))
						{
							f = ((Territory)i).getFaction();
						}
					}
				}
			}
			if(is > not)
			{
				s = true;
			}
			
			if(!s)
			{
				if(!f.equals(Faction.neutral()))
				{
					percent -= 1;
					
					if(!map.getPl().getServerDataComponent().isProduction())
					{
						percent -= 9;
					}
					
					if(percent <= 0)
					{
						game.getPaladinHandler().remove(territory.getFaction());
						territory.setFaction(f);
						territory.accent(territory.getFaction().getDyeColor());
						stabilize();
						percent = 100;
						color(DyeColor.CYAN);
						
						for(Player i : game.players())
						{
							Audio.clear(i);
						}
						
						game.getGameController().o(f.getColor() + f.getName() + " >> " + c.getColor() + c.getName());
						
						for(Player i : game.players())
						{
							if(game.getFactionHandler().getFaction(i).equals(c))
							{
								game.getNotificationHandler().queue(i, NotificationPreset.WARPGATE_LOST.format(new Object[] {f.getColor() + ""}, new Object[] {f.getColor() + ""}, null));
							}
							
							if(game.getFactionHandler().getFaction(i).equals(f))
							{
								game.getNotificationHandler().queue(i, NotificationPreset.WARPGATE_CAPTURED.format(new Object[] {f.getColor() + ""}, new Object[] {f.getColor() + ""}, null));
								game.getExperienceHandler().giveXp(i, (long) 1000, Experience.CAPTURE_WARPGATE);
							}
						}
					}
					
					else if(percent <= 10)
					{
						color(DyeColor.BLACK);
					}
					
					else if(percent <= 20)
					{
						color(DyeColor.GRAY);
					}
					
					else if(percent <= 30)
					{
						color(DyeColor.BROWN);
					}
					
					else if(percent <= 40)
					{
						color(DyeColor.RED);
					}
					
					else if(percent <= 50)
					{
						color(DyeColor.ORANGE);
					}
					
					else if(percent <= 60)
					{
						color(DyeColor.YELLOW);
					}
					
					else if(percent <= 70)
					{
						color(DyeColor.GREEN);
					}
					
					else if(percent <= 80)
					{
						color(DyeColor.LIME);
					}
					
					else if(percent <= 90)
					{
						color(DyeColor.BLUE);
					}
					
					else
					{
						color(DyeColor.LIGHT_BLUE);
					}
				}
			}
			
			else
			{
				stabilize();
			}
		}
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public Territory getTerritory()
	{
		return territory;
	}
	
	public void setTerritory(Territory territory)
	{
		this.territory = territory;
	}
	
	public Map getMap()
	{
		return map;
	}
	
	public void setMap(Map map)
	{
		this.map = map;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public void setGame(Game game)
	{
		this.game = game;
	}
	
	public boolean isStable()
	{
		return stable;
	}
	
	public void setStable(boolean stable)
	{
		this.stable = stable;
	}
	
	public int getPercent()
	{
		return percent;
	}
	
	public void setPercent(int percent)
	{
		this.percent = percent;
	}
}
