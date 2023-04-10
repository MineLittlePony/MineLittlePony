package com.minelittlepony.client.render.entity.npc;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.IllagerPonyModel;
import com.minelittlepony.client.render.entity.feature.IllagerHeldItemFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class IllagerPonyRenderer<T extends IllagerEntity> extends PonyRenderer<T, IllagerPonyModel<T>> {
    public static final Identifier ILLUSIONIST = new Identifier("minelittlepony", "textures/entity/illager/illusionist_pony.png");
    public static final Identifier EVOKER = new Identifier("minelittlepony", "textures/entity/illager/evoker_pony.png");
    public static final Identifier VINDICATOR = new Identifier("minelittlepony", "textures/entity/illager/vindicator_pony.png");

    public IllagerPonyRenderer(EntityRendererFactory.Context context, Identifier texture) {
        super(context, ModelType.ILLAGER, TextureSupplier.of(texture), BASE_MODEL_SCALE);
    }

    @Override
    protected HeldItemFeature<T, IllagerPonyModel<T>> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new IllagerHeldItemFeature<>(this, context.getHeldItemRenderer());
    }

    public static IllagerPonyRenderer<VindicatorEntity> vindicator(EntityRendererFactory.Context context) {
        return new IllagerPonyRenderer<>(context, VINDICATOR);
    }

    public static IllagerPonyRenderer<EvokerEntity> evoker(EntityRendererFactory.Context context) {
        return new IllagerPonyRenderer<>(context, EVOKER);
    }

    public static class Illusionist extends IllagerPonyRenderer<IllusionerEntity> {

        public Illusionist(EntityRendererFactory.Context context) {
            super(context, ILLUSIONIST);
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
        protected boolean isVisible(IllusionerEntity entity) {
            return true;
        }
    }
}
