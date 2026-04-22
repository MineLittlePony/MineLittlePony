package com.minelittlepony.client.render;

import com.google.common.base.Predicates;
import com.minelittlepony.api.config.MobPonifyCategory;
import com.minelittlepony.api.config.MobPonifyCategory.Context;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.state.PonifiedRenderState;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.Untyped;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.mson.api.EntityRendererRegistry;
import com.minelittlepony.mson.api.Mson;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 */
public class PonyRenderDispatcherImpl implements PonyRenderDispatcher {
    public PonyRenderDispatcherImpl() {
        PonyForm.register(PonyForm.DEFAULT, Predicates.alwaysTrue(), PlayerPonyRenderer::new);
        PonyForm.register(PonyForm.SEAPONY, PonyPosture::hasSeaponyForm, (context, slimArms) -> new AquaticPlayerPonyRenderer<>(context, slimArms, DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, PonyPosture::isSeaponyFormActive));
        PonyForm.register(PonyForm.NIRIK, PonyPosture::hasNirikForm, (context, slimArms) -> new FormChangingPlayerPonyRenderer<>(context, slimArms, DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, PonyPosture::isNirikFormActive));
    }

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public <T extends Avatar & ClientAvatarEntity> void initialise(EntityRenderDispatcher manager, boolean force) {
        EntityRendererRegistry registry = Mson.getInstance().getEntityRendererRegistry();
        PonyForm.REGISTRY.values().forEach(form -> {
            for (PlayerModelType armShape : PlayerModelType.values()) {
                Identifier id = form.id().withSuffix("/" + armShape.getSerializedName());
                Function<EntityRendererProvider.Context, ? extends PlayerPonyRenderer<T>> factory = context -> Untyped.cast(form.factory().create(context, armShape == PlayerModelType.SLIM));
                registry.registerPlayerRenderer(
                        id,
                        player -> !Pony.getManager().getPony(player).race().isHuman()
                                    && player.getSkin().model() == armShape
                                    && form.shouldApply().test(player)
                                    && PonyForm.of(player) == form
                                    && (!(player instanceof Mannequin) || MobRenderers.MANNEQUINE.test(player)),
                        factory
                );
                registry.registerPlayerStateRenderer(id,
                        state -> state instanceof PlayerPonyRenderState s
                                    && !s.race.isHuman()
                                    && s.smallArms == (armShape == PlayerModelType.SLIM)
                                    && form.id().equals(s.form),
                        factory
                );
            }
        });

        final var context = new Context() {
            MobPonifyCategory category;
            @SuppressWarnings("hiding")
            @Override
            public <T extends Entity, R extends EntityRenderer<?, ?>> void registerEntityRenderer(EntityType<T> type, Predicate<? super T> condition, Function<EntityRendererProvider.Context, R> constructor) {
                registry.registerEntityRenderer(type, condition, constructor);
                registry.registerEntityStateRenderer(type, s -> s.entityType == type && s instanceof PonifiedRenderState, constructor);
            }

            @Override
            public <P extends BlockEntity, R extends BlockEntityRenderer<?, ?>> void registerBlockRenderer(BlockEntityType<P> type, Predicate<? super P> condition, Function<BlockEntityRendererProvider.Context, R> constructor) {
                registry.registerBlockRenderer(type, condition, constructor);
                var category = this.category;
                registry.registerBlockStateRenderer(type, s -> s.blockEntityType == type && category.option().get(), constructor);
            }
        };

        MobRenderers.REGISTRY.values().forEach(i -> {
            context.category = i;
            i.apply(context);
        });
    }

    @Override
    public <T extends Model<?> & PonyModel<?>> PlayerModelKey<T> getPlayerModel(Race race) {
        return ModelType.getPlayerModel(race);
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
