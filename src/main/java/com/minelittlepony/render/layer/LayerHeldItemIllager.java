package com.minelittlepony.render.layer;

import com.minelittlepony.model.ponies.ModelIllagerPony;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.AbstractIllager.IllagerArmPose;
import net.minecraft.util.EnumHandSide;

public class LayerHeldItemIllager<T extends AbstractIllager> extends LayerHeldPonyItem<T> {

    public LayerHeldItemIllager(RenderLivingBase<T> livingPony) {
        super(livingPony);
    }

    @Override
    public void doPonyRender(T entity, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale) {
        if (shouldRender(entity)) {
            super.doPonyRender(entity, move, swing, ticks, age, headYaw, headPitch, scale);
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
