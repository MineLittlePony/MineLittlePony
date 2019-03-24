package com.minelittlepony.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public interface IModUtilities {
    <T extends TileEntity> void addRenderer(Class<T> type, TileEntityRenderer<T> renderer);

    <T extends Entity> void addRenderer(Class<T> type, Render<T> renderer);

    boolean hasFml();

    float getRenderPartialTicks();
}
