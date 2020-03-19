package com.minelittlepony.client.render.entity.npc;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.IllagerPonyModel;
import com.minelittlepony.client.render.entity.feature.IllagerHeldItemFeature;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class IllagerPonyRenderer<T extends IllagerEntity> extends PonyRenderer<T, IllagerPonyModel<T>> {

    public static final Identifier ILLUSIONIST = new Identifier("minelittlepony", "textures/entity/illager/illusionist_pony.png");
    public static final Identifier EVOKER = new Identifier("minelittlepony", "textures/entity/illager/evoker_pony.png");
    public static final Identifier VINDICATOR = new Identifier("minelittlepony", "textures/entity/illager/vindicator_pony.png");

    public IllagerPonyRenderer(EntityRenderDispatcher manager) {
        super(manager, ModelType.ILLAGER);
    }

    @Override
    protected HeldItemFeature<T, IllagerPonyModel<T>> createItemHoldingLayer() {
        return new IllagerHeldItemFeature<>(this);
    }

    @Override
    public void scale(T entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        stack.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    public static class Vindicator extends IllagerPonyRenderer<VindicatorEntity> {

        public Vindicator(EntityRenderDispatcher manager) {
            super(manager);

        }

        @Override
        public Identifier findTexture(VindicatorEntity entity) {
            return VINDICATOR;
        }
    }

    public static class Evoker extends IllagerPonyRenderer<EvokerEntity> {

        public Evoker(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public Identifier findTexture(EvokerEntity entity) {
            return EVOKER;
        }
    }

    public static class Illusionist extends IllagerPonyRenderer<IllusionerEntity> {

        public Illusionist(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public Identifier findTexture(IllusionerEntity entity) {
            return ILLUSIONIST;
        }

        @Override
        public void render(IllusionerEntity entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
            if (entity.isInvisible()) {
                Vec3d[] clones = entity.method_7065(tickDelta);
                float rotation = getAnimationProgress(entity, tickDelta);

                for (int i = 0; i < clones.length; ++i) {
                    stack.push();
                    stack.translate(
                            clones[i].x + MathHelper.cos(i + rotation * 0.5F) * 0.025D,
                            clones[i].y + MathHelper.cos(i + rotation * 0.75F) * 0.0125D,
                            clones[i].z + MathHelper.cos(i + rotation * 0.7F) * 0.025D
                    );
                    super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
                    stack.pop();
                }
            } else {
                super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
            }
        }

        @Override
        protected boolean isFullyVisible(IllusionerEntity entity) {
            return true;
        }
    }
}
