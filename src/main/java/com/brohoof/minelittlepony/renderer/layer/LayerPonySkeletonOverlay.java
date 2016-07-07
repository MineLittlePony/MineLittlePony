package com.brohoof.minelittlepony.renderer.layer;

import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.model.pony.ModelSkeletonPony;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.util.ResourceLocation;

public class LayerPonySkeletonOverlay extends LayerOverlayBase<EntitySkeleton> {

    private final ModelSkeletonPony overlayModel;

    public LayerPonySkeletonOverlay(RenderLivingBase<?> render) {
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
        return PonyManager.STRAY_SKELETON_OVERLAY;
    }
}
