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

    public RenderPonyIllusionIllager(RenderManager manager) {
        super(manager, PMAPI.illager);
    }

    @Override
    protected void addLayers() {
        addLayer(new LayerHeldPonyItem(this) {
            @Override
            public void doPonyRender(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ticks, float age, float headYaw, float headPitch, float scale) {
                if (((EntityIllusionIllager) entity).isSpellcasting() || ((EntityIllusionIllager) entity).isAggressive()) {
                    super.doPonyRender(entity, limbSwing, limbSwingAmount, ticks, age, headYaw, headPitch, scale);
                }
            }

            @Override
            protected void translateToHand(EnumHandSide hand) {
                ((ModelIllagerPony) getRenderer().getMainModel()).getArm(hand).postRender(0.0625F);
            }
        });
    }

    @Override
    protected ResourceLocation getTexture(EntityIllusionIllager entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityIllusionIllager entity, float ticks) {
        super.preRenderCallback(entity, ticks);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public void doRender(EntityIllusionIllager entity, double x, double y, double z, float yaw, float ticks) {
        if (entity.isInvisible()) {
            Vec3d[] vects = entity.getRenderLocations(ticks);
            float f = handleRotationFloat(entity, ticks);

            for (int i = 0; i < vects.length; ++i) {
                super.doRender(entity,
                        x + vects[i].x + MathHelper.cos(i + f * 0.5F) * 0.025D,
                        y + vects[i].y + MathHelper.cos(i + f * 0.75F) * 0.0125D,
                        z + vects[i].z + MathHelper.cos(i + f * 0.7F) * 0.025D,
                        yaw, ticks);
            }
        } else {
            super.doRender(entity, x, y, z, yaw, ticks);
        }
    }

    @Override
    protected boolean isVisible(EntityIllusionIllager entity) {
        return true;
    }
}
