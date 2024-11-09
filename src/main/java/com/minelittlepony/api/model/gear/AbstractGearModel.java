package com.minelittlepony.api.model.gear;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public abstract class AbstractGearModel extends Model implements Gear {

    private final float stackingHeight;

    public AbstractGearModel(ModelPart root, float stackingHeight) {
        super(root, RenderLayer::getEntitySolid);
        this.stackingHeight = stackingHeight;
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color, UUID interpolatorId) {
        render(stack, vertices, overlay, light, color);
    }

    @Override
    public boolean isStackable() {
        return stackingHeight > 0;
    }

    @Override
    public float getStackingHeight() {
        return stackingHeight;
    }
}
