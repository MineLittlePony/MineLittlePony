package com.minelittlepony.client.render.entities;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.client.model.entities.ModelBreezie;

/**
 * AKA a breezie :D
 */
public class RenderPonyVex extends RenderBiped<EntityVex> {

    private static final ResourceLocation VEX = new ResourceLocation("minelittlepony", "textures/entity/illager/vex_pony.png");
    private static final ResourceLocation VEX_CHARGING = new ResourceLocation("minelittlepony", "textures/entity/illager/vex_charging_pony.png");

    public RenderPonyVex(RenderManager manager) {
        super(manager, new ModelBreezie(), 0.3F);
    }

    @Override
    protected void preRenderCallback(EntityVex entity, float ticks) {
        GlStateManager.scalef(0.4F, 0.4F, 0.4F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVex entity) {
        return entity.isCharging() ? VEX_CHARGING : VEX;
    }

}
