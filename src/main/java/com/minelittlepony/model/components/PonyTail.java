package com.minelittlepony.model.components;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.render.plane.PlaneRenderer;

public class PonyTail extends PlaneRenderer implements IModelPart {

    private static final int SEGMENTS = 4;

    private final AbstractPonyModel theModel;

    private int tailStop = 0;

    public boolean isHidden = false;

    public PonyTail(AbstractPonyModel model) {
        super(model);
        theModel = model;
    }

    @Override
    public void init(float yOffset, float stretch) {
        for (int i = 0; i < SEGMENTS; i++) {
            addChild(new TailSegment(theModel, i, yOffset, stretch));
        }
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, float move, float swing, float bodySwing, float ticks) {
        rotateAngleZ = rainboom ? 0 : MathHelper.cos(move * 0.8F) * 0.2f * swing;
        rotateAngleY = bodySwing;

        if (theModel.isCrouching() && !rainboom) {
            rotateSneak();
        } else if (theModel.isRiding) {
            rotationPointZ = 13;
            rotationPointY = 3;
            rotateAngleX = PI / 5;
        } else {
            setRotationPoint(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_NOTSNEAK);
            if (rainboom) {
                rotateAngleX = ROTATE_90 + MathHelper.sin(move) / 10;
            } else {
                rotateAngleX = swing / 2;
            }

            if (!rainboom) {
                swingX(ticks);
            }
        }

        if (rainboom) {
            rotationPointY += 6;
            rotationPointZ++;
        }

        tailStop = theModel.metadata.getTail().ordinal();
    }

    private void swingX(float ticks) {
        float sinTickFactor = MathHelper.sin(ticks * 0.067f) * 0.05f;
        rotateAngleX += sinTickFactor;
        rotateAngleY += sinTickFactor;
    }

    private void rotateSneak() {
        setRotationPoint(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_SNEAK);
        rotateAngleX = -BODY_ROTATE_ANGLE_X_SNEAK + 0.1F;
    }

    @Override
    public void setVisible(boolean visible) {
        isHidden = !visible;
    }

    @Override
    public void renderPart(float scale) {
        render(scale);
    }

    private class TailSegment extends PlaneRenderer {

        private final int index;

        public TailSegment(ModelBase model, int index, float yOffset, float stretch) {
            super(model);
            this.index = index;

            offsetY = (float)index/4 + 0.063f;

            init(yOffset, stretch);
        }

        public void init(float yOffset, float stretch) {
            int texX = index % 2 * 4;

            around(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);

            if (index == 0) {
                tex(32, 0).addTopPlane(-2, 0, 2, 4, 4, stretch);
            }

            tex(36, texX).addEastPlane( 2, 0, 2, 4, 4, stretch)
                         .addWestPlane(-2, 0, 2, 4, 4, stretch);
            tex(32, texX).addBackPlane(-2, 0, 2, 4, 4, stretch)
                        .addFrontPlane(-2, 0, 6, 4, 4, stretch);
            tex(32, 0) .addBottomPlane(-2, 4, 2, 4, 4, stretch);
        }

        @Override
        public void render(float scale) {
            if (!isHidden && index < tailStop) {
                super.render(scale);
            }
        }
    }
}
