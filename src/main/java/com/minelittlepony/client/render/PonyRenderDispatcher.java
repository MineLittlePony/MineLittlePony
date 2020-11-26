package com.minelittlepony.client.render;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.entity.race.PlayerModels;

import javax.annotation.Nullable;

import com.minelittlepony.common.mixin.MixinEntityRenderDispatcher;
import com.minelittlepony.mson.api.Mson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 * Old values are persisted internally.
 */
public class PonyRenderDispatcher {

    private static final PonyRenderDispatcher INSTANCE = new PonyRenderDispatcher();

    /**
     * Gets the static pony render manager responsible for all entity renderers.
     */
    public static PonyRenderDispatcher getInstance() {
        return INSTANCE;
    }

    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();

    private final Map<EntityType<?>, EntityRenderer<?>> renderMap = Maps.newHashMap();

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public void initialise(EntityRenderDispatcher manager) {
        PlayerModels.registry.forEach(i -> registerPlayerSkin(manager, i));
        MobRenderers.REGISTRY.values().forEach(i -> i.apply(this));
    }

    private void registerPlayerSkin(EntityRenderDispatcher manager, PlayerModels playerModel) {
        if (playerModel != PlayerModels.DEFAULT) {
            addPlayerSkin(manager, false, playerModel);
            addPlayerSkin(manager, true, playerModel);
        }
    }

    private void addPlayerSkin(EntityRenderDispatcher manager, boolean slimArms, PlayerModels playerModel) {
        Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                playerModel.getId(slimArms),
                playerModel.getModelKey().getRendererFactory(slimArms)
        );
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
    @SuppressWarnings("unchecked")
    <T extends Entity, V extends T> void switchRenderer(boolean state, EntityType<V> type, Function<EntityRendererFactory.Context, EntityRenderer<T>> factory) {
        if (state) {
            if (!renderMap.containsKey(type)) {
                renderMap.put(type, ((MixinEntityRenderDispatcher)MinecraftClient.getInstance().getEntityRenderDispatcher()).getEntityRenderers().get(type));
            }
            Mson.getInstance().getEntityRendererRegistry().registerEntityRenderer(type, factory);
        } else {
            if (renderMap.containsKey(type)) {
                Mson.getInstance().getEntityRendererRegistry().registerEntityRenderer(type, m -> (EntityRenderer<T>)renderMap.get(type));
            }
        }
    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> IPonyRenderContext<T, M> getPonyRenderer(@Nullable T entity) {
        if (entity == null) {
            return null;
        }

        EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        if (renderer instanceof IPonyRenderContext) {
            return (IPonyRenderContext<T, M>) renderer;
        }

        return null;
    }
}
