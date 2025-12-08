package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import com.minelittlepony.client.MineLittlePony;

public final class DebugBoundingBoxRenderer {
    public static void drawHitboxes(Entity entity, float tickProgress, boolean inLocalServer) {
        if (inLocalServer || !(entity instanceof LivingEntity l) || !MinecraftClient.getInstance().debugHudEntryList.isEntryVisible(MineLittlePony.PONY_HITBOXES_DEBUG_HUD_ENTRY)) {
            return;
        }
        var renderer = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(l);
        if (renderer == null) {
            return;
        }

        Vec3d posJitter = entity.getLerpedPos(tickProgress).subtract(entity.getEntityPos());

        Box box = renderer.getEquineManager().getHitbox(l).offset(posJitter);
        GizmoDrawing.box(box, DrawStyle.stroked(0xFFFFFF00));


        float yaw = (l.isSleeping() && l.getSleepingDirection() != null ? l.getSleepingDirection().getPositiveHorizontalDegrees() : MathHelper.lerp(tickProgress, l.lastBodyYaw, l.bodyYaw)) * MathHelper.RADIANS_PER_DEGREE;
        Vec3d min = new Vec3d(0, 0, 0.3).rotateY(MathHelper.PI - yaw);

        box = box.offset(min.x, 0, min.z);
        GizmoDrawing.box(new Box(box.minX, box.minY, box.minZ, box.maxX, box.minY + (box.maxY - box.minY) * 0.6F, box.maxZ), DrawStyle.stroked(0xFFFF0000));
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
