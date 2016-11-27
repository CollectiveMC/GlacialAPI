package com.glacialrush.api.component;

import java.io.File;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.game.data.ChunkletData;
import com.glacialrush.api.game.data.MapData;
import com.glacialrush.api.game.data.RegionData;
import com.glacialrush.api.map.Chunklet;
import com.glacialrush.api.map.Map;
import com.glacialrush.api.map.Region;
import com.glacialrush.api.object.GList;

public class MapDataController extends Controller
{
	private DataComponent<MapData> mdc;
	
	public MapDataController(GlacialPlugin pl, File base, String targetExtention)
	{
		super(pl);
		
		mdc = new DataComponent<MapData>(pl, base, targetExtention);
	}
	
	public GList<MapData> getMapData()
	{
		GList<MapData> md = new GList<MapData>();
		
		for(File f : mdc.getCache().keySet())
		{
			md.add(mdc.getCache().get(f));
		}
		
		return md;
	}
	
	public void track(Map m)
	{
		GList<RegionData> rgd = new GList<RegionData>();
		
		for(Region i : m.getRegions())
		{
			GList<ChunkletData> chd = new GList<ChunkletData>();
			
			for(Chunklet j : i.getChunklets())
			{
				chd.add(new ChunkletData(j.getX(), j.getZ()));
			}
			
			rgd.add(new RegionData(i.getName(), chd, i.getType()));
		}
		
		MapData md = new MapData(m.getName(), rgd, m.locked(), m.getWorld().getName());
		md.setConfig(m.getMapConfiguration());
		
		mdc.track(new File(mdc.getBase(), md.getName().toLowerCase().replace(' ', '-') + ".map"), md);
	}
	
	public void preEnable()
	{
		mdc.loadAll();
	}
	
	public void postEnable()
	{
		
	}
	
	public void preDisable()
	{
		
	}
	
	public void	postDisable()
	{
		mdc.saveAll();
	}

	public DataComponent<MapData> getMdc()
	{
		return mdc;
	}

	public void setMdc(DataComponent<MapData> mdc)
	{
		this.mdc = mdc;
	}
}
