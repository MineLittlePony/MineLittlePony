package com.minelittlepony.client.render;

import net.minecraft.client.render.Frustum;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;

import com.minelittlepony.client.PonyBounds;

public class FrustrumCheck<T extends LivingEntity> extends Frustum {
    public static final Frustum ALWAYS_VISIBLE = new Frustum(new Matrix4f(), new Matrix4f()) {
        public boolean isVisible(Box bounds) {
            return true;
        }
    };

    private T entity;

    private Frustum vanilla;

    private final EquineRenderManager<T, ?> renderer;

    public FrustrumCheck(EquineRenderManager<T, ?> render) {
        super(new Matrix4f(), new Matrix4f());
        renderer = render;
    }

    public Frustum withCamera(T entity, Frustum vanillaFrustrum) {
        this.entity = entity;
        vanilla = vanillaFrustrum;
        return this;
    }

    @Override
    public boolean isVisible(Box bounds) {
        return vanilla.isVisible(PonyBounds.getBoundingBox(renderer.getPony(entity), entity));
    }

    @Override
    public void setPosition(double x, double y, double z) {
        vanilla.setPosition(x, y, z);
    }
}
