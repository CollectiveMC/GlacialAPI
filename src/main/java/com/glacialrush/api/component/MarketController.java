package com.glacialrush.api.component;

import java.util.Random;
import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.data.PlayerData;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.game.obtainable.Obtainable;
import com.glacialrush.api.rank.Rank;
import org.bukkit.ChatColor;

public class MarketController extends Controller
{
	public MarketController(GlacialPlugin pl)
	{
		super(pl);
	}
	
	public boolean has(Player p, Obtainable o)
	{
		return pl.gpd(p).getOwned().contains(o.getId());
	}
	
	public long getShards(Player p)
	{
		return pl.gpd(p).getShards();
	}
	
	public void setShards(Player p, long shards)
	{
		pl.gpd(p).setShards(shards);
	}
	
	public void giveShards(Player p, long shards)
	{
		setShards(p, getShards(p) + Math.abs(shards));
		
		Game g = pl.gameControl.getGame(p);
		Notification n = NotificationPreset.FOUND.format(null, new Object[] {ChatColor.AQUA + "" + shards + " Shard" + (shards > 1 ? "s" : "")}, null);

		if(g != null && g.getType().equals(GameType.REGIONED))
		{
			RegionedGame rg = (RegionedGame) g;
			rg.getNotificationHandler().queue(p, n);
		}
		
		else
		{
			n.show(p);
		}
	}
	
	public void giveShards(String p, long shards)
	{
		for(Player i : pl.onlinePlayers())
		{
			if(i.getName().equals(p))
			{
				giveShards(i, shards);
			}
		}
	}
	
	public void takeShards(Player p, long shards)
	{
		setShards(p, getShards(p) - Math.abs(shards));
	}
	
	public long getSkill(Player p)
	{
		return pl.gpd(p).getSkill();
	}
	
	public void setSkill(Player p, long skill)
	{
		pl.gpd(p).setSkill(skill);
	}
	
	public void takeSkill(Player p, long skill)
	{
		setSkill(p, getSkill(p) - skill);
	}
	
	public boolean has(Player p, long skill)
	{
		return getSkill(p) >= skill;
	}
	
	public void give(Player p, Obtainable o)
	{
		if(!has(p, o))
		{
			pl.gpd(p).getOwned().add(o.getId());
		}
	}
	
	public void rankUp(Player p, Rank r)
	{
		PlayerData pd = pl.gpd(p);
		
		if(pd.getRanks().contains(r))
		{
			return;
		}
		
		boolean admin = pd.getRanks().contains(Rank.OWNER);
		boolean mod = pd.getRanks().contains(Rank.MOD);
		
		pd.getRanks().clear();
		takeShards(p, r.getCost());
		
		for(Rank i : r.getInheritance())
		{
			pd.getRanks().add(i);
		}
		
		pd.getRanks().add(r);
		
		if(mod)
		{
			pd.getRanks().add(Rank.MOD);
		}
		
		if(admin)
		{
			pd.getRanks().add(Rank.OWNER);
		}
		
		Notification n = NotificationPreset.UNLOCK_RANK.format(new Object[]{r.getName()}, null, null);
		n.show(p);
	}
	
	public void prioritizeRank(Player p, Rank r)
	{
		
	}
	
	public void buy(Player p, Obtainable o)
	{
		if(has(p, o) || !has(p, o.getCost()))
		{
			return;
		}
		
		give(p, o);
		takeSkill(p, o.getCost());
		
		Game g = pl.getGameControl().getGame(p);
		
		if(g != null && g.getType().equals(GameType.REGIONED))
		{
			((RegionedGame)g).getNotificationHandler().queue(p, NotificationPreset.UNLOCK.format(new Object[]{o.getName()}, null, null));
		}
		
		else
		{
			Notification n = NotificationPreset.UNLOCK.format(new Object[]{o.getName()}, null, null);
			n.show(p);
		}
	}
	
	public void chanceShard(Player p)
	{
		Random r = new Random();
		Integer a = r.nextInt(3);
		
		if(a <= 0)
		{
			return;
		}
		
		if(r.nextBoolean() && r.nextBoolean() && r.nextBoolean() && r.nextBoolean())
		{
			giveShards(p, a);
		}
	}
	
	public void awardShards(Player p)
	{
		Random r = new Random();
		Integer a = r.nextInt(24) + 2;
		Game game = pl.getGameControl().getGame(p);
		Notification n = NotificationPreset.FOUND.format(null, new Object[] {ChatColor.AQUA + "" + a + " Shards"}, null);
		giveShards(p, a);
		
		if(game == null || !game.getType().equals(GameType.REGIONED))
		{
			n.show(p);
		}
		
		else
		{
			((RegionedGame)game).getNotificationHandler().queue(p, n);
		}
	}
	
	public long getMapFragments(Player p)
	{
		return Statistic.SUPPORT_MAP_FRAGMENTS_OWNED.get(p).longValue();
	}
	
	public void chanceFragments(Player p, int outOf100, int reward)
	{
		if((Math.random() * 100) <= outOf100)
		{
			Statistic.SUPPORT_MAP_FRAGMENTS_FOUND.add(p, reward);
			Statistic.SUPPORT_MAP_FRAGMENTS_OWNED.add(p, reward);
			
			if(pl.gameControl.getGame(p) == null)
			{
				NotificationPreset.FOUND_MAP_FRAGMENT.format(null, new Object[]{reward + ""}, null).show(p);
			}
			
			else
			{
				((RegionedGame)pl.getGameControl().getGame(p)).getNotificationHandler().queue(p, NotificationPreset.FOUND_MAP_FRAGMENT.format(null, new Object[]{reward + ""}, null));
			}
		}
	}
}
