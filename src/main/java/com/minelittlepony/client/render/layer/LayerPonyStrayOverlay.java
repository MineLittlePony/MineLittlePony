package com.minelittlepony.client.render.layer;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.entities.ModelSkeletonPony;

public class LayerPonyStrayOverlay<Skeleton extends AbstractSkeletonEntity> extends LayerOverlayBase<Skeleton, ModelSkeletonPony<Skeleton>> {

    public static final Identifier STRAY_SKELETON_OVERLAY = new Identifier("minelittlepony", "textures/entity/skeleton/stray_pony_overlay.png");

    private final ModelSkeletonPony<Skeleton> overlayModel = new ModelSkeletonPony<>();

    public LayerPonyStrayOverlay(LivingEntityRenderer<Skeleton, ModelSkeletonPony<Skeleton>> render) {
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
