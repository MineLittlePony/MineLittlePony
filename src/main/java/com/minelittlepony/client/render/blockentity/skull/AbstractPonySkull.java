package com.minelittlepony.client.render.blockentity.skull;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.client.model.PonySkullModel;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.ISkull;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.model.ModelType;

public abstract class AbstractPonySkull implements ISkull {

    private PonySkullModel ponyHead = ModelType.SKULL.createModel();

    @Override
    public void setAngles(float angle, float poweredTicks) {
        ponyHead.setHeadRotation(poweredTicks, angle, 0);
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
