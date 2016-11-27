package com.glacialrush.api.map;

import org.bukkit.block.Block;

public interface Buildable
{
	void preBuild();
	void postBuild();
	void build(Block block);
}
