package com.minelittlepony.client;

import java.util.Map;

import com.google.common.collect.Maps;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.entities.MobRenderers;
import com.minelittlepony.client.render.entities.player.RenderPonyPlayer;

import javax.annotation.Nullable;

import com.minelittlepony.common.mixin.MixinEntityRenderDispatcher;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 * Old values are persisted internally.
 */
public class PonyRenderManager {

    private static final PonyRenderManager renderManager = new PonyRenderManager();

    /**
     * Gets the static pony render manager responsible for all entity renderers.
     */
    public static PonyRenderManager getInstance() {
        return renderManager;
    }

    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();


    private final Map<Class<? extends Entity>, EntityRenderer<?>> renderMap = Maps.newHashMap();

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public void initialiseRenderers(EntityRenderDispatcher manager) {
        PlayerModels.registry.forEach(i -> registerPlayerSkin(manager, i));
        MobRenderers.registry.forEach(i -> i.apply(this));
    }

    private void registerPlayerSkin(EntityRenderDispatcher manager, PlayerModels playerModel) {
        if (playerModel != PlayerModels.DEFAULT) {
            addPlayerSkin(manager, false, playerModel);
            addPlayerSkin(manager, true, playerModel);
        }
    }

    private void addPlayerSkin(EntityRenderDispatcher manager, boolean slimArms, PlayerModels playerModel) {
        RenderPonyPlayer renderer = playerModel.createRenderer(manager, slimArms);

        addPlayerRenderer(playerModel.getId(slimArms), renderer);
    }

    private static void addPlayerRenderer(String modelType, PlayerEntityRenderer renderer) {
        EntityRenderDispatcher mx = MinecraftClient.getInstance().getEntityRenderManager();
        ((MixinEntityRenderDispatcher)mx).getPlayerRenderers().put(modelType, renderer);
    }

    /**
     *
     * Replaces an entity renderer depending on whether we want ponies or not.
     *
     * @param state   True if we want ponies (the original will be stored)
     * @param type    The type to replace
     * @param factory The replacement value
     * @param <T> The entity type
     */
    public <T extends Entity, V extends T> void switchRenderer(boolean state, EntityType<V> type, EntityRendererRegistry.Factory factory) {
        if (state) {
            if (!renderMap.containsKey(type)) {
                renderMap.put(type, MinecraftClient.getInstance().getEntityRenderManager().getRenderer(type));
            }
            EntityRendererRegistry.INSTANCE.register(type, factory);
        } else {
            if (renderMap.containsKey(type)) {
                EntityRendererRegistry.INSTANCE.register(type, (m, c) -> renderMap.get(type));
            }
        }
    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> IPonyRender<T, M> getPonyRenderer(@Nullable T entity) {
        if (entity == null) {
            return null;
        }

        EntityRenderer<T> renderer = (EntityRenderer<T>)MinecraftClient.getInstance().getEntityRenderManager().getRenderer(entity);

        if (renderer instanceof IPonyRender) {
            return (IPonyRender<T, M>) renderer;
        }

        return null;
    }
}
