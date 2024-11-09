package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PillagerPonyModel;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

public class PillagerRenderer extends PonyRenderer<PillagerEntity, PillagerRenderer.State, PillagerPonyModel> {
    private static final Identifier TEXTURE = MineLittlePony.id("textures/entity/illager/pillager_pony.png");

    public PillagerRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.PILLAGER, TextureSupplier.of(TEXTURE));
    }

    @Override
    protected HeldItemFeature<PillagerRenderer.State, PillagerPonyModel> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new IllagerHeldItemFeature<>(this, context.getItemRenderer());
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public class State extends PonyRenderState {
        public IllagerEntity.State state;

        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            state = ((IllagerEntity)entity).getState();
        }
    }

    public class IllagerHeldItemFeature<
        T extends IllagerEntity,
        S extends PillagerRenderer.State,
        M extends AlicornModel<S>
    > extends HeldItemFeature<S, M> {

        public IllagerHeldItemFeature(PonyRenderContext<T, S, M> livingPony, ItemRenderer renderer) {
            super(livingPony, renderer);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
            if (shouldRender(state)) {
                super.render(matrices, vertices, light, state, limbAngle, limbDistance);
            }
        }

        protected boolean shouldRender(S state) {
            return state.state != IllagerEntity.State.CROSSED;
        }
    }
}
