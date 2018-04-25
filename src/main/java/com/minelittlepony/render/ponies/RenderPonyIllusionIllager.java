package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.ponies.ModelIllagerPony;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.layer.LayerHeldPonyItem;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public class RenderPonyIllusionIllager extends RenderPonyMob<EntityIllusionIllager> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("minelittlepony", "textures/entity/illager/illusionist_pony.png");

    public RenderPonyIllusionIllager(RenderManager renderManager) {
        super(renderManager, PMAPI.illager);
    }

    @Override
    protected void addLayers() {
        this.addLayer(new LayerHeldPonyItem(this) {
            @Override
            public void doPonyRender(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                                     float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (((EntityIllusionIllager) entitylivingbaseIn).isSpellcasting() || ((EntityIllusionIllager) entitylivingbaseIn).isAggressive()) {
                    super.doPonyRender(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }
            }

            @Override
            protected void translateToHand(EnumHandSide p_191361_1_) {
                ((ModelIllagerPony) this.getRenderer().getMainModel()).getArm(p_191361_1_).postRender(0.0625F);
            }
        });
    }

    @Override
    protected ResourceLocation getTexture(EntityIllusionIllager entity) {
        return TEXTURE;
    }

    protected void preRenderCallback(EntityIllusionIllager entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public void doRender(EntityIllusionIllager entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity.isInvisible()) {
            Vec3d[] avec3d = entity.getRenderLocations(partialTicks);
            float f = this.handleRotationFloat(entity, partialTicks);

            for (int i = 0; i < avec3d.length; ++i) {
                super.doRender(entity,
                        x + avec3d[i].x + (double) MathHelper.cos((float) i + f * 0.5F) * 0.025D,
                        y + avec3d[i].y + (double) MathHelper.cos((float) i + f * 0.75F) * 0.0125D,
                        z + avec3d[i].z + (double) MathHelper.cos((float) i + f * 0.7F) * 0.025D,
                        yaw, partialTicks);
            }
        } else {
            super.doRender(entity, x, y, z, yaw, partialTicks);
        }
    }

    @Override
    protected boolean isVisible(EntityIllusionIllager p_193115_1_) {
        return true;
    }
}
