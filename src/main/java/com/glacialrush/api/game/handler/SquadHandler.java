package com.glacialrush.api.game.handler;

import org.bukkit.entity.Player;
import com.glacialrush.api.dispatch.notification.Notification;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.SquadCreateEvent;
import com.glacialrush.api.game.event.SquadDisbandEvent;
import com.glacialrush.api.game.event.SquadInviteEvent;
import com.glacialrush.api.game.event.SquadJoinEvent;
import com.glacialrush.api.game.event.SquadLeaderEvent;
import com.glacialrush.api.game.event.SquadLeaveEvent;
import com.glacialrush.api.game.object.Faction;
import com.glacialrush.api.game.object.Greek;
import com.glacialrush.api.game.object.Squad;
import com.glacialrush.api.game.object.Statistic;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.api.sfx.Audio;

public class SquadHandler extends GlacialHandler
{
	private GMap<Faction, GList<Squad>> squads;
	private GMap<Squad, GList<Player>> invites;
	private GList<Player> tested;
	private RegionedGame rg;
	
	public SquadHandler(Game game)
	{
		super(game);
		
		this.rg = (RegionedGame) game;
		this.squads = new GMap<Faction, GList<Squad>>();
		this.invites = new GMap<Squad, GList<Player>>();
		this.tested = new GList<Player>();
		
		for(Faction i : Faction.all())
		{
			squads.put(i, new GList<Squad>());
		}
	}
	
	public void start(GameState state)
	{
		this.rg = (RegionedGame) state.getGame();
		this.squads = new GMap<Faction, GList<Squad>>();
		this.invites = new GMap<Squad, GList<Player>>();
		this.tested = new GList<Player>();
		
		for(Faction i : Faction.all())
		{
			squads.put(i, new GList<Squad>());
		}
	}
	
	public GList<Squad> getSquads(Faction f)
	{
		if(squads == null)
		{
			this.squads = new GMap<Faction, GList<Squad>>();
		}
		
		if(f == null || !squads.containsKey(f))
		{
			return new GList<Squad>();
		}
		
		return squads.get(f);
	}
	
	public Squad getSquad(Player p)
	{
		for(Squad i : getSquads(rg.getFactionHandler().getFaction(p)))
		{
			if(i.getMembers().contains(p))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public boolean isLeader(Player p, Squad s)
	{
		return s.getLeader().equals(p);
	}
	
	public Squad squadByLeader(Player p)
	{
		for(Squad i : getSquads(rg.getFactionHandler().getFaction(p)))
		{
			if(i.getLeader().equals(p))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public boolean inSquad(Player p)
	{
		return getSquad(p) != null;
	}
	
	public boolean isInvited(Player p, Squad s)
	{
		return invites.containsKey(s) && invites.get(s).contains(p);
	}
	
	public Squad squadByGreek(Faction f, Greek g)
	{
		for(Squad i : getSquads(f))
		{
			if(i.getGreek().equals(g))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public boolean isSquad(Faction f, Greek g)
	{
		return squadByGreek(f, g) != null;
	}
	
	public GList<Greek> squadsForCreation(Faction f)
	{
		GList<Greek> g = new GList<Greek>();
		
		for(Greek i : Greek.values())
		{
			if(!isSquad(f, i))
			{
				g.add(i);
			}
		}
		
		return g;
	}
	
	public void create(Player p, Greek g)
	{
		if(isSquad(rg.getFactionHandler().getFaction(p), g) || inSquad(p))
		{
			return;
		}
		
		squads.get(rg.getFactionHandler().getFaction(p)).add(new Squad(p, rg.getFactionHandler().getFaction(p), g));
		invites.put(squadByLeader(p), new GList<Player>());
		rg.pl().callEvent(new SquadCreateEvent(rg, p, squadByLeader(p)));
		
		if(!tested.contains(p))
		{
			tested.add(p);
			Statistic.SQUAD_LEADS.add(p);
		}
	}
	
	public void leave(Player p)
	{
		if(!inSquad(p))
		{
			return;
		}
		
		Squad s = getSquad(p);
		s.getMembers().remove(p);
		
		Notification n = NotificationPreset.SQUAD_EVENT.format(null, null, new Object[] {s.getColor() + "" + p.getName() + " has left the squad."});
		
		for(Player i : s.getMembers())
		{
			if(i.equals(p))
			{
				continue;
			}
			
			rg.getNotificationHandler().queue(i, n);
			Audio.UI_ACTION.play(i);
		}
		
		rg.pl().callEvent(new SquadLeaveEvent(rg, p, s));
		
		if(isLeader(p, s))
		{
			if(!s.getMembers().isEmpty())
			{
				Player x = s.getMembers().pickRandom();
				s.setLeader(x);
				rg.pl().callEvent(new SquadLeaveEvent(rg, x, s));
				
				Notification nx = NotificationPreset.SQUAD_EVENT.format(null, null, new Object[] {s.getColor() + "" + x.getName() + " has has been promoted to squad leader."});
				
				for(Player i : s.getMembers())
				{
					rg.getNotificationHandler().queue(i, nx);
					Audio.UI_ACTION.play(i);
				}
			}
			
			else
			{
				disband(s);
			}
		}
		
		else
		{
		
		}
	}
	
	public void join(Player p, Squad s)
	{
		if(!isInvited(p, s) || inSquad(p))
		{
			return;
		}
		
		s.getMembers().add(p);
		invites.get(s).remove(p);
		rg.pl().callEvent(new SquadJoinEvent(rg, p, s));
		
		Notification nx = NotificationPreset.SQUAD_EVENT.format(null, null, new Object[] {s.getColor() + "" + p.getName() + " has joined the squad."});
		
		for(Player i : s.getMembers())
		{
			rg.getNotificationHandler().queue(i, nx);
			Audio.UI_ACTION.play(i);
		}
		
		if(!tested.contains(p))
		{
			tested.add(p);
			Statistic.SQUAD_JOINED.add(p);
		}
	}
	
	public void disband(Squad s)
	{
		for(Player i : s.getMembers())
		{
			rg.pl().callEvent(new SquadLeaveEvent(rg, i, s));
		}
		
		invites.remove(s);
		squads.get(s.getFaction()).remove(s);
		rg.pl().callEvent(new SquadDisbandEvent(rg, null, s));
	}
	
	public void setLeader(Player p, Squad s)
	{
		s.setLeader(p);
		rg.pl().callEvent(new SquadLeaderEvent(rg, p, s));
	}
	
	public void invite(Player p, Squad s)
	{
		if(inSquad(p) || isInvited(p, s))
		{
			return;
		}
		
		invites.get(s).add(p);
		rg.pl().callEvent(new SquadInviteEvent(rg, p, s));
		
		Notification nx = NotificationPreset.SQUAD_EVENT.format(null, null, new Object[] {s.getColor() + "" + p.getName() + " has been invited to the squad."});
		
		for(Player i : s.getMembers())
		{
			rg.getNotificationHandler().queue(i, nx);
			Audio.UI_ACTION.play(i);
		}
	}
}
