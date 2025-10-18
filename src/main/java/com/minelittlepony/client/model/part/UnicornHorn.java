package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.MagicGlow;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class UnicornHorn<T extends PonyRenderState> implements SubModel<T> {

    private final ModelPart horn;
    private final ModelPart glow;

    private int tint;

    public UnicornHorn(ModelPart tree) {
        horn = tree.getChild("bone");
        glow = tree.getChild("corona");
    }

    @Override
    public void accept(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        horn.render(stack, vertices, overlay, light, color);
    }

    @Override
    public void render(PonyModel<T> model, T state, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        if (tint != 0) {
            matrices.push();
            model.transformAccessory(state, BodyPart.HEAD, matrices);
            queue.submitModelPart(glow, matrices, MagicGlow.getRenderLayer(),
                    LightmapTextureManager.MAX_LIGHT_COORDINATE,
                    OverlayTexture.DEFAULT_UV, null, false, false, ColorHelper.withAlpha(1F, tint),
                    null, 0);
            matrices.pop();
        }
    }

    @Override
    public void setVisible(boolean visible, T state) {
        tint = visible && state.hasMagicGlow() && state.headVisible && state.hornGlowVisible ? state.glowColor : 0;
        horn.visible = visible && state.race.hasHorn() && state.headVisible;
        glow.visible = tint != 0;
    }

    @Override
    public void setAngles(PonyModel<T> model, T state) {
        horn.resetTransform();
        glow.resetTransform();
        model.transform(state, BodyPart.HORN, horn);
        model.transform(state, BodyPart.HORN, glow);
    }
}
