package com.minelittlepony.client.render;

import com.google.common.base.Predicates;
import com.minelittlepony.api.model.PreviewModel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.mson.api.Mson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.LivingEntity;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 */
public class PonyRenderDispatcher {
    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();

    public PonyRenderDispatcher() {
        PonyForm.register(PonyForm.DEFAULT, Predicates.alwaysTrue(), PlayerPonyRenderer::new);
        PonyForm.register(PonyForm.SEAPONY, PonyPosture::hasSeaponyForm, (context, slimArms) -> new AquaticPlayerPonyRenderer(context, slimArms, DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, entity -> {
            if (entity instanceof PreviewModel preview) {
                return preview.getForm() == PonyForm.SEAPONY;
            }
            return PonyPosture.hasSeaponyForm(entity) && PonyPosture.isPartiallySubmerged(entity);
        }));
        PonyForm.register(PonyForm.NIRIK, PonyPosture::hasNirikForm, (context, slimArms) -> new FormChangingPlayerPonyRenderer(context, slimArms, DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, PonyPosture::hasNirikForm));
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
        MobRenderers.REGISTRY.values().forEach(i -> i.changer().accept(i, Mson.getInstance().getEntityRendererRegistry()));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends LivingEntity, S extends PonyRenderState, M extends ClientPonyModel<S>, R extends LivingEntityRenderer<T, S, M> & PonyRenderContext<T, S, M>> R getPonyRenderer(@Nullable T entity) {
        if (entity != null && MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof PonyRenderContext c) {
            return (R)c;
        }

        return null;
    }
}
