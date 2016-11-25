package com.minelittlepony.renderer;

import com.minelittlepony.model.pony.ModelIllagerPony;
import com.minelittlepony.model.pony.ModelVindicatorPony;
import com.minelittlepony.renderer.layer.LayerHeldPonyItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVindicator extends RenderLiving<EntityVindicator> {

    private static final ResourceLocation VINDICATOR = new ResourceLocation("minelittlepony", "textures/entity/illager/vindicator_pony.png");

    public RenderPonyVindicator(RenderManager renderManager) {
        super(renderManager, new ModelVindicatorPony(), 0.5F);

        this.addLayer(new LayerHeldPonyItem(this) {
            @Override
            public void doRenderLayer(EntityLivingBase vindicator, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

                if (((EntityVindicator) vindicator).isAggressive()) {
                    super.doRenderLayer(vindicator, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }
            }

            @Override
            protected void translateToHand(EnumHandSide side) {
                ((ModelIllagerPony) this.livingPonyEntity.getMainModel()).getArm(side).postRender(0.0625F);
            }
        });
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVindicator entity) {
        return VINDICATOR;
    }

    @Override
    protected void preRenderCallback(EntityVindicator entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

}
