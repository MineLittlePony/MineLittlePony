package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SkeleponyModel;
import com.minelittlepony.client.render.entity.feature.ClothingFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class SkeleponyRenderer<Skeleton extends AbstractSkeletonEntity> extends PonyRenderer<Skeleton, SkeleponyModel<Skeleton>> {
    public static final Identifier SKELETON = MineLittlePony.id("textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = MineLittlePony.id("textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = MineLittlePony.id("textures/entity/skeleton/stray_pony.png");
    public static final Identifier BOGGED = MineLittlePony.id("textures/entity/skeleton/bogged_pony.png");

    public static final Identifier STRAY_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/stray_pony_overlay.png");
    public static final Identifier BOGGED_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/bogged_pony_overlay.png");

    public SkeleponyRenderer(EntityRendererFactory.Context context, Identifier texture, float scale) {
        super(context, ModelType.SKELETON, TextureSupplier.of(texture), scale);
    }

    public static SkeleponyRenderer<SkeletonEntity> skeleton(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, SKELETON, 1);
    }

    public static SkeleponyRenderer<StrayEntity> stray(EntityRendererFactory.Context context) {
        return PonyRenderer.appendFeature(new SkeleponyRenderer<StrayEntity>(context, STRAY, 1), ctx -> {
            return new ClothingFeature<StrayEntity, SkeleponyModel<StrayEntity>>(ctx, ModelType.SKELETON_CLOTHES, STRAY_SKELETON_OVERLAY);
        });
    }

    public static SkeleponyRenderer<BoggedEntity> bogged(EntityRendererFactory.Context context) {
        return PonyRenderer.appendFeature(PonyRenderer.appendFeature(new SkeleponyRenderer<>(context, BOGGED, 1), ctx -> {
            return new ClothingFeature<BoggedEntity, SkeleponyModel<BoggedEntity>>(ctx, ModelType.SKELETON_CLOTHES, BOGGED_SKELETON_OVERLAY);
        }), BoggedMushroomsFeature::new);
    }

    public static SkeleponyRenderer<WitherSkeletonEntity> wither(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, WITHER, 1.2F);
    }

    public static class BoggedMushroomsFeature extends FeatureRenderer<BoggedEntity, SkeleponyModel<BoggedEntity>> {
        public static final Identifier MUSHROOMS = MineLittlePony.id("textures/entity/skeleton/bogged_pony_mushrooms.png");

        private final SinglePartEntityModel<BoggedEntity> model = ModelType.BOGGED_MUSHROOMS.createModel();

        public BoggedMushroomsFeature(LivingEntityRenderer<BoggedEntity, SkeleponyModel<BoggedEntity>> renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, BoggedEntity entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
            if (!entity.isSheared()) {
                matrices.push();
                getContextModel().transform(BodyPart.HEAD, matrices);
                getContextModel().head.rotate(matrices);
                VertexConsumer buffer = vertices.getBuffer(model.getLayer(MUSHROOMS));
                model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, Colors.WHITE);
                matrices.pop();
            }
        }
    }
}
