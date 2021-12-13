package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SkeleponyModel;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;
import com.minelittlepony.client.render.entity.feature.GlowingItemFeature;
import com.minelittlepony.client.render.entity.feature.StrayClothingFeature;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.Identifier;

public class SkeleponyRenderer<Skeleton extends AbstractSkeletonEntity> extends PonyRenderer<Skeleton, SkeleponyModel<Skeleton>> {

    public static final Identifier SKELETON = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = new Identifier("minelittlepony", "textures/entity/skeleton/stray_pony.png");

    public SkeleponyRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.SKELETON);
    }

    @Override
    public Identifier findTexture(Skeleton entity) {
        return SKELETON;
    }

    @Override
    protected HeldItemFeature<Skeleton, SkeleponyModel<Skeleton>> createItemHoldingLayer() {
        return new GlowingItemFeature<>(this);
    }

    public static class Stray extends SkeleponyRenderer<StrayEntity> {

        public Stray(EntityRendererFactory.Context context) {
            super(context);
            addFeature(new StrayClothingFeature<>(this));
        }

        @Override
        public Identifier findTexture(StrayEntity entity) {
            return STRAY;
        }
    }

    public static class Wither extends SkeleponyRenderer<WitherSkeletonEntity> {

        public Wither(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public Identifier findTexture(WitherSkeletonEntity entity) {
            return WITHER;
        }

        @Override
        public void scale(WitherSkeletonEntity skeleton, MatrixStack stack, float ticks) {
            super.scale(skeleton, stack, ticks);
            stack.scale(1.2F, 1.2F, 1.2F);
        }

    }

}
