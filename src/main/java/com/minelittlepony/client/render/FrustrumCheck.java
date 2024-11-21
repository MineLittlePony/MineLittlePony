package com.minelittlepony.client.render;

import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;

import org.joml.Matrix4f;

import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class FrustrumCheck<T extends PonyRenderState> extends Frustum {
    public static final Frustum ALWAYS_VISIBLE = new Frustum(new Matrix4f(), new Matrix4f()) {
        public boolean isVisible(Box bounds) {
            return true;
        }
    };

    private T entity;

    private Frustum vanilla;

    public FrustrumCheck() {
        super(new Matrix4f(), new Matrix4f());
    }

    public Frustum withCamera(T entity, Frustum vanillaFrustrum) {
        this.entity = entity;
        vanilla = vanillaFrustrum;
        return this;
    }

    @Override
    public boolean isVisible(Box bounds) {
        return vanilla.isVisible(DebugBoundingBoxRenderer.getBoundingBox(entity));
    }

    @Override
    public void setPosition(double x, double y, double z) {
        vanilla.setPosition(x, y, z);
    }
}
