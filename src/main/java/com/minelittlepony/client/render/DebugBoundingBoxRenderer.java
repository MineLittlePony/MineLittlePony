package com.minelittlepony.client.render;

import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import com.minelittlepony.api.model.RenderPass;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public final class DebugBoundingBoxRenderer {
    public static <T extends PonyRenderState> void render(T state, MatrixStack stack, OrderedRenderCommandQueue queue) {
        if (RenderPass.getCurrent() != RenderPass.WORLD || state.hitbox == null) {
            return;
        }

        if (state.hitbox == null) {
            return;
        }

        Box box = getBoundingBox(state).offset(
                -state.x,
                -state.y + (state instanceof PlayerPonyRenderState s ? s.baseScale * s.yOffset : 0),
                -state.z
        );
        queue.submitCustom(stack, RenderLayer.getLines(), (entry, vertices) -> VertexRendering.drawBox(entry, vertices, box, 1, 1, 0, 1));
    }

    public static Box getBoundingBox(PonyRenderState state) {
        return getBoundingBox(state.x, state.y, state.z, state.attributes.size.scaleFactor(), state.width, state.height);
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
