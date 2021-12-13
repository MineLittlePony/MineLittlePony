package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.BreezieModel;

/**
 * AKA a breezie :D
 */
public class VexRenderer extends BipedEntityRenderer<VexEntity, BreezieModel<VexEntity>> {

    private static final Identifier VEX = new Identifier("minelittlepony", "textures/entity/illager/vex_pony.png");
    private static final Identifier VEX_CHARGING = new Identifier("minelittlepony", "textures/entity/illager/vex_charging_pony.png");

    public VexRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.BREEZIE.createModel(), 0.3F);
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
