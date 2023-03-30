package com.minelittlepony.client.render;

import java.util.function.Function;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.mixin.MixinEntityRenderers;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelType;

import org.jetbrains.annotations.Nullable;

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

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public void initialise(EntityRenderDispatcher manager, boolean force) {
        Race.REGISTRY.forEach(r -> {
            if (!r.isHuman()) {
                registerPlayerSkin(manager, r);
            }
        });
        MobRenderers.REGISTRY.values().forEach(i -> i.apply(this, force));
    }

    private void registerPlayerSkin(EntityRenderDispatcher manager, Race race) {
        addPlayerSkin(manager, false, race);
        addPlayerSkin(manager, true, race);
    }

    private void addPlayerSkin(EntityRenderDispatcher manager, boolean slimArms, Race race) {
        Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                race.getModelId(slimArms),
                ModelType.getPlayerModel(race).getFactory(slimArms)
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
    <T extends Entity, V extends T> void switchRenderer(MobRenderers state, EntityType<V> type, Function<EntityRendererFactory.Context, EntityRenderer<T>> factory) {
        Mson.getInstance().getEntityRendererRegistry().registerEntityRenderer(type, ctx -> {
            if (!state.get()) {
                return MixinEntityRenderers.getRendererFactories().get(type).create(ctx);
            }
            return factory.apply(ctx);
        });
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
