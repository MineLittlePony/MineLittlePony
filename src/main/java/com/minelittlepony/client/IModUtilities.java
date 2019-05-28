package com.minelittlepony.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

import java.nio.file.Path;
import java.util.function.Function;

public interface IModUtilities {
    <T extends BlockEntity> void addRenderer(Class<T> type, BlockEntityRenderer<T> renderer);

    <T extends Entity> void addRenderer(Class<T> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer);

    default boolean hasFml() {
        return false;
    }

    Path getConfigDirectory();

    Path getAssetsDirectory();
}
