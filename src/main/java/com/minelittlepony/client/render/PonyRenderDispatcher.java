package com.minelittlepony.client.render;

import com.google.common.base.Predicates;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.mson.api.Mson;

import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.decoration.MannequinEntity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.util.Identifier;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 */
public class PonyRenderDispatcher {
    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();

    public PonyRenderDispatcher() {
        PonyForm.register(PonyForm.DEFAULT, Predicates.alwaysTrue(), PlayerPonyRenderer::new);
        PonyForm.register(PonyForm.SEAPONY, PonyPosture::hasSeaponyForm, (context, slimArms) -> new AquaticPlayerPonyRenderer<>(context, slimArms, DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, PonyPosture::isSeaponyFormActive));
        PonyForm.register(PonyForm.NIRIK, PonyPosture::hasNirikForm, (context, slimArms) -> new FormChangingPlayerPonyRenderer<>(context, slimArms, DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, PonyPosture::isNirikFormActive));
    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public <T extends PlayerLikeEntity & ClientPlayerLikeEntity> void initialise(EntityRenderManager manager, boolean force) {
        PonyForm.REGISTRY.values().forEach(form -> {
            for (PlayerSkinType armShape : PlayerSkinType.values()) {
                Identifier id = form.id().withSuffixedPath("/" + armShape.asString());
                @SuppressWarnings("unchecked")
                Function<EntityRendererFactory.Context, ? extends PlayerPonyRenderer<T>> factory = context -> (PlayerPonyRenderer<T>)form.factory().create(context, armShape == PlayerSkinType.SLIM);
                Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                        id,
                        player -> !Pony.getManager().getPony(player).race().isHuman()
                                    && player.getSkin().model() == armShape
                                    && form.shouldApply().test(player)
                                    && PonyForm.of(player) == form
                                    && (!(player instanceof MannequinEntity) || MobRenderers.MANNEQUINE.test(player)),
                        factory
                );
                Mson.getInstance().getEntityRendererRegistry().registerPlayerStateRenderer(id,
                        state -> state instanceof PlayerPonyRenderState s
                                    && !s.race.isHuman()
                                    && s.smallArms == (armShape == PlayerSkinType.SLIM)
                                    && form.id().equals(s.form),
                        factory
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
