package com.minelittlepony.client.render;

import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import com.google.common.collect.ImmutableList.Builder;
import com.minelittlepony.api.model.RenderPass;

public final class DebugBoundingBoxRenderer {
    public static <T extends LivingEntity> void appendHitbox(T entity, EquineRenderManager<T, ?, ?> manager, Builder<EntityHitbox> builder, float tickDelta) {
        if (RenderPass.getCurrent() == RenderPass.WORLD) {
            Box box = manager.getHitbox(entity);
            builder.add(new EntityHitbox(
                box.minX - entity.getX(),
                box.minY - entity.getY(),
                box.minZ - entity.getZ(),
                box.maxX - entity.getX(),
                box.maxY - entity.getY(),
                box.maxZ - entity.getZ(),
                1,
                1,
                0
            ));
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
