package com.minelittlepony.renderer;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.pony.ModelIllagerPony;
import com.minelittlepony.renderer.layer.LayerHeldPonyItem;
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
            public void doPonyRender(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                    float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (((EntityIllusionIllager) entitylivingbaseIn).func_193082_dl() || ((EntityIllusionIllager) entitylivingbaseIn).func_193096_dj()) {
                    super.doPonyRender(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }
            }

            protected void translateToHand(EnumHandSide p_191361_1_) {
                ((ModelIllagerPony) this.getRenderer().getMainModel()).getArm(p_191361_1_).postRender(0.0625F);
            }
        });
    }

    @Override
    protected ResourceLocation getTexture(EntityIllusionIllager entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(EntityIllusionIllager entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity.isInvisible()) {
            Vec3d[] avec3d = entity.func_193098_a(partialTicks);
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

    protected boolean func_193115_c(EntityIllusionIllager p_193115_1_) {
        return true;
    }
}
