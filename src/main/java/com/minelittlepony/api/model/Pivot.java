package com.minelittlepony.api.model;

import net.minecraft.client.model.geom.ModelPart;

public record Pivot(float x, float y, float z) {
    public static final Pivot ZERO = new Pivot(0, 0, 0);

    public void set(ModelPart part) {
        part.setPos(x, y, z);
    }

    public void add(ModelPart part) {
        part.setPos(part.x + x, part.y + y, part.z + z);
    }
}