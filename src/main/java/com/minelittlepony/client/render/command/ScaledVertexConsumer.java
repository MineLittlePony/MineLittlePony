package com.minelittlepony.client.render.command;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;

import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.*;

final class ScaledVertexConsumer implements VertexConsumer {
    private int vertexIndex;

    private final VertexConsumer buffer;
    private float scale = 1;
    private final int color;
    private final PoseStack localTransform;
    private final RenderType renderType;

    public ScaledVertexConsumer(VertexConsumer buffer, RenderType renderType, int color, PoseStack localTransform) {
        this.buffer = buffer;
        this.color = color;
        this.localTransform = localTransform;
        this.renderType = renderType;
    }

    public VertexConsumer setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float width) {
        buffer.setLineWidth(width);
        return this;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        buffer.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int argb) {
        buffer.setColor(color);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        buffer.setColor(ARGB.red(color), ARGB.green(color), ARGB.blue(color), ARGB.alpha(color));
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        buffer.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        buffer.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        buffer.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        buffer.setNormal(x, y, z);
        return this;
    }

    @Override
    public void addVertex(float x, float y, float z, int c, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        var normal = localTransform.last().normal().invert().transform(new Vector3f(normalX, normalY, normalZ));
        var inflation = VertexTransforms.getInflationNormal(vertexIndex++, normal);
        inflation = localTransform.last().normal().invert().transform(inflation);
        float sc = scale / 32F;
        buffer.addVertex(
                x + inflation.x() * sc,
                y + inflation.y() * sc,
                z + inflation.z() * sc, color, u, v, OverlayTexture.NO_OVERLAY, LightCoordsUtil.FULL_BRIGHT, normalX, normalY, normalZ);
    }

    @Override
    public void putBlockBakedQuad(float x, float y, float z, BakedQuad quad, QuadInstance instance) {
        float sc = scale / 32F;
        instance.setColor(color);
        instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
        instance.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
        buffer.putBlockBakedQuad(x, y, z, VertexTransforms.inflateQuad(quad, VertexTransforms.materialOf(quad.materialInfo(), renderType, Transparency.TRANSLUCENT), sc), instance);
    }

    @Override
    public void putBakedQuad(PoseStack.Pose matrixEntry, BakedQuad quad, QuadInstance instance) {
        float sc = scale / 32F;
        instance.setColor(color);
        instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
        instance.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
        buffer.putBakedQuad(matrixEntry, VertexTransforms.inflateQuad(quad, VertexTransforms.materialOf(quad.materialInfo(), renderType, Transparency.TRANSLUCENT), sc), instance);
    }

    // Sodium
    // https://github.com/CaffeineMC/sodium/blob/dev/common/src/main/java/net/caffeinemc/mods/sodium/mixin/core/render/immediate/consumer/SheetedDecalTextureGeneratorMixin.java
    // @Override
    public boolean canUseIntrinsics() {
        return false;
    }
}
