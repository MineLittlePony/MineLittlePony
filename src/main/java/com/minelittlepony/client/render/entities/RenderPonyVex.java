package com.minelittlepony.client.render.entities;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.entities.ModelBreezie;
import com.mojang.blaze3d.platform.GlStateManager;

/**
 * AKA a breezie :D
 */
public class RenderPonyVex extends BipedEntityRenderer<VexEntity, ModelBreezie<VexEntity>> {

    private static final Identifier VEX = new Identifier("minelittlepony", "textures/entity/illager/vex_pony.png");
    private static final Identifier VEX_CHARGING = new Identifier("minelittlepony", "textures/entity/illager/vex_charging_pony.png");

    public RenderPonyVex(EntityRenderDispatcher manager) {
        super(manager, new ModelBreezie<>(), 0.3F);
    }

    @Override
    protected void scale(VexEntity entity, float ticks) {
        GlStateManager.scalef(0.4F, 0.4F, 0.4F);
    }

    @Override
    protected Identifier getTexture(VexEntity entity) {
        return entity.isCharging() ? VEX_CHARGING : VEX;
    }

}
