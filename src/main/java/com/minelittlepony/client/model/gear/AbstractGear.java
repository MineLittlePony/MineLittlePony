package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.model.gear.IGear;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractGear extends Model implements IGear {

    private final List<ModelPart> parts = new ArrayList<>();

    public AbstractGear() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 64;
        textureHeight = 64;
    }

    @Override
    public void accept(ModelPart t) {
        parts.add(t);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        parts.forEach(part -> {
            part.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
        });
    }
}
