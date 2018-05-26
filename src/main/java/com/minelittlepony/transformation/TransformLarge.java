package com.minelittlepony.transformation;

import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

public class TransformLarge implements PonyTransformation {
    @Override
    public void transform(AbstractPonyModel model, BodyPart part) {
        if (model.isSleeping) translate(0, -0.98F, 0.2F);

        switch (part) {
            case NECK:
                translate(0, -0.15F, -0.07F);
                if (model.isCrouching()) translate(-0.03F, 0.16F, 0.07F);
                break;
            case HEAD:
                translate(0, -0.17F, -0.04F);
                if (model.isSleeping) translate(0, 0, -0.1F);
                if (model.isCrouching()) translate(0, 0.15F, 0);
                break;
            case BODY:
                translate(0, -0.2F, -0.04F);
                scale(1.15F, 1.2F, 1.2F);
                break;
            case TAIL:
                translate(0, -0.2F, 0.08F);
                break;
            case LEGS:
                translate(0, -0.14F, 0);
                scale(1.15F, 1.12F, 1.15F);
                break;
        }
    }

}
