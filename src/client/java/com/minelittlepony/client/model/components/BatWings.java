package com.minelittlepony.client.model.components;

import net.minecraft.client.renderer.GlStateManager;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.model.IPegasus;

import java.util.UUID;

public class BatWings<T extends AbstractPonyModel & IPegasus> extends PegasusWings<T> {

    public BatWings(T model, float yOffset, float stretch) {
        super(model, yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        leftWing = new ModelBatWing<>(pegasus, false, false, yOffset, stretch, 16);
        rightWing = new ModelBatWing<>(pegasus, true, false, yOffset, stretch, 16);
    }

    @Override
    public ModelWing<T> getRight() {
        return rightWing;
    }

    @Override
    public void renderPart(float scale, UUID interpolatorId) {

        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.3F, 1.3F, 1.3F);

        super.renderPart(scale, interpolatorId);

        GlStateManager.popMatrix();
    }
}
