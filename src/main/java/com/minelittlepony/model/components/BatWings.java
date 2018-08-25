package com.minelittlepony.model.components;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPegasus;

public class BatWings<T extends AbstractPonyModel & IModelPegasus> extends PegasusWings<T> {

    public BatWings(T model, float yOffset, float stretch) {
        super(model, yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        int x = 57;

        leftWing = new ModelBatWing<T>(pegasus, false, false, yOffset, stretch, x, 16);
        rightWing = new ModelBatWing<T>(pegasus, true, false, yOffset, stretch, x - 1, 16);
    }

    @Override
    public ModelWing<T> getRight() {
        return rightWing;
    }

    @Override
    public void renderPart(float scale) {

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.3F, 1.3F, 1.3F);

        super.renderPart(scale);

        GlStateManager.popMatrix();
    }
}
