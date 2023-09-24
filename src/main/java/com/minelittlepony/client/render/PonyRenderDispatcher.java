package com.minelittlepony.client.render;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.mixin.MixinEntityRenderers;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelType;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.mson.api.Mson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 */
public class PonyRenderDispatcher {
    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }

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
        addPlayerSkin(manager, SkinTextures.Model.SLIM, race);
        addPlayerSkin(manager, SkinTextures.Model.WIDE, race);
    }

    private void addPlayerSkin(EntityRenderDispatcher manager, SkinTextures.Model armShape, Race race) {
        Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                new Identifier("minelittlepony", race.name().toLowerCase(Locale.ROOT) + "/" + armShape.getName()),
                (Predicate<AbstractClientPlayerEntity>)(player -> {
                    return IPony.getManager().getPony(player).metadata().getRace() == race
                            && player.method_52814().model() == armShape;
                }),
                ModelType.getPlayerModel(race).getFactory(armShape == SkinTextures.Model.SLIM)
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
