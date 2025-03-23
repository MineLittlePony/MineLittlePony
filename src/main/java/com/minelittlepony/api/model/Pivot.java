package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;

public record Pivot(float x, float y, float z) {
    public void set(ModelPart part) {
        part.setOrigin(x, y, z);
    }

    public void add(ModelPart part) {
        part.setOrigin(part.originX + x, part.originY + y, part.originZ + z);
    }
}