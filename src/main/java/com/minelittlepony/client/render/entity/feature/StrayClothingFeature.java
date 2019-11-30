package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.entity.ModelSkeletonPony;

public class StrayClothingFeature<Skeleton extends AbstractSkeletonEntity> extends AbstractClothingFeature<Skeleton, ModelSkeletonPony<Skeleton>> {

    public static final Identifier STRAY_SKELETON_OVERLAY = new Identifier("minelittlepony", "textures/entity/skeleton/stray_pony_overlay.png");

    private final ModelSkeletonPony<Skeleton> overlayModel = new ModelSkeletonPony<>();

    public StrayClothingFeature(LivingEntityRenderer<Skeleton, ModelSkeletonPony<Skeleton>> render) {
        super(render);
    }

    @Override
    protected ModelSkeletonPony<Skeleton> getOverlayModel() {
        return overlayModel;
    }

    @Override
    protected Identifier getOverlayTexture() {
        return STRAY_SKELETON_OVERLAY;
    }
}
