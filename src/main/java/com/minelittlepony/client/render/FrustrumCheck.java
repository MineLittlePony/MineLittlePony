package com.minelittlepony.client.render;

import net.minecraft.client.render.VisibleRegion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BoundingBox;

import com.minelittlepony.pony.IPony;

public class FrustrumCheck<T extends LivingEntity> implements VisibleRegion {

    private T entity;

    private VisibleRegion vanilla;

    private final RenderPony<T, ?> renderer;

    public FrustrumCheck(RenderPony<T, ?> render) {
        renderer = render;
    }

    public VisibleRegion withCamera(T entity, VisibleRegion vanillaFrustrum) {
        this.entity = entity;
        vanilla = vanillaFrustrum;
        return this;
    }

    @Override
    public boolean intersects(BoundingBox bounds) {
        IPony pony = renderer.getPony(entity);

        BoundingBox boundingBox = pony.getComputedBoundingBox(entity);

        return vanilla.intersects(boundingBox);
    }

    @Override
    public void setOrigin(double x, double y, double z) {
        vanilla.setOrigin(x, y, z);
    }
}
