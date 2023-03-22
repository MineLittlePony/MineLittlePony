package com.minelittlepony.client.model.part;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.client.render.MagicGlow;
import com.minelittlepony.common.util.Color;

public class UnicornHorn implements IPart {

    private final ModelPart horn;
    private final ModelPart glow;

    protected boolean visible = true;

    public UnicornHorn(ModelPart tree) {
        horn = tree.getChild("bone");
        glow = tree.getChild("corona");
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
        if (visible) {
            horn.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        }
    }

    public void renderMagic(MatrixStack stack, VertexConsumer verts, int tint) {
        if (visible) {
            Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            VertexConsumer vertices = immediate.getBuffer(MagicGlow.getRenderLayer());

            glow.render(stack, vertices, OverlayTexture.DEFAULT_UV, 0x0F00F0, Color.r(tint), Color.g(tint), Color.b(tint), 0.4F);
        }
    }

    @Override
    public void setVisible(boolean visible, ModelAttributes attributes) {
        this.visible = visible;
    }
}
