package com.minelittlepony.client.model.part;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.client.render.MagicGlow;

public class UnicornHorn implements SubModel {

    private final ModelPart horn;
    private final ModelPart glow;

    protected boolean visible = true;

    public UnicornHorn(ModelPart tree) {
        horn = tree.getChild("bone");
        glow = tree.getChild("corona");
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color, ModelAttributes attributes) {
        horn.render(stack, vertices, overlay, light, color);
    }

    public void renderMagic(MatrixStack stack, VertexConsumer verts, int tint) {
        if (glow.visible) {
            Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            VertexConsumer vertices = immediate.getBuffer(MagicGlow.getRenderLayer());
            glow.render(stack, vertices, OverlayTexture.DEFAULT_UV, 0x0F00F0, (tint & 0xFFFFFF) | (102 << 24));
        }
    }

    @Override
    public void setVisible(boolean visible, ModelAttributes attributes) {
        horn.visible = this.visible && visible && attributes.headVisible;
        glow.visible = this.visible && visible && attributes.headVisible;
    }
}
