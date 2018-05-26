package com.minelittlepony.transformation;

import static net.minecraft.client.renderer.GlStateManager.translate;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

public class TransformNormal implements PonyTransformation {
    @Override
    public void transform(AbstractPonyModel model, BodyPart part) {
        if (model.isSleeping) translate(0, -0.61F, 0.25F);

        switch (part) {
            case NECK:
                if (model.isCrouching()) translate(-0.03F, 0.03F, 0.1F);
            default:
        }
    }
}
