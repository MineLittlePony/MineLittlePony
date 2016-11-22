package com.minelittlepony.renderer;

import com.minelittlepony.model.pony.ModelBreezie;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.ResourceLocation;

/**
 * AKA a breezie :D
 */
public class RenderPonyVex extends RenderBiped<EntityVex> {

    private static final ResourceLocation VEX = new ResourceLocation("minelittlepony", "textures/entity/illager/vex_pony.png");
    private static final ResourceLocation VEX_CHARGING = new ResourceLocation("minelittlepony", "textures/entity/illager/vex_charging_pony.png");

    public RenderPonyVex(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBreezie(), 0.3F);
    }

    @Override
    protected void preRenderCallback(EntityVex entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVex entity) {
        return entity.isCharging() ? VEX_CHARGING : VEX;
    }

}
