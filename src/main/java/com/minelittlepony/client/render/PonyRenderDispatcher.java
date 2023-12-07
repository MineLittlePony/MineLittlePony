package com.minelittlepony.client.render;

import java.util.function.Function;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.mixin.MixinEntityRenderers;
import com.minelittlepony.client.render.entity.*;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.mson.api.Mson;

import net.minecraft.client.MinecraftClient;
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
        for (SkinTextures.Model armShape : SkinTextures.Model.values()) {
            addPlayerRenderer(armShape);
        }
        MobRenderers.REGISTRY.values().forEach(i -> i.apply(this, force));
    }

    private void addPlayerRenderer(SkinTextures.Model armShape) {
        Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                new Identifier("minelittlepony", "sea/" + armShape.getName()),
                player -> {
                    return !Pony.getManager().getPony(player).race().isHuman()
                            && PonyPosture.hasSeaponyForm(player)
                            && player.method_52814().model() == armShape;
                },
                context -> new AquaticPlayerPonyRenderer(context, armShape == SkinTextures.Model.SLIM)
        );
        Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                new Identifier("minelittlepony", "nirik/" + armShape.getName()),
                player -> {
                    return !Pony.getManager().getPony(player).race().isHuman()
                            && PonyPosture.hasNirikForm(player)
                            && player.method_52814().model() == armShape;
                },
                context -> new FormChangingPlayerPonyRenderer(context, armShape == SkinTextures.Model.SLIM, DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, PonyPosture::isNirikModifier)
        );
        Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                new Identifier("minelittlepony", "land/" + armShape.getName()),
                player -> {
                    return !Pony.getManager().getPony(player).race().isHuman()
                            && !PonyPosture.hasSeaponyForm(player) && !PonyPosture.hasNirikForm(player)
                            && player.method_52814().model() == armShape;
                },
                context -> new PlayerPonyRenderer(context, armShape == SkinTextures.Model.SLIM)
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
    public <T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> PonyRenderContext<T, M> getPonyRenderer(@Nullable T entity) {
        if (entity == null) {
            return null;
        }

        EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        if (renderer instanceof PonyRenderContext) {
            return (PonyRenderContext<T, M>) renderer;
        }

        return null;
    }
}
