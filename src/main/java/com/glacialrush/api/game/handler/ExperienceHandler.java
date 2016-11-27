package com.glacialrush.api.game.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.glacialrush.api.dispatch.notification.NotificationPreset;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameState;
import com.glacialrush.api.game.GlacialHandler;
import com.glacialrush.api.game.RegionedGame;
import com.glacialrush.api.game.event.BattleRankEvent;
import com.glacialrush.api.game.event.ExperienceEvent;
import com.glacialrush.api.game.experience.Experience;
import com.glacialrush.api.game.experience.ExperienceBoost;

public class ExperienceHandler extends GlacialHandler
{
	private int updateCooldown;
	
	public ExperienceHandler(Game game)
	{
		super(game);
		
		this.updateCooldown = 0;
	}
	
	@EventHandler
	public void onExperience(ExperienceEvent e)
	{
		if(game.players().size() < 2)
		{
			e.setExperience(((long) (Math.random() * 5)) + 2);
		}
	}
	
	public void tick(GameState state)
	{
		updateCooldown--;
		
		if(updateCooldown <= 0)
		{
			updateCooldown = 20;
			
			for(Player p : state.getPlayers())
			{
				p.setLevel(getBr(p));
			}
		}
	}
	
	public int getBattleRank(long xp)
	{
		return (int) Math.pow(xp, 1.0 / 4);
	}
	
	public double xpToNextBR(Player p)
	{
		long c = getExperience(getBr(p) + 1);
		long b = getXp(p);
		long a = getExperience(getBr(p));
		
		return c - (a + b);
	}
	
	public double percentToNextBR(Player p)
	{
		long c = getExperience(getBr(p) + 1);
		long b = getXp(p);
		long a = getExperience(getBr(p));
		
		return (double) (Math.abs((double) a - (double) b) / (double) Math.abs((double) a - (double) c));
	}
	
	public long getExperience(int battleRank)
	{
		return (long) Math.pow(battleRank, 4);
	}
	
	public Long getXp(Player p)
	{
		return game.getGameController().gpd(p).getExperience();
	}
	
	public void setXp(Player p, Long xp)
	{
		game.getGameController().gpd(p).setExperience(Math.abs(xp));
		setBr(p, getBattleRank(xp));
	}
	
	public Double getXpb(Player p)
	{
		return game.getGameController().gpd(p).getExperienceBoost();
	}
	
	public Double getXpb(ExperienceBoost b, Player p)
	{
		return game.getGameController().gpd(p).getExperienceBoostMap().getBoostComponent(b);
	}
	
	public void setXpb(Player p, ExperienceBoost type, Double xpb)
	{
		game.getGameController().gpd(p).getExperienceBoostMap().boost(game.getGameController().gpd(p), type, xpb);
		updateBoosts(p);
	}
	
	public void updateBoosts(Player p)
	{
		game.getGameController().gpd(p).getExperienceBoostMap().update(game.getGameController().gpd(p));
	}
	
	public long getXpn(Player p)
	{
		return game.getGameController().gpd(p).getSkillNext();
	}
	
	public void setXpn(Player p, long xpn)
	{
		game.getGameController().gpd(p).setSkillNext(xpn);
	}
	
	public long getSk(Player p)
	{
		return game.getGameController().gpd(p).getSkill();
	}
	
	public void setSk(Player p, long sk)
	{
		game.getGameController().gpd(p).setSkill(sk);
	}
	
	public void addSk(Player p, long sk)
	{
		setSk(p, getSk(p) + sk);
	}
	
	public void setBr(Player p, int br)
	{
		game.getGameController().gpd(p).setBattleRank(br);
	}
	
	public int getBr(Player p)
	{
		return game.getGameController().gpd(p).getBattleRank();
	}
	
	public void addXpn(Player p, Long xpn)
	{
		long tsk = getXpn(p) + xpn;
		
		if(tsk >= 500)
		{
			long sk = 0;
			
			while(tsk >= 500)
			{
				sk++;
				tsk -= 500;
			}
			
			addSk(p, sk);
		}
		
		setXpn(p, tsk);
	}
	
	public void giveXp(Player p, Long xp, Experience reason)
	{
		ExperienceEvent xpe = new ExperienceEvent((RegionedGame) game, p, reason, xp);
		
		game.pl().callEvent(xpe);
		
		if(!xpe.isCancelled())
		{
			updateBoosts(p);
			game.pl().getMarketController().chanceShard(p);
			int cbr = getBr(p);
			
			setXp(p, getXp(p) + (Math.abs(xp) + ((long) (getXpb(p) * Math.abs(xp)))));
			addXpn(p, (Math.abs(xp) + ((long) (getXpb(p) * Math.abs(xp)))));
			
			int cbn = getBr(p);
			
			((RegionedGame) game).getNotificationHandler().queue(p, NotificationPreset.EXPERIENCE.format(null, new Object[] {"" + ((Math.abs(xp) + ((long) (getXpb(p) * Math.abs(xp)))))}, new Object[] {reason.reason(), (int) (100 * getXpb(p)) + "%"}));
			
			if(cbr < cbn)
			{
				game.pl().callEvent(new BattleRankEvent((RegionedGame) game, p, getExperience(cbn), cbn, cbr));
				((RegionedGame) game).getNotificationHandler().queue(p, NotificationPreset.EXPERIENCE_RANKUP.format(new Object[] {cbn + ""}, new Object[] {getExperience(cbn) + ""}, null));
			}
			
			p.setLevel(getBr(p));
		}
	}
	
	public void giveXpRaw(Player p, Long xp, Experience reason)
	{
		updateBoosts(p);
		game.pl().getMarketController().chanceShard(p);
		int cbr = getBr(p);
		
		setXp(p, getXp(p) + (Math.abs(xp)));
		addXpn(p, (Math.abs(xp)));
		
		int cbn = getBr(p);
		
		((RegionedGame) game).getNotificationHandler().queue(p, NotificationPreset.EXPERIENCE.format(null, new Object[] {"" + ((Math.abs(xp)))}, new Object[] {reason.reason(), (int) (0) + "%"}));
		
		if(cbr < cbn)
		{
			game.pl().callEvent(new BattleRankEvent((RegionedGame) game, p, getExperience(cbn), cbn, cbr));
			((RegionedGame) game).getNotificationHandler().queue(p, NotificationPreset.EXPERIENCE_RANKUP.format(new Object[] {cbn + ""}, new Object[] {getExperience(cbn) + ""}, null));
		}
		
		p.setLevel(getBr(p));
	}
}
