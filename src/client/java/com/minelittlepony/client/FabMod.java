package com.minelittlepony.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.hdskins.mixin.MixinEntityRenderDispatcher;
import com.minelittlepony.settings.SensibleJsonConfig;

import java.nio.file.Path;
import java.util.function.Function;

public class FabMod implements ClientModInitializer, IModUtilities {

    private final MineLPClient mlp = new MineLPClient(this);

    @Override
    public void onInitializeClient() {
        mlp.init(SensibleJsonConfig.of(getConfigDirectory(), ClientPonyConfig::new));
        mlp.postInit(MinecraftClient.getInstance());
    }

    @Override
    public <T extends BlockEntity> void addRenderer(Class<T> type, BlockEntityRenderer<T> renderer) {
    }

    @Override
    public <T extends Entity> void addRenderer(Class<T> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer) {
        EntityRenderDispatcher mx = MinecraftClient.getInstance().getEntityRenderManager();
        ((MixinEntityRenderDispatcher)mx).getRenderers().put(type, renderer.apply(mx));
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDirectory().toPath();
    }

    @Override
    public Path getAssetsDirectory() {
        return FabricLoader.getInstance().getGameDirectory().toPath().resolve("assets");
    }
}