package com.minelittlepony.client.render.command;

import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;

import org.joml.Vector3f;

final class ScaledVertexConsumer implements VertexConsumer {
    private int vertexIndex;

    private final VertexConsumer buffer;
    private final float scale;
    private final int color;
    private final MatrixStack localTransform;

    public ScaledVertexConsumer(VertexConsumer buffer, float scale, int color, MatrixStack localTransform) {
        this.buffer = buffer;
        this.scale = scale;
        this.color = color;
        this.localTransform = localTransform;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        buffer.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        buffer.color(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), ColorHelper.getAlpha(color));
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        buffer.texture(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        buffer.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        buffer.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        buffer.normal(x, y, z);
        return this;
    }

    @Override
    public void vertex(float x, float y, float z, int c, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        var normal = localTransform.peek().getNormalMatrix().invert().transform(new Vector3f(normalX, normalY, normalZ));
        var inflation = VertexTransforms.getInflationNormal(vertexIndex++, normal);
        inflation = localTransform.peek().getNormalMatrix().invert().transform(inflation);
        float sc = (scale - 1F) / 3F;
        buffer.vertex(
                x + inflation.x() * sc,
                y + inflation.y() * sc,
                z + inflation.z() * sc, color, u, v, OverlayTexture.DEFAULT_UV, LightmapTextureManager.MAX_LIGHT_COORDINATE, normalX, normalY, normalZ);
    }

    @Override
    public void quad(
            MatrixStack.Entry matrixEntry,
            BakedQuad quad,
            float[] brightnesses,
            float red,
            float green,
            float blue,
            float alpha,
            int[] lights,
            int overlay,
            boolean colorize
        ) {
        buffer.quad(matrixEntry, VertexTransforms.inflateQuad(quad, scale), brightnesses,
                ColorHelper.getRedFloat(color),
                ColorHelper.getGreenFloat(color),
                ColorHelper.getBlueFloat(color),
                ColorHelper.getAlphaFloat(color),
                lights, OverlayTexture.DEFAULT_UV, colorize);
    }

    // Sodium
    // https://github.com/CaffeineMC/sodium/blob/dev/common/src/main/java/net/caffeinemc/mods/sodium/mixin/core/render/immediate/consumer/SheetedDecalTextureGeneratorMixin.java
    // @Override
    public boolean canUseIntrinsics() {
        return false;
    }
}
