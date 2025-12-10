package com.minelittlepony.client.render.command;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Vec3i;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface VertexTransforms {

    static BakedQuad inflateQuad(BakedQuad quad, float inflation) {
        Vector3fc[] inflatedVertices = new Vector3fc[BakedQuad.NUM_VERTICES];

        Vec3i normal = quad.face().getOpposite().getVector();
        Vec3i normalizedNormal = new Vec3i(Math.abs(normal.getX()), Math.abs(normal.getY()), Math.abs(normal.getZ()));
        Vector3f inflationNormal = new Vector3f();

        for (int vertexIndex = 0; vertexIndex < inflatedVertices.length; vertexIndex++) {
            Vector3fc vertex = quad.getPosition(vertexIndex);
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
                quad.tintIndex(), quad.face(), quad.sprite(), false, 1);
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
