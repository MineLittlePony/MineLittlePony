package com.minelittlepony.client.render.command;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface VertexTransforms {

    static BakedQuad inflateQuad(BakedQuad quad, float inflation) {
        return new BakedQuad(inflateQuad(quad.vertexData(), quad.face(), inflation), quad.tintIndex(), quad.face(), quad.sprite(), false, 1);
    }

    static int[] inflateQuad(int[] packedVertices, Direction face, float inflation) {
        int[] vertices = new int[packedVertices.length];
        System.arraycopy(packedVertices, 0, vertices, 0, vertices.length);

        Vec3i normal = face.getOpposite().getVector();
        Vec3i normalizedNormal = new Vec3i(Math.abs(normal.getX()), Math.abs(normal.getY()), Math.abs(normal.getZ()));
        Vector3f inflationNormal = new Vector3f();

        for (int i = 0; i < vertices.length; i += 8) {
            int vertexIndex = i / 8;
            int inner = vertexIndex > 0 && vertexIndex < 3 ? 1 : -1;
            int lower = vertexIndex < 2 ? 1 : -1;
            inflationNormal.set(
                normal.getX() + (normalizedNormal.getY() * lower) - (normal.getZ() * lower),
                normal.getY() + (normalizedNormal.getX() * inner) + (normalizedNormal.getZ() * inner),
                normal.getZ() + (normal.getY() * inner) + (normal.getX() * lower)
            ).normalize();
            vertices[i] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i]) - inflation * inflationNormal.x());
            vertices[i + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i + 1]) - inflation * inflationNormal.y());
            vertices[i + 2] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i + 2]) - inflation * inflationNormal.z());
        }

        return vertices;
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
