package com.minelittlepony.transformation;

import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

public class TransformTall implements PonyTransformation {
    @Override
    public void transform(AbstractPonyModel model, BodyPart part) {
        if (model.isSleeping) translate(0, -0.5F, 0.25F);

        switch (part) {
            case NECK:
                translate(0, -0.09F, -0.01F);
                scale(1, 1.1F, 1);
                if (model.isCrouching()) translate(-0.02F, -0.02F, 0.1F);
                break;
            case HEAD:
                translate(0, -0.15F, 0.01F);
                if (model.isCrouching()) translate(0, 0.05F, 0);
                break;
            case BODY:
                translate(0, -0.1F, 0);
                break;
            case TAIL:
                translate(0, -0.1F, 0);
                break;
            case LEGS:
                translate(0, -0.25F, 0.03F);
                scale(1, 1.18F, 1);
                if (model.isGoingFast()) translate(0, 0.05F, 0);
                break;
        }
    }

}
