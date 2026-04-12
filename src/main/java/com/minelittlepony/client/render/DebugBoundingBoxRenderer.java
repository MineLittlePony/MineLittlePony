package com.minelittlepony.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.HorseCam;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.util.settings.Setting;

public final class DebugBoundingBoxRenderer {
    public static void drawHitboxes(Entity entity, float tickProgress) {
        if (!(entity instanceof LivingEntity l) || !Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MineLittlePony.PONY_HITBOXES_DEBUG_HUD_ENTRY)) {
            return;
        }
        var renderer = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(l);
        if (renderer == null) {
            return;
        }

        Vec3 posJitter = entity.getPosition(tickProgress).subtract(entity.position());

        AABB box = renderer.getEquineManager().getHitbox(l).move(posJitter);
        Gizmos.cuboid(box, GizmoStyle.stroke(0xFFFFFF00));


        float yaw = (l.isSleeping() && l.getBedOrientation() != null ? l.getBedOrientation().toYRot() : Mth.lerp(tickProgress, l.yBodyRotO, l.yBodyRot)) * Mth.DEG_TO_RAD;
        Vec3 min = new Vec3(0, 0, 0.3).yRot(Mth.PI - yaw);

        box = box.move(min.x, 0, min.z);
        Gizmos.cuboid(new AABB(box.minX, box.minY, box.minZ, box.maxX, box.minY + (box.maxY - box.minY) * 0.6F, box.maxZ), GizmoStyle.stroke(0xFFFF0000));

        drawFillyCamRays(entity, tickProgress);
    }

    public static void drawFillyCamRays(Entity entity, float tickProgress) {
        if (!(entity instanceof ClientAvatarEntity) || !Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MineLittlePony.PONY_FILLYCAM_RAYS_DEBUG_HUD_ENTRY)) {
            return;
        }
        Setting<Boolean> fillyCam = PonyConfig.getInstance().fillycam;

        fillyCam.set(false);
        final float vanillaHeight = entity.getEyeHeight(entity.getPose());
        fillyCam.set(true);
        final float alteredHeight = entity.getEyeHeight(entity.getPose());

        final float pitch = entity.getViewXRot(tickProgress);
        final float rescaledPitch = HorseCam.rescaleCameraPitch(entity, alteredHeight, vanillaHeight, pitch, tickProgress);

        final Vec3 entityPos = entity.getPosition(tickProgress);

        var a = entityPos.add(0, vanillaHeight, 0);
        var b = HorseCam.getRaycastPos(entity, a, rescaledPitch, tickProgress);
        if (b != null) {
            Gizmos.line(a, b, CommonColors.RED, 4);
        }

        a = entityPos.add(0, alteredHeight, 0);
        b = HorseCam.getRaycastPos(entity, a, pitch, tickProgress);
        if (b != null) {
            Gizmos.line(a, b, CommonColors.WHITE, 4);
        }

        var corner = new Vec3(b.x, b.y + (entityPos.y - b.y + vanillaHeight), b.z);

        Gizmos.line(b, corner, CommonColors.YELLOW, 4);
        Gizmos.line(a.with(Direction.Axis.Y, corner.y), corner, CommonColors.BLUE, 4);
    }

    public static AABB getBoundingBox(double x, double y, double z, float scale, float width, float height) {
        width *= scale;
        height *= scale;
        return new AABB(x - width, y, z - width, x + width, y + height, z + width);
    }

    public static AABB applyScale(float scale, AABB box) {
        double w = (box.maxX - box.minX) * 0.5F,
                h = (box.maxY - box.minY),
                d = (box.maxZ - box.minZ) * 0.5F,
                x = box.minX + w,
                z = box.minZ + d;
        w *= scale;
        d *= scale;
        return new AABB(
                x - w, box.minY, z - d,
                x + w, box.minY + h * scale, z + d
        );
    }
}
