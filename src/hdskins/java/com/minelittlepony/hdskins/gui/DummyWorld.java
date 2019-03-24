package com.minelittlepony.hdskins.gui;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.storage.WorldInfo;

public class DummyWorld extends World {

    public static final World INSTANCE = new DummyWorld();

    private final Chunk chunk = new Chunk(this, new ChunkPrimer(new ChunkPos(0, 0), UpgradeData.EMPTY), 0, 0);

    private DummyWorld() {
        super(null, null,
                new WorldInfo(new WorldSettings(0, GameType.NOT_SET, false, false, WorldType.DEFAULT), "MpServer"),
                new OverworldDimension(),
                null,
                true);
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    public boolean isAreaLoaded(int p_175663_1_, int p_175663_2_, int p_175663_3_, int p_175663_4_, int p_175663_5_, int p_175663_6_, boolean p_175663_7_) {
        return true;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return chunk;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public float getBrightness(BlockPos pos) {
        return 15;
    }

    @Override
    public BlockPos getSpawnPoint() {
        return BlockPos.ORIGIN;
    }

    @Override
    public ITickList<Block> getPendingBlockTicks() {
        return EmptyTickList.get();
    }

    @Override
    public ITickList<Fluid> getPendingFluidTicks() {
        return EmptyTickList.get();
    }

    @Override
    public boolean isChunkLoaded(int var1, int var2, boolean var3) {
        return true;
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public RecipeManager getRecipeManager() {
        return null;
    }

    @Override
    public NetworkTagManager getTags() {
        return null;
    }
}
