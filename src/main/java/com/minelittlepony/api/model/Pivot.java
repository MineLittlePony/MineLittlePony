package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;

public record Pivot(float x, float y, float z) {
    public void set(ModelPart part) {
        part.setPivot(x, y, z);
    }

    public void add(ModelPart part) {
        part.setPivot(part.pivotX + x, part.pivotY + y, part.pivotZ + z);
    }
}