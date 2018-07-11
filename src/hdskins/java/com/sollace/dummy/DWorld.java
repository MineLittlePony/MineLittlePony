package com.sollace.dummy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

public class DWorld extends World {

	public DWorld() {
		super(null, new WorldInfo(
				new WorldSettings(0, GameType.NOT_SET, false, false, WorldType.DEFAULT), "MpServer"),
				new WorldProviderSurface(), null, false);
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return true;
	}
	
	@Override
	public float getLightBrightness(BlockPos pos) {
		return 1;
	}
	
	@Override
	public BlockPos getSpawnPoint() {
		return BlockPos.ORIGIN;
	}
}
