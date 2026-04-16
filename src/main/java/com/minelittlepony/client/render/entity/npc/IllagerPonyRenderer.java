package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.*;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.IllagerPonyModel;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.model.entity.race.ChangelingModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;
import com.mojang.blaze3d.vertex.PoseStack;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

public class IllagerPonyRenderer<
        T extends AbstractIllager,
        S extends IllagerPonyRenderer.State,
        M extends AlicornModel<S>
    > extends PonyRenderer<T, S, M> {
    public static final Identifier PILLAGER = MineLittlePony.id("textures/entity/illager/pillager_pony.png");
    public static final Identifier ILLUSIONIST = MineLittlePony.id("textures/entity/illager/illusionist_pony.png");
    public static final Identifier EVOKER = MineLittlePony.id("textures/entity/illager/evoker_pony.png");
    public static final Identifier VINDICATOR = MineLittlePony.id("textures/entity/illager/vindicator_pony.png");

    public IllagerPonyRenderer(EntityRendererProvider.Context context, ModelKey<? super M> key, Identifier texture) {
        super(context, key, TextureSupplier.of(texture), BASE_MODEL_SCALE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public S createRenderState() {
        return (S)new State();
    }

    @Override
    protected HeldItemFeature<S, M> createHeldItemFeature(EntityRendererProvider.Context context) {
        return new IllagerHeldItemFeature<>(this);
    }

    public static IllagerPonyRenderer<Pillager, State, ChangelingModel<State>> pillager(EntityRendererProvider.Context context) {
        return new IllagerPonyRenderer<Pillager, State, ChangelingModel<State>>(context, ModelType.PILLAGER, PILLAGER) {
            @Override
            public ArmPose getArmPose(Pillager entity, HumanoidArm arm) {
                if (entity.getMainArm() == arm) {
                    switch (entity.getArmPose()) {
                        case BOW_AND_ARROW: return ArmPose.BOW_AND_ARROW;
                        case CROSSBOW_CHARGE: return ArmPose.CROSSBOW_CHARGE;
                        case CROSSBOW_HOLD: return ArmPose.CROSSBOW_HOLD;
                        default: return super.getArmPose(entity, arm);
                    }
                }

                return super.getArmPose(entity, arm);
            }
        };
    }

    public static IllagerPonyRenderer<Vindicator, ?, ?> vindicator(EntityRendererProvider.Context context) {
        return new IllagerPonyRenderer<Vindicator, State, IllagerPonyModel<State>>(context, ModelType.ILLAGER, VINDICATOR);
    }

    public static IllagerPonyRenderer<Evoker, ?, ?> evoker(EntityRendererProvider.Context context) {
        return new IllagerPonyRenderer<Evoker, State, IllagerPonyModel<State>>(context,ModelType.ILLAGER, EVOKER);
    }

    public static class State extends PonyRenderState {
        public AbstractIllager.IllagerArmPose state;

        public void updateState(ItemModelResolver resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            state = ((AbstractIllager)entity).getArmPose();
        }
    }

    public static class IllagerHeldItemFeature<
        T extends AbstractIllager,
        S extends IllagerPonyRenderer.State,
        M extends AlicornModel<S>
    > extends HeldItemFeature<S, M> {
        public IllagerHeldItemFeature(PonyRenderContext<T, S, M> livingPony) {
            super(livingPony);
        }

        @Override
        public void render(PoseStack matrices, SubmitNodeCollector frame, int light, S state, float xRot, float yRot) {
            if (shouldRender(state)) {
                super.render(matrices, frame, light, state, xRot, yRot);
            }
        }

        protected boolean shouldRender(S state) {
            return state.state != AbstractIllager.IllagerArmPose.CROSSED;
        }
    }
}
