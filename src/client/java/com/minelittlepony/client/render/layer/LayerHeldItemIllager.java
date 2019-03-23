package com.minelittlepony.client.render.layer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.AbstractIllager.IllagerArmPose;
import net.minecraft.util.EnumHandSide;

import com.minelittlepony.client.model.entities.ModelIllagerPony;

public class LayerHeldItemIllager<T extends AbstractIllager> extends LayerHeldPonyItem<T> {

    public LayerHeldItemIllager(RenderLivingBase<T> livingPony) {
        super(livingPony);
    }

    @Override
    public void doRenderLayer(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        if (shouldRender(entity)) {
            super.doRenderLayer(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
        }
    }

    @Override
    protected void renderArm(EnumHandSide side) {
        ((ModelIllagerPony)getPonyModel()).getArm(side).postRender(0.0625F);
    }

    public boolean shouldRender(T entity) {
        return entity.getArmPose() != IllagerArmPose.CROSSED;
    }
}
