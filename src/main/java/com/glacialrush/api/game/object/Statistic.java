package com.glacialrush.api.game.object;

import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.Game;
import com.glacialrush.api.game.GameType;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.object.GMap;
import com.glacialrush.xapi.Format;

public enum Statistic
{
	ACTIVE_HOURS("Hours Played", "You have been online for %s hours in total."),
	MOVEMENT_BLOCKS("Blocks Travelled", "You have moved a total of %s blocks."),
	COMBAT_KILLS("Kills", "You have killed %s enemies."),
	COMBAT_KDR("KDR", "You Kill %s enemies per life. (kills/deaths)"),
	COMBAT_DEATHS("Deaths", "You have been slain %s times."),
	COMBAT_KILLASSISTS("Kill Assists", "You have assisted %s kills."),
	COMBAT_BOW_LONGEST("Longest Shot", "You have shot an arrow that travelled %s blocks, and hit an enemy."),
	COMBAT_BOW_SHOT("Shots Fired", "You have shot enemies %s times."),
	COMBAT_BOW_MISS("Shots Missed", "You have shot and missed %s times."),
	COMBAT_BOW_ACCURACY("Bow Accuracy", "You hit your target %s%% of the time."),
	COMBAT_BOW_CHEAPSHOT("Cheap Shots", "You have shot %s non-critical arrows."),
	COMBAT_BOW_CHEAPRATE("Cheap Rate", "You shoot cheap shots %s%% of the time."),
	COMBAT_RAGEQUIT("Others have Rage Quit because of you", "You have made %s others rage quit after you killed them."),
	COMBAT_JANITOR("Janitor Kills", "You killed %s enemies already low on health (cleaning up kills)."),
	COMBAT_JANITORED("Janitors Cleaned you up", "You have been cleaned up %s times."),
	COMBAT_SUICIDE("Suicides", "You have killed yourself %s times."),
	TERRITORY_POINTS("Points Captured", "You have captured %s capture points."),
	TERRITORY_CAPTURED("Territories Captured", "You have captured %s territories."),
	TERRITORY_LOST("Territories Lost", "You have lost %s territories."),
	TERRITORY_CLR("Territory CLR", "You capture %s%% territories your involved in."),
	SOCIAL_REPUTATION("Reputation", "Your reputation is %s. This is determined by what you say, do, and more."),
	SOCIAL_HACUSATOR("Hackusations", "You have accused %s others of using hacks."),
	SOCIAL_HACKS("Hacks Accused", "You have been accused by %s others of using hacks."),
	SOCIAL_TRASHMOUTH("Trash Mouthed Words", "You have dumped %s trashy words into the community."),
	SQUAD_LEADS("Squads Led", "You have led %s squads."),
	SQUAD_JOINED("Squads Played", "You have been with %s squads."),
	ABILITY_FIRED("Abilities Fired", "You have used your ability %s times."),
	SUPPORT_DONATIONS("Donations", "You have donated %s times!"),
	SUPPORT_VOTES("Votes for Glacial Rush", "You have voted for us %s times :)"),
	SUPPORT_MAP_FRAGMENTS_FOUND("Total Map Fragments Found", "You have found a total of %s map fragments."),
	SUPPORT_MAP_FRAGMENTS_OWNED("Map Fragments", "You have %s map fragments."),
	SUPPORT_MAPS_CONSTRUCTED("Treasure Maps Constructed from Fragments", "You have created %s maps from map fragments."),
	GAMES_LOST("Games Lost", "You have lost %s games."),
	GAMES_WON("Games Won", "You have won %s games."),
	GAMES_WLR("Games Wins/Losses", "Your Win Loss Ratio is %s (kdr for games)"),
	GAMES_PLAYED("Games Played", "You have played %s games."),
	GAMES_WINRATE("Game Win Percent", "You have won %s%% of all games.");
	
	private String tag;
	private String description;
	
	private Statistic(String tag, String description)
	{
		this.tag = tag;
		this.description = description;
	}
	
	public static GMap<String, GList<Statistic>> filter()
	{
		GMap<String, GList<Statistic>> map = new GMap<String, GList<Statistic>>();
		
		for(Statistic i : values())
		{
			if(!map.containsKey(i.category()))
			{
				map.put(i.category(), new GList<Statistic>());
			}
			
			map.get(i.category()).add(i);
		}
		
		return map;
	}
	
	public void add(Player p, double v)
	{
		Double d = get(p);
		
		if(d != null)
		{
			set(p, d + v);
		}
	}
	
	public void add(Player p)
	{
		add(p, 1.0);
	}
	
	public void sub(Player p, double v)
	{
		add(p, -v);
	}
	
	public void sub(Player p)
	{
		add(p, -1.0);
	}
	
	public void set(Player p, double v)
	{
		Game g = GlacialPlugin.instance().gameControl.getGame(p);
		
		if(g != null && g.getType().equals(GameType.REGIONED))
		{
			g.getGameController().gpd(p).getPlayerStatistics().set(this, v);
		}
		
		else
		{
			GlacialPlugin.instance().gameControl.gpd(p).getPlayerStatistics().set(this, v);
		}
	}
	
	public Double get(Player p)
	{
		Game g = GlacialPlugin.instance().gameControl.getGame(p);
		
		if(g != null && g.getType().equals(GameType.REGIONED))
		{
			return g.getGameController().gpd(p).getPlayerStatistics().get(this);
		}
		
		else
		{
			return GlacialPlugin.instance().gameControl.gpd(p).getPlayerStatistics().get(this);
		}
	}
	
	public String category()
	{
		String s = "";
		
		for(Character i : toString().toCharArray())
		{
			if(i.equals('_'))
			{
				break;
			}
			
			if(s.length() > 0)
			{
				s = s + new String("" + i).toLowerCase();
			}
			
			else
			{
				s = s + i;
			}
		}
		
		return s;
	}
	
	public static Statistic get(String key)
	{
		return Statistic.valueOf(key.toUpperCase().replace('.', '_'));
	}
	
	public String yamlTag(String key)
	{
		return key + "." + toString().replace('_', '.').toLowerCase();
	}
	
	public String yamlTag()
	{
		return toString().replace('_', '.').toLowerCase();
	}
	
	public String tag(double value)
	{
		return Format.fa(value) + " " + getTag();
	}
	
	public String describe(Double value)
	{
		return String.format(description, Format.fa(value.doubleValue()) + "");
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public String getDescription()
	{
		return description;
	}
}
