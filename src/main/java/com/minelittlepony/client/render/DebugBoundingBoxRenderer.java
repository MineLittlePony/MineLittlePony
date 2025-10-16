package com.minelittlepony.client.render;

import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;

import com.google.common.collect.ImmutableList.Builder;
import com.minelittlepony.api.model.RenderPass;

public final class DebugBoundingBoxRenderer {
    public static <T extends LivingEntity> void appendHitbox(T entity, EquineRenderManager<T, ?, ?> manager, Builder<EntityHitbox> builder, float tickDelta) {
        if (RenderPass.getCurrent() == RenderPass.WORLD) {
            Box box = manager.getHitbox(entity).offset(-entity.getX(), -entity.getY(), -entity.getZ());
            builder.add(new EntityHitbox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 1, 1, 0));

            float yaw = (entity.isSleeping() && entity.getSleepingDirection() != null ? entity.getSleepingDirection().getPositiveHorizontalDegrees() : entity.bodyYaw) * MathHelper.RADIANS_PER_DEGREE;
            Vec3d min = new Vec3d(0, 0, 0.3).rotateY(MathHelper.PI - yaw);

            box = box.offset(min.x, 0, min.z);
            builder.add(new EntityHitbox(box.minX, box.minY, box.minZ, box.maxX, box.maxY * 0.6F, box.maxZ, 1, 0, 0));
        }
    }

    public static Box getBoundingBox(double x, double y, double z, float scale, float width, float height) {
        width *= scale;
        height *= scale;
        return new Box(x - width, y, z - width, x + width, y + height, z + width);
    }

    public static Box applyScale(float scale, Box box) {
        double w = (box.maxX - box.minX) * 0.5F,
                h = (box.maxY - box.minY),
                d = (box.maxZ - box.minZ) * 0.5F,
                x = box.minX + w,
                z = box.minZ + d;
        w *= scale;
        d *= scale;
        return new Box(
                x - w, box.minY, z - d,
                x + w, box.minY + h * scale, z + d
        );
    }
}
