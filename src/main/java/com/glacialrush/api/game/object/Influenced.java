package com.glacialrush.api.game.object;

import org.bukkit.entity.Player;
import com.glacialrush.api.object.GMap;

public interface Influenced
{
	GMap<Player, Integer> getInfluenceMap();
	GMap<Player, Integer> popInfluenceMap();
	double getInfluence(Player p);
	void resetInfluenceMap();
	void injectInfluence(GMap<Player, Integer> influence);
	void influence(Player p, Integer i);
}
