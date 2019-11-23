package com.minelittlepony.client.render.tileentities.skull;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.client.model.components.ModelPonyHead;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.pony.IPony;

public abstract class PonySkull implements ISkull {

    private static ModelPonyHead ponyHead = new ModelPonyHead();

    @Override
    public void preRender(boolean transparency) {

    }

    @Override
    public void bindPony(IPony pony) {
        ponyHead.metadata = pony.getMetadata();
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int lightUv, int overlayUv, float red, float green, float blue, float alpha) {
        ponyHead.render(stack, vertices, lightUv, overlayUv, red, green, blue, alpha);
    }
}