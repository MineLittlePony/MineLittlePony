package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.math.*;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.HorseCam;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.util.settings.Setting;

public final class DebugBoundingBoxRenderer {
    public static void drawHitboxes(Entity entity, float tickProgress) {
        if (!(entity instanceof LivingEntity l) || !MinecraftClient.getInstance().debugHudEntryList.isEntryVisible(MineLittlePony.PONY_HITBOXES_DEBUG_HUD_ENTRY)) {
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

        drawFillyCamRays(entity, tickProgress);
    }

    public static void drawFillyCamRays(Entity entity, float tickProgress) {
        if (!(entity instanceof ClientPlayerLikeEntity) || !MinecraftClient.getInstance().debugHudEntryList.isEntryVisible(MineLittlePony.PONY_FILLYCAM_RAYS_DEBUG_HUD_ENTRY)) {
            return;
        }
        Setting<Boolean> fillyCam = PonyConfig.getInstance().fillycam;

        fillyCam.set(false);
        final float vanillaHeight = entity.getEyeHeight(entity.getPose());
        fillyCam.set(true);
        final float alteredHeight = entity.getEyeHeight(entity.getPose());

        final float pitch = entity.getPitch(tickProgress);
        final float rescaledPitch = HorseCam.rescaleCameraPitch(entity, alteredHeight, vanillaHeight, pitch, tickProgress);

        final Vec3d entityPos = entity.getLerpedPos(tickProgress);

        var a = entityPos.add(0, vanillaHeight, 0);
        var b = HorseCam.getRaycastPos(entity, a, rescaledPitch, tickProgress);

        if (b != null) {
            GizmoDrawing.line(a, b, Colors.RED, 4);
        }

        a = entityPos.add(0, alteredHeight, 0);
        b = HorseCam.getRaycastPos(entity, a, pitch, tickProgress);
        if (b != null) {
            GizmoDrawing.line(a, b, Colors.WHITE, 4);
        }

        var corner = new Vec3d(b.x, b.y + (entityPos.y - b.y + vanillaHeight), b.z);

        GizmoDrawing.line(b, corner, Colors.YELLOW, 4);
        GizmoDrawing.line(a.withAxis(Direction.Axis.Y, corner.y), corner, Colors.BLUE, 4);
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
