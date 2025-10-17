package com.minelittlepony.client.model.part;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;

import com.minelittlepony.api.model.SubModel;
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
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        horn.render(stack, vertices, overlay, light, color);
    }

    public void renderMagic(MatrixStack stack, VertexConsumer verts) {
        if (tint != 0) {
            Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer vertices = immediate.getBuffer(MagicGlow.getRenderLayer());
            glow.render(stack, vertices, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, ColorHelper.withAlpha(0.5F, tint));
        }
    }

    @Override
    public void setVisible(boolean visible, T state) {
        tint = visible && state.hasMagicGlow() && state.headVisible && state.hornGlowVisible ? state.glowColor : 0;
        horn.visible = visible && state.race.hasHorn() && state.headVisible;
        glow.visible = tint != 0;
    }
}
