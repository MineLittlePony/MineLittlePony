package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SkeleponyModel;

public class StrayClothingFeature<Skeleton extends AbstractSkeletonEntity> extends AbstractClothingFeature<Skeleton, SkeleponyModel<Skeleton>> {

    public static final Identifier STRAY_SKELETON_OVERLAY = new Identifier("minelittlepony", "textures/entity/skeleton/stray_pony_overlay.png");

    @SuppressWarnings("unchecked")
    private final SkeleponyModel<Skeleton> overlayModel = (SkeleponyModel<Skeleton>)ModelType.SKELETON_CLOTHES.createModel();

    public StrayClothingFeature(LivingEntityRenderer<Skeleton, SkeleponyModel<Skeleton>> render) {
        super(render);
    }

    @Override
    protected SkeleponyModel<Skeleton> getOverlayModel() {
        return overlayModel;
    }

    @Override
    protected Identifier getOverlayTexture() {
        return STRAY_SKELETON_OVERLAY;
    }
}
