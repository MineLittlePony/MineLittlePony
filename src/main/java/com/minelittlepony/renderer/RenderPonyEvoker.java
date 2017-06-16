package com.minelittlepony.renderer;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.pony.ModelIllagerPony;
import com.minelittlepony.renderer.layer.LayerHeldPonyItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntitySpellcasterIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderPonyEvoker extends RenderPonyMob<EntityEvoker> {

    private static final ResourceLocation EVOKER = new ResourceLocation("minelittlepony", "textures/entity/illager/evoker_pony.png");

    public RenderPonyEvoker(RenderManager rendermanagerIn) {
        super(rendermanagerIn, PMAPI.illager);
    }

    @Override
    protected void addLayers() {
        this.addLayer(new LayerHeldPonyItem(this) {
            public void doPonyRender(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (((EntitySpellcasterIllager) entitylivingbaseIn).isSpellcasting()) {
                    super.doPonyRender(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }
            }

            protected void translateToHand(EnumHandSide p_191361_1_) {
                ((ModelIllagerPony) this.getRenderer().getMainModel()).getArm(p_191361_1_).postRender(0.0625F);
            }
        });
    }

    @Override
    protected ResourceLocation getTexture(EntityEvoker entity) {
        return EVOKER;
    }

    @Override
    protected void preRenderCallback(EntityEvoker entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

}
