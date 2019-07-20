package com.minelittlepony.client.render.layer;

import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;

import com.minelittlepony.client.model.races.ModelAlicorn;
import com.minelittlepony.client.render.IPonyRender;

public class LayerHeldItemIllager<T extends IllagerEntity, M extends ModelAlicorn<T>> extends LayerHeldPonyItem<T, M> {

    public LayerHeldItemIllager(IPonyRender<T,M> livingPony) {
        super(livingPony);
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        if (shouldRender(entity)) {
            super.render(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
        }
    }

    @Override
    protected void renderArm(Arm side) {
        getModel().getArm(side).applyTransform(0.0625F);
    }

    public boolean shouldRender(T entity) {
        return entity.getState() != IllagerEntity.State.CROSSED;
    }
}
