package com.minelittlepony.client.render;

import java.util.function.Function;

import com.google.common.base.Predicates;
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

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 */
public class PonyRenderDispatcher {
    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();

    public PonyRenderDispatcher() {
        PonyForm.register(PonyForm.DEFAULT, Predicates.alwaysTrue(), PlayerPonyRenderer::new);
        PonyForm.register(PonyForm.SEAPONY, PonyPosture::hasSeaponyForm, (context, slimArms) -> new AquaticPlayerPonyRenderer(context, slimArms, DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, PonyPosture::isSeaponyFormActive));
        PonyForm.register(PonyForm.NIRIK, PonyPosture::hasNirikForm, (context, slimArms) -> new FormChangingPlayerPonyRenderer(context, slimArms, DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, PonyPosture::isNirikFormActive));
    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public void initialise(EntityRenderDispatcher manager, boolean force) {
        PonyForm.REGISTRY.values().forEach(form -> {
            for (SkinTextures.Model armShape : SkinTextures.Model.values()) {
                Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                        form.id().withSuffixedPath("/" + armShape.getName()),
                        player -> !Pony.getManager().getPony(player).race().isHuman()
                                    && player.getSkinTextures().model() == armShape
                                    && form.shouldApply().test(player) && PonyForm.of(player) == form,
                        context -> form.factory().create(context, armShape == SkinTextures.Model.SLIM)
                );
            }
        });
        MobRenderers.REGISTRY.values().forEach(i -> i.apply(this, force));
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
        Mson.getInstance().getEntityRendererRegistry().registerEntityRenderer(type, ctx -> state.get()
                ? factory.apply(ctx)
                : MixinEntityRenderers.getRendererFactories().get(type).create(ctx)
        );
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> PonyRenderContext<T, M> getPonyRenderer(@Nullable T entity) {
        if (entity != null && MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof PonyRenderContext c) {
            return c;
        }

        return null;
    }
}
