package com.glacialrush.api.game.object;

import com.glacialrush.api.object.GList;

public enum BattleRank
{
	GODLY(100),
	KRACKEN(96),
	LEVIATHAN(92),
	OBLITERATOR(88),
	DESTRUCTOR(84),
	PREDATOR(80),
	LORD(76),
	ARCHON(72),
	MASTER(68),
	LEGEND(64),
	GUARDIAN(60),
	OVERSEER(56),
	GENERAL(52),
	BRIGADIER(48),
	MAVERICK(44),
	WARDEN(40),
	SENTINEL(36),
	CHEIF(32),
	MAJOR(28),
	CAPTAIN(24),
	COMMANDER(20),
	SERGEANT(16),
	SPECIALIST(12),
	PRIVATE(8),
	INITIATE(1);
	
	private int rank;
	private int reward;
	
	private BattleRank(int rank)
	{
		this.rank = rank;
		this.reward = rank * 2;
	}
	
	public boolean test(int prev, int curr)
	{
		return prev < rank && curr >= rank;
	}
	
	public static BattleRank getTitle(int prev, int curr)
	{
		GList<BattleRank> ranks = new GList<BattleRank>(BattleRank.values());
		BattleRank c = null;
		
		while(!ranks.isEmpty())
		{
			int mx = Integer.MIN_VALUE;
			BattleRank t = null;
			
			for(BattleRank i : ranks.copy())
			{
				if(i.rank > mx)
				{
					mx = i.rank;
					t = i;
				}
			}
			
			if(t.test(prev, curr))
			{
				c = t;
			}
			
			ranks.remove(t);
		}
		
		if(c == null)
		{
			return BattleRank.INITIATE;
		}
		
		return c;
	}
	
	public static BattleRank getTitle(int br)
	{
		GList<BattleRank> ranks = new GList<BattleRank>(BattleRank.values());
		
		while(!ranks.isEmpty())
		{
			int mx = Integer.MIN_VALUE;
			BattleRank t = null;
			
			for(BattleRank i : ranks.copy())
			{
				if(i.rank > mx)
				{
					mx = i.rank;
					t = i;
				}
			}
			
			if(t.rank <= br)
			{
				return t;
			}
			
			ranks.remove(t);
		}
		
		return BattleRank.INITIATE;
	}

	public int getRank()
	{
		return rank;
	}

	public int getReward()
	{
		return reward;
	}
}
