package com.minelittlepony.client.render;

import net.minecraft.client.render.Frustum;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.SizePreset;

@Deprecated
public final class FrustrumCheck<T extends LivingEntity> extends Frustum {
    public static final Frustum ALWAYS_VISIBLE = new Frustum(new Matrix4f(), new Matrix4f()) {
        public boolean isVisible(Box bounds) {
            return true;
        }
    };

    private Frustum vanilla;
    @Nullable
    private Box boxOverride;

    public FrustrumCheck() {
        super(new Matrix4f(), new Matrix4f());
    }

    public Frustum withCamera(PonyRenderContext<T, ?, ?> context, T entity, Frustum vanillaFrustrum) {
        vanilla = vanillaFrustrum;

        Pony pony = context.getEntityPony(entity);
        boolean baby = entity.isBaby();

        boxOverride = DebugBoundingBoxRenderer.getBoundingBox(
                entity.getX(), entity.getY(), entity.getZ(),
                (baby ? SizePreset.FOAL : pony.size()).scaleFactor(),
                entity.getWidth(),
                entity.getHeight()
        );
        return this;
    }

    @Override
    public boolean isVisible(Box bounds) {
        return vanilla.isVisible(boxOverride == null ? bounds : boxOverride);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        vanilla.setPosition(x, y, z);
    }
}
