package com.minelittlepony.client.render.command;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Vec3i;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.mojang.blaze3d.platform.Transparency;

public interface VertexTransforms {

    static BakedQuad inflateQuad(BakedQuad quad, float inflation) {
        return inflateQuad(quad, quad.materialInfo(), inflation);
    }

    static BakedQuad inflateQuad(BakedQuad quad, BakedQuad.MaterialInfo material, float inflation) {
        Vector3fc[] inflatedVertices = new Vector3fc[BakedQuad.VERTEX_COUNT];

        Vec3i normal = quad.direction().getOpposite().getUnitVec3i();
        Vec3i normalizedNormal = new Vec3i(Math.abs(normal.getX()), Math.abs(normal.getY()), Math.abs(normal.getZ()));
        Vector3f inflationNormal = new Vector3f();

        for (int vertexIndex = 0; vertexIndex < inflatedVertices.length; vertexIndex++) {
            Vector3fc vertex = quad.position(vertexIndex);
            int inner = vertexIndex > 0 && vertexIndex < 3 ? 1 : -1;
            int lower = vertexIndex < 2 ? 1 : -1;
            inflatedVertices[vertexIndex] = inflationNormal.set(
                normal.getX() + (normalizedNormal.getY() * lower) - (normal.getZ() * lower),
                normal.getY() + (normalizedNormal.getX() * inner) + (normalizedNormal.getZ() * inner),
                normal.getZ() + (normal.getY() * inner) + (normal.getX() * lower)
            ).normalize().mul(-inflation).add(vertex, new Vector3f());
        }

        return new BakedQuad(
                inflatedVertices[0], inflatedVertices[1], inflatedVertices[2], inflatedVertices[3],
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                quad.direction(),
                material);
    }

    static BakedQuad.MaterialInfo materialOf(BakedQuad.MaterialInfo basis, RenderType renderType, Transparency transparency) {
        return new BakedQuad.MaterialInfo(basis.sprite(), ChunkSectionLayer.byTransparency(transparency), renderType, 0, false, 15);
    }

    static Vector3f getInflationNormal(int vertexIndex, Vector3fc normal) {
        int inner = vertexIndex % 4 < 2 ? -1 : 1;
        int lower = (vertexIndex + 1) % 2 == 1 ? -inner : inner;
        return new Vector3f(
                normal.x() + (Math.abs(normal.y()) * lower) - (normal.z() * lower),
                normal.y() + (Math.abs(normal.x()) * inner) + (Math.abs(normal.z()) * inner),
                normal.z() + (normal.y() * inner) + (normal.x() * lower)
        ).normalize();
    }
}
