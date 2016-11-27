package com.glacialrush.api.game.data;

import java.io.Serializable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.glacialrush.api.game.experience.ExperienceBoostMap;
import com.glacialrush.api.game.loadout.LoadoutSet;
import com.glacialrush.api.object.GList;
import com.glacialrush.api.rank.Rank;

public class PlayerData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Long experience;
	private Long skillNext;
	private Double experienceBoost;
	private Long skill;
	private Long shards;
	private Integer battleRank;
	private GList<String> owned;
	private GList<Rank> ranks;
	private LoadoutSet loadoutSet;
	private Boolean resourcePackAccepted;
	private Integer selectedLoadout;
	private ExperienceBoostMap experienceBoostMap;
	private PlayerStatistics playerStatistics;
	
	private Boolean particles;
	private Boolean customSounds;
	private Boolean verbose;
	
	public PlayerData(Player player)
	{
		this.name = player.getName();
		this.experience = (long) 0;
		this.experienceBoost = 0.0;
		this.skillNext = (long) 0;
		this.resourcePackAccepted = false;
		this.battleRank = 0;
		this.skill = (long) 0;
		this.shards = (long) 0;
		this.selectedLoadout = 0;
		this.owned = new GList<String>();
		this.ranks = new GList<Rank>().qadd(Rank.COLD);
		this.particles = true;
		this.customSounds = true;
		this.verbose = false;
		this.loadoutSet = new LoadoutSet();
		this.experienceBoostMap = new ExperienceBoostMap();
		this.playerStatistics = new PlayerStatistics();
		
		if(player.getName().equals("cyberpwn") || player.getName().equals("Xanthous_"))
		{
			ranks.clear();
			ranks.add(Rank.OWNER);
		}
	}
	
	public PlayerData(FileConfiguration fc)
	{
		name = fc.getString("name");
		experience = fc.getLong("experience");
		experienceBoost = fc.getDouble("experience-boost");
		skillNext = fc.getLong("skill-next");
		battleRank = fc.getInt("battle-rank");
		resourcePackAccepted = fc.getBoolean("resource-pack-accepted");
		skill = fc.getLong("skill");
		shards = fc.getLong("shards");
		selectedLoadout = fc.getInt("selected-loadout");
		owned = new GList<String>(fc.getStringList("owned"));
		ranks = new GList<Rank>();
		particles = fc.getBoolean("settings.particles");
		customSounds = fc.getBoolean("settings.custom-sounds");
		verbose = fc.getBoolean("settings.verbose");
		playerStatistics = new PlayerStatistics(fc, "stats");
		
		GList<String> rks = new GList<String>(fc.getStringList("ranks"));
		
		for(String i : rks)
		{
			ranks.add(Rank.valueOf(i));
		}
		
		loadoutSet = new LoadoutSet(fc);
		experienceBoostMap = new ExperienceBoostMap(fc);
	}
	
	public FileConfiguration yaml()
	{
		FileConfiguration fc = new YamlConfiguration();
		
		fc.set("name", name);
		fc.set("experience", experience);
		fc.set("experience-boost", experienceBoost);
		fc.set("resource-pack-accepted", resourcePackAccepted);
		fc.set("battle-rank", battleRank);
		fc.set("skill-next", skillNext);
		fc.set("skill", skill);
		fc.set("shards", shards);
		fc.set("selected-loadout", selectedLoadout);
		fc.set("owned", owned);
		
		GList<String> rks = new GList<String>();
		
		for(Rank i : ranks)
		{
			rks.add(i.toString());
		}
		
		fc.set("ranks", rks);
		fc.set("settings.particles", particles);
		fc.set("settings.custom-sounds", customSounds);
		fc.set("settings.verbose", verbose);
		fc = loadoutSet.yml(fc);
		fc = experienceBoostMap.yaml(fc);
		fc = playerStatistics.toYaml(fc, "stats");
		
		return fc;
	}
	
	public PlayerStatistics getPlayerStatistics()
	{
		return playerStatistics;
	}

	public void setPlayerStatistics(PlayerStatistics playerStatistics)
	{
		this.playerStatistics = playerStatistics;
	}

	public ExperienceBoostMap getExperienceBoostMap()
	{
		return experienceBoostMap;
	}

	public void setExperienceBoostMap(ExperienceBoostMap experienceBoostMap)
	{
		this.experienceBoostMap = experienceBoostMap;
	}

	public Boolean getResourcePackAccepted()
	{
		return resourcePackAccepted;
	}

	public void setResourcePackAccepted(Boolean resourcePackAccepted)
	{
		this.resourcePackAccepted = resourcePackAccepted;
	}

	public Integer getSelectedLoadout()
	{
		return selectedLoadout;
	}

	public void setSelectedLoadout(Integer selectedLoadout)
	{
		this.selectedLoadout = selectedLoadout;
	}

	public Long getShards()
	{
		return shards;
	}

	public void setShards(Long shards)
	{
		this.shards = shards;
	}

	public LoadoutSet getLoadoutSet()
	{
		return loadoutSet;
	}

	public void setLoadoutSet(LoadoutSet loadoutSet)
	{
		this.loadoutSet = loadoutSet;
	}

	public Boolean getVerbose()
	{
		return verbose;
	}
	
	public void setVerbose(Boolean verbose)
	{
		this.verbose = verbose;
	}
	
	public Boolean getCustomSounds()
	{
		return customSounds;
	}
	
	public void setCustomSounds(Boolean customSounds)
	{
		this.customSounds = customSounds;
	}
	
	public Boolean getParticles()
	{
		return particles;
	}
	
	public void setParticles(Boolean particles)
	{
		this.particles = particles;
	}
	
	public Integer getBattleRank()
	{
		return battleRank;
	}
	
	public void setBattleRank(Integer battleRank)
	{
		this.battleRank = battleRank;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Long getExperience()
	{
		return experience;
	}
	
	public void setExperience(Long experience)
	{
		this.experience = experience;
	}
	
	public Double getExperienceBoost()
	{
		return experienceBoost;
	}
	
	public void setExperienceBoost(Double experienceBoost)
	{
		this.experienceBoost = experienceBoost;
	}
	
	public Long getSkill()
	{
		return skill;
	}
	
	public void setSkill(Long skill)
	{
		this.skill = skill;
	}
	
	public GList<String> getOwned()
	{
		return owned;
	}
	
	public void setOwned(GList<String> owned)
	{
		this.owned = owned;
	}
	
	public GList<Rank> getRanks()
	{
		return ranks;
	}
	
	public void setRanks(GList<Rank> ranks)
	{
		this.ranks = ranks;
	}
	
	public Long getSkillNext()
	{
		return skillNext;
	}
	
	public void setSkillNext(Long skillNext)
	{
		this.skillNext = skillNext;
	}
}
