package com.minelittlepony.client.model.part;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.model.IPart;
import com.minelittlepony.util.math.Color;

import javax.annotation.Nullable;

import java.util.UUID;

public class UnicornHorn implements IPart {

    private ModelPart horn;
    private ModelPart glow;

    protected boolean visible = true;

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, @Nullable UUID interpolatorId) {
        if (visible) {
            horn.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        }
    }

    public void renderMagic(MatrixStack stack, int tint) {
        if (visible) {
            horn.rotate(stack);

            VertexConsumer vertices = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getTranslucentNoCrumbling());
            glow.render(stack, vertices, OverlayTexture.DEFAULT_UV, 0x0F00F0, Color.r(tint), Color.g(tint), Color.b(tint), 0.4F);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
