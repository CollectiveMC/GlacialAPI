package com.glacialrush.api.game.handler;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.glacialrush.Strings;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.BattleRankEvent;
import com.glacialrush.api.game.event.CombatEvent;
import com.glacialrush.api.game.event.DeathEvent;
import com.glacialrush.api.game.event.FireAbilityEvent;
import com.glacialrush.api.game.event.GameJoinEvent;
import com.glacialrush.api.game.event.GameQuitEvent;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.object.BattleRank;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.PlayerObject;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.game.obtainable.item.AbilityEffect;
import com.glacialrush.api.game.obtainable.item.WeaponEffect;
import com.glacialrush.api.object.GBiset;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.vfx.GVFX;
import com.glacialrush.api.vfx.ParticleEffect;
import com.glacialrush.xapi.Area;

public class CombatHandler extends GlacialHandler
{
	private RegionedGame rg;
	private GMap<Player, BattleRank> titles;
	private GMap<Player, GList<GBiset<Player, Integer>>> kax;
	private GMap<Player, Integer> stabbers;
	private GMap<Player, Player> lastKill;
	
	public CombatHandler(Game game)
	{
		super(game);
		rg = (RegionedGame) game;
		titles = new GMap<Player, BattleRank>();
		kax = new GMap<Player, GList<GBiset<Player, Integer>>>();
		stabbers = new GMap<Player, Integer>();
		lastKill = new GMap<Player, Player>();
	}
	
	public void tick(GameState state)
	{
		for(Player i : new GList<Player>(stabbers.keySet()))
		{
			stabbers.put(i, stabbers.get(i) - 1);
			
			if(stabbers.get(i) < 0)
			{
				stabbers.remove(i);
			}
		}
		
		GVFX.tick();
	}
	
	public void cleanedUp(Player janitor, Player cleaned)
	{
		String s = Strings.COMBAT_JANITOR.get();
		rg.getNotificationHandler().queue(janitor, NotificationPreset.ACHEIVEMENT.format(null, new Object[] {s}, null));
		Statistic.COMBAT_JANITOR.add(janitor);
		Statistic.COMBAT_JANITORED.add(cleaned);
		cleaned.sendMessage(ChatColor.GOLD + janitor.getName() + " cleaned you up.");
	}
	
	public void suicide(Player p)
	{
		String s = Strings.COMBAT_SUICIDE.get();
		rg.getNotificationHandler().queue(p, NotificationPreset.ACHEIVEMENT.format(null, new Object[] {s}, null));
		Statistic.COMBAT_SUICIDE.add(p);
		kax.remove(p);
	}
	
	@EventHandler
	public void onAbility(FireAbilityEvent e)
	{
		Statistic.ABILITY_FIRED.add(e.getPlayer());
	}
	
	@EventHandler
	public void gameEvent(GameJoinEvent e)
	{
		if(!rg.getUuid().equals(e.getGame().getUuid()))
		{
			return;
		}
		
		titles.put(e.getPlayer(), BattleRank.getTitle(game.getGameController().gpd(e.getPlayer()).getBattleRank()));
	}
	
	@EventHandler
	public void gameEvent(GameQuitEvent e)
	{
		if(!rg.getUuid().equals(e.getGame().getUuid()))
		{
			return;
		}
		
		titles.remove(e.getPlayer());
	}
	
	@EventHandler
	public void battleRankUp(BattleRankEvent e)
	{
		BattleRank br = titles.get(e.getPlayer());
		BattleRank nr = BattleRank.getTitle(e.getBattleRank());
				
		if(!br.equals(nr))
		{
			titles.put(e.getPlayer(), nr);
			rg.getNotificationHandler().queue(e.getPlayer(), NotificationPreset.RANK_TITLE.format(new Object[] {StringUtils.capitalize(nr.toString().toLowerCase())}, null, null));
			game.pl().getMarketController().giveShards(e.getPlayer(), nr.getReward());
		}
		
		for(int i = 0; i < (Math.random() * e.getBattleRank()) + 1; i++)
		{
			rg.getDropletHandler().dropSomething(e.getPlayer().getLocation().add(0, 3, 0), (long) (2 + (Math.random() * 10)), Experience.INFLUENCE_MAP);
		}
	}
	
