package com.minelittlepony.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Timer;

public interface IModUtilities {
    <T extends TileEntity> void addRenderer(Class<T> type, TileEntitySpecialRenderer<T> renderer);

    <T extends Entity> void addRenderer(Class<T> type, Render<T> renderer);

    boolean hasFml();

    Timer getGameTimer();
}
