package com.minelittlepony.transformation;

import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

public class TransformFoal implements PonyTransformation {
    @Override
    public void transform(AbstractPonyModel model, BodyPart part) {
        if (model.isCrouching()) translate(0, -0.12F, 0);
        if (model.isSleeping) translate(0, -1.48F, 0.25F);
        if (model.isRiding) translate(0, -0.1F, 0);

        switch (part) {
            case NECK:
                translate(0, 0.76F, 0);
                scale(0.9F, 0.9F, 0.9F);
                if (model.isCrouching()) translate(0, -0.01F, 0.15F);
                break;
            case HEAD:
                translate(0, 0.76F, 0);
                scale(0.9F, 0.9F, 0.9F);
                break;
            case BODY:
            case TAIL:
                translate(0, 0.76F, -0.04F);
                scale(0.6F, 0.6F, 0.6F);
                break;
            case LEGS:
                translate(0, 0.89F, 0);
                scale(0.6F, 0.41F, 0.6F);
                if (model.isCrouching()) translate(0, 0.12F, 0);
                if (model.isGoingFast()) translate(0, -0.08F, 0);
                break;
        }
    }

}