	public void spawnDead(Player p, Location location)
	{
		ParticleEffect.CRIT.display(0, 0, 0, 1f, 20, location.clone().add(0, 1, 0), 30);
	}
	
	@EventHandler
	public void onCombat(DeathEvent e)
	{
		spawnDead(e.getPlayer(), e.getPlayer().getLocation());
		
		try
		{
			if(lastKill.contains(e.getDamager()))
			{
				if(lastKill.get(e.getDamager()).equals(e.getPlayer()) && !e.getDamager().equals(e.getPlayer()))
				{
					((RegionedGame)game).getExperienceHandler().giveXp(e.getDamager(), 350l, Experience.KILL_REPEAT);
				}
			}
		}
		
		catch(Exception ex)
		{
			
		}
		
		if(e.getDamager().equals(e.getPlayer()))
		{
			suicide(e.getPlayer());
		}
		
		else
		{
			e.getRegion().getMap().influence(e.getDamager(), 1);
			lastKill.put(e.getDamager(), e.getPlayer());
			rg.getDropletHandler().dropSomething(e.getPlayer().getLocation(), (long) (1 + (Math.random() * 100)), Experience.KILL_BONUS);
			GVFX.firework(e.getPlayer().getLocation(), Faction.get(e.getPlayer()));
			
			try
			{
				if(kax.get(e.getPlayer()).size() > 1)
				{
					for(GBiset<Player, Integer> j : kax.copy().get(e.getPlayer()))
					{
						if(j.getA().equals(e.getDamager()))
						{						
							if(j.getB() < 4)
							{
								cleanedUp(e.getDamager(), e.getPlayer());
							}
						}
					}
				}
			}
			
			catch(Exception exx)
			{
				
			}
		}
		
		kax.remove(e.getPlayer());
		lastKill.remove(e.getPlayer());
	}
	
	@EventHandler
	public void onCombat(CombatEvent e)
	{
		if(e.getPlayer().equals(e.getDamager()))
		{
			return;
		}
		
		if(!kax.containsKey(e.getPlayer()))
		{
			kax.put(e.getPlayer(), new GList<GBiset<Player, Integer>>());
		}
		
		boolean bb = false;
		
		for(GBiset<Player, Integer> i : kax.get(e.getPlayer()))
		{
			if(i.getA().equals(e.getDamager()))
			{
				bb = true;
				i.setB(i.getB() + 1);
			}
		}
		
		if(!bb)
		{
			kax.get(e.getPlayer()).add(new GBiset<Player, Integer>(e.getDamager(), 1));
		}
		
		PlayerObject p = game.getGameController().gpo(e.getDamager());
		
		if(((RegionedGame)game).getAbilityHandler().isActive(e.getPlayer()))
		{
			if(((RegionedGame)game).getAbilityHandler().getAbilityEffect(e.getPlayer()).equals(AbilityEffect.STRENGTH))
			{
				e.setDamage(e.getDamage() / 1.5);
			}
		}
		
		if(p.getWeapon() != null)
		{
			if(p.getWeapon().getWeaponEffect().equals(WeaponEffect.SPLASH))
			{
				Area a = new Area(e.getPlayer().getLocation(), 2.1);
				
				for(Player i : a.getNearbyPlayers())
				{
					if(!game.players().contains(i))
					{
						continue;
					}
					
					if(i.equals(e.getDamager()) || i.equals(e.getPlayer()))
					{
						continue;
					}
					
					Faction f = ((RegionedGame)game).getFactionHandler().getFaction(i);
					
					if(!f.equals(((RegionedGame)game).getFactionHandler().getFaction(e.getDamager())))
					{
						i.damage(2.8);
					}
				}
			}
			
			if(p.getWeapon().getWeaponEffect().equals(WeaponEffect.SHOCK))
			{
				
			}
			
			if(p.getWeapon().getWeaponEffect().equals(WeaponEffect.BACKSTAB))
			{
				if(!stabbers.containsKey(e.getDamager()))
				{
					stabbers.put(e.getDamager(), 400);
					e.getPlayer().setHealth(e.getPlayer().getHealth() - e.getPlayer().getHealth() / 4);
				}
			}
		}
	}
}
