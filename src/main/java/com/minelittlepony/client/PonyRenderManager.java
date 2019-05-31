package com.minelittlepony.client;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.minelittlepony.client.gui.hdskins.DummyPony;
import com.minelittlepony.client.gui.hdskins.RenderDummyPony;
import com.minelittlepony.client.mixin.MixinRenderManager;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.entities.MobRenderers;
import com.minelittlepony.client.render.entities.player.RenderPonyPlayer;
import com.minelittlepony.model.IPonyModel;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
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
        // Preview on the select skin gui
        MineLPClient.getInstance().getModUtilities().addRenderer(DummyPony.class, RenderDummyPony::new);

        PlayerModels[] models = PlayerModels.values();

        for (int i = 1; i < models.length; i++) {
            registerPlayerSkin(manager, models[i]);
        }

        MobRenderers.registry.forEach(i -> i.apply(this));
    }

    private void registerPlayerSkin(EntityRenderDispatcher manager, PlayerModels playerModel) {
        addPlayerSkin(manager, false, playerModel);
        addPlayerSkin(manager, true, playerModel);
    }

    private void addPlayerSkin(EntityRenderDispatcher manager, boolean slimArms, PlayerModels playerModel) {
        RenderPonyPlayer renderer = playerModel.createRenderer(manager, slimArms);

        ((MixinRenderManager)manager).getMutableSkinMap().put(playerModel.getId(slimArms), renderer);
    }

    /**
     *
     * Replaces an entity renderer depending on whether we want ponies or not.
     *
     * @param state   True if we want ponies (the original will be stored)
     * @param type    The type to replace
     * @param renderer The replacement value
     * @param <T> The entity type
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity, V extends T> void switchRenderer(boolean state, Class<V> type, Function<EntityRenderDispatcher, EntityRenderer<T>> func) {
        if (state) {
            if (!renderMap.containsKey(type)) {
                renderMap.put(type, MinecraftClient.getInstance().getEntityRenderManager().getRenderer(type));
            }
            MineLPClient.getInstance().getModUtilities().addRenderer((Class<T>)type, func);
        } else {
            if (renderMap.containsKey(type)) {
                MineLPClient.getInstance().getModUtilities().addRenderer(type, m -> (EntityRenderer<V>)renderMap.get(type));
            }
        }
    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>, R extends LivingEntityRenderer<T, M> & IPonyRender<T, M>> R getPonyRenderer(@Nullable Entity entity) {
        if (entity == null || !(entity instanceof LivingEntity)) {
            return null;
        }

        EntityRenderer<Entity> renderer = MinecraftClient.getInstance().getEntityRenderManager().getRenderer(entity);

        if (renderer instanceof IPonyRender) {
            return (R)(Object)renderer;
        }

        return null;
    }
}
