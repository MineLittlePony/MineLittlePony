package com.minelittlepony.client.render.entity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.entity.ModelBreezie;

/**
 * AKA a breezie :D
 */
public class RenderPonyVex extends BipedEntityRenderer<VexEntity, ModelBreezie<VexEntity>> {

    private static final Identifier VEX = new Identifier("minelittlepony", "textures/entity/illager/vex_pony.png");
    private static final Identifier VEX_CHARGING = new Identifier("minelittlepony", "textures/entity/illager/vex_charging_pony.png");

    public RenderPonyVex(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, new ModelBreezie<>(), 0.3F);
    }

    @Override
    protected void scale(VexEntity entity, MatrixStack stack, float ticks) {
        stack.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    public Identifier getTexture(VexEntity entity) {
        return entity.isCharging() ? VEX_CHARGING : VEX;
    }

}
