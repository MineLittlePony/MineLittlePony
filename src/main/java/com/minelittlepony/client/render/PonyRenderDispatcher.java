package com.minelittlepony.client.render;

import com.google.common.base.Predicates;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.Untyped;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.PlayerModelType;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.mson.api.Mson;

import java.util.function.Function;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 */
public class PonyRenderDispatcher {
    public PonyRenderDispatcher() {
        PonyForm.register(PonyForm.DEFAULT, Predicates.alwaysTrue(), PlayerPonyRenderer::new);
        PonyForm.register(PonyForm.SEAPONY, PonyPosture::hasSeaponyForm, (context, slimArms) -> new AquaticPlayerPonyRenderer<>(context, slimArms, DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, PonyPosture::isSeaponyFormActive));
        PonyForm.register(PonyForm.NIRIK, PonyPosture::hasNirikForm, (context, slimArms) -> new FormChangingPlayerPonyRenderer<>(context, slimArms, DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, PonyPosture::isNirikFormActive));
    }

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public <T extends Avatar & ClientAvatarEntity> void initialise(EntityRenderDispatcher manager, boolean force) {
        PonyForm.REGISTRY.values().forEach(form -> {
            for (PlayerModelType armShape : PlayerModelType.values()) {
                Identifier id = form.id().withSuffix("/" + armShape.getSerializedName());
                Function<EntityRendererProvider.Context, ? extends PlayerPonyRenderer<T>> factory = context -> Untyped.cast(form.factory().create(context, armShape == PlayerModelType.SLIM));
                Mson.getInstance().getEntityRendererRegistry().registerPlayerRenderer(
                        id,
                        player -> !Pony.getManager().getPony(player).race().isHuman()
                                    && player.getSkin().model() == armShape
                                    && form.shouldApply().test(player)
                                    && PonyForm.of(player) == form
                                    && (!(player instanceof Mannequin) || MobRenderers.MANNEQUINE.test(player)),
                        factory
                );
                Mson.getInstance().getEntityRendererRegistry().registerPlayerStateRenderer(id,
                        state -> state instanceof PlayerPonyRenderState s
                                    && !s.race.isHuman()
                                    && s.smallArms == (armShape == PlayerModelType.SLIM)
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
        if (entity != null && Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof PonyRenderContext c) {
            return (R)c;
        }

        return null;
    }
}
