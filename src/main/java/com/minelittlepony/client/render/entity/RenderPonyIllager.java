package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.entity.ModelIllagerPony;
import com.minelittlepony.client.render.entity.feature.LayerHeldItemIllager;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItem;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
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

public abstract class RenderPonyIllager<T extends IllagerEntity> extends RenderPonyMob<T, ModelIllagerPony<T>> {

    public static final Identifier ILLUSIONIST = new Identifier("minelittlepony", "textures/entity/illager/illusionist_pony.png");
    public static final Identifier EVOKER = new Identifier("minelittlepony", "textures/entity/illager/evoker_pony.png");
    public static final Identifier VINDICATOR = new Identifier("minelittlepony", "textures/entity/illager/vindicator_pony.png");

    public RenderPonyIllager(EntityRenderDispatcher manager) {
        super(manager, new ModelIllagerPony<>());
    }

    @Override
    protected LayerHeldPonyItem<T, ModelIllagerPony<T>> createItemHoldingLayer() {
        return new LayerHeldItemIllager<>(this);
    }

    @Override
    public void scale(T entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        stack.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    public static class Vindicator extends RenderPonyIllager<VindicatorEntity> {

        public Vindicator(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
            super(manager);

        }

        @Override
        public Identifier findTexture(VindicatorEntity entity) {
            return VINDICATOR;
        }
    }

    public static class Evoker extends RenderPonyIllager<EvokerEntity> {

        public Evoker(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
            super(manager);
        }

        @Override
        public Identifier findTexture(EvokerEntity entity) {
            return EVOKER;
        }
    }

    public static class Illusionist extends RenderPonyIllager<IllusionerEntity> {

        public Illusionist(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
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
                float rotation = getAge(entity, tickDelta);

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
        protected boolean method_4056(IllusionerEntity entity, boolean xray) {
            return true;
        }
    }
}
