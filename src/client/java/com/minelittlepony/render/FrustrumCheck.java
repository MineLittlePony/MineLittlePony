package com.minelittlepony.render;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;

import com.minelittlepony.pony.data.IPony;

public class FrustrumCheck<T extends EntityLivingBase> implements ICamera {

    private T entity;

    private ICamera vanilla;

    private final RenderPony<T> renderer;

    public FrustrumCheck(RenderPony<T> render) {
        renderer = render;
    }

    public ICamera withCamera(T entity, ICamera vanillaFrustrum) {
        this.entity = entity;
        vanilla = vanillaFrustrum;
        return this;
    }

    @Override
    public boolean isBoundingBoxInFrustum(AxisAlignedBB bounds) {
        IPony pony = renderer.getPony(entity);

        AxisAlignedBB boundingBox = pony.getComputedBoundingBox(entity);

        return vanilla.isBoundingBoxInFrustum(boundingBox);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        vanilla.setPosition(x, y, z);
    }
}
