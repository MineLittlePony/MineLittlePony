package com.minelittlepony.client.render.entity.npc;

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
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class IllagerPonyRenderer<
        T extends IllagerEntity,
        S extends IllagerPonyRenderer.State,
        M extends AlicornModel<S>
    > extends PonyRenderer<T, S, M> {
    public static final Identifier PILLAGER = MineLittlePony.id("textures/entity/illager/pillager_pony.png");
    public static final Identifier ILLUSIONIST = MineLittlePony.id("textures/entity/illager/illusionist_pony.png");
    public static final Identifier EVOKER = MineLittlePony.id("textures/entity/illager/evoker_pony.png");
    public static final Identifier VINDICATOR = MineLittlePony.id("textures/entity/illager/vindicator_pony.png");

    public IllagerPonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, Identifier texture) {
        super(context, key, TextureSupplier.of(texture), BASE_MODEL_SCALE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public S createRenderState() {
        return (S)new State();
    }

    @Override
    protected HeldItemFeature<S, M> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new IllagerHeldItemFeature<>(this);
    }

    public static IllagerPonyRenderer<PillagerEntity, State, ChangelingModel<State>> pillager(EntityRendererFactory.Context context) {
        return new IllagerPonyRenderer<PillagerEntity, State, ChangelingModel<State>>(context, ModelType.PILLAGER, PILLAGER) {
            @Override
            public ArmPose getArmPose(PillagerEntity entity, Arm arm) {
                if (entity.getMainArm() == arm) {
                    switch (entity.getState()) {
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

    public static IllagerPonyRenderer<VindicatorEntity, ?, ?> vindicator(EntityRendererFactory.Context context) {
        return new IllagerPonyRenderer<VindicatorEntity, State, IllagerPonyModel<State>>(context, ModelType.ILLAGER, VINDICATOR);
    }

    public static IllagerPonyRenderer<EvokerEntity, ?, ?> evoker(EntityRendererFactory.Context context) {
        return new IllagerPonyRenderer<EvokerEntity, State, IllagerPonyModel<State>>(context,ModelType.ILLAGER, EVOKER);
    }

    public static class State extends PonyRenderState {
        public IllagerEntity.State state;

        public void updateState(ItemModelManager resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            state = ((IllagerEntity)entity).getState();
        }
    }

    public static class IllagerHeldItemFeature<
        T extends IllagerEntity,
        S extends IllagerPonyRenderer.State,
        M extends AlicornModel<S>
    > extends HeldItemFeature<S, M> {
        public IllagerHeldItemFeature(PonyRenderContext<T, S, M> livingPony) {
            super(livingPony);
        }

        @Override
        public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
            if (shouldRender(state)) {
                super.render(matrices, queue, light, state, limbAngle, limbDistance);
            }
        }

        protected boolean shouldRender(S state) {
            return state.state != IllagerEntity.State.CROSSED;
        }
    }
}
