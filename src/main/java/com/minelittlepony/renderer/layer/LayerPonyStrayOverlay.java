package com.minelittlepony.renderer.layer;

import com.minelittlepony.model.pony.ModelSkeletonPony;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.util.ResourceLocation;

public class LayerPonyStrayOverlay extends LayerOverlayBase<EntitySkeleton> {

    public static final ResourceLocation STRAY_SKELETON_OVERLAY = new ResourceLocation("minelittlepony", "textures/entity/skeleton/stray_pony_overlay.png");

    private final ModelSkeletonPony overlayModel;

    public LayerPonyStrayOverlay(RenderLivingBase<?> render) {
        super(render);
        this.overlayModel = new ModelSkeletonPony();
        this.overlayModel.init(0F, 0.25F);
    }

    @Override
    public void doRenderLayer(EntitySkeleton skele, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (skele.getSkeletonType() == SkeletonType.STRAY) {
            this.renderOverlay(skele, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    protected ModelBase getOverlayModel() {
        return this.overlayModel;
    }

    @Override
    protected ResourceLocation getOverlayTexture() {
        return STRAY_SKELETON_OVERLAY;
    }
}
