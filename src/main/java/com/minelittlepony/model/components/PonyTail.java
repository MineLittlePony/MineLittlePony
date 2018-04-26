package com.minelittlepony.model.components;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.pony.data.TailLengths;
import com.minelittlepony.render.plane.PlaneRenderer;

public class PonyTail extends PlaneRenderer {
    
    private final TailSegment[] segments = new TailSegment[4];
    
    private final AbstractPonyModel theModel;
    
    public PonyTail(AbstractPonyModel model) {
        super(model);
        theModel = model;
    }
    
    public void init(float yOffset, float stretch) {
        for (int i = 0; i < segments.length; i++) {
            addChild(segments[i] = new TailSegment(theModel, i, yOffset, stretch));
        }
    }
    
    public void setRotationAndAngles(boolean rainboom, float limbSwing, float limbSwingAmount, float bodySwing, float ticks) {
        swingZ(rainboom, limbSwing, limbSwingAmount);
        rotateAngleY = bodySwing;
        
        if (theModel.isSneak && !theModel.isFlying && !rainboom) {
            rotateSneak();
        } else if (theModel.isRiding) {
            rotationPointZ = 13;
            rotationPointY = 3;
            rotateAngleX = PI / 5;
        } else {
            setRotationPoint(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_NOTSNEAK);
            if (rainboom) {
                rotateAngleX = ROTATE_90 + MathHelper.sin(limbSwing) / 10;
            } else {
                rotateAngleX = limbSwingAmount / 2;
            }

            if (!rainboom) {
                swingX(ticks);
            }
        }

        if (rainboom) {
            rotationPointY += 6;
            rotationPointZ++;
        }
    }

    public void swingZ(boolean rainboom, float move, float swing) {
        rotateAngleZ = rainboom ? 0 : MathHelper.cos(move * 0.8F) * 0.2f * swing;
    }

    public void swingX(float tick) {
        float sinTickFactor = MathHelper.sin(tick * 0.067f) * 0.05f;
        rotateAngleX += sinTickFactor;
        rotateAngleY += sinTickFactor;
    }
    
    public void rotateSneak() {
        setRotationPoint(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_SNEAK);
        rotateAngleX = -BODY_ROTATE_ANGLE_X_SNEAK + 0.1F;
    }

    public void render(TailLengths tail, float scale) {
        int tailStop = tail.ordinal();

        for (int i = 0; i < segments.length; i++) {
            segments[i].isHidden = i >= tailStop;
        }

        super.render(scale);
    }
    
    private class TailSegment extends PlaneRenderer {
        
        public TailSegment(ModelBase model, int index, float yOffset, float stretch) {
            super(model);
            
            this.offsetY = ((float)index)/4 + 0.063f;
            
            init(index, yOffset, stretch);
        }
        
        public void init(int index, float yOffset, float stretch) {
            int texX = (index % 2) * 4;
            
            if (index == 0) {
                tex(32, 0).addTopPlane(-2, 0, 2, 4, 4, stretch);
            }
            
            around(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
            tex(36, texX)
                .addEastPlane(2, 0, 2, 4, 4, stretch)
                .addWestPlane(-2, 0, 2, 4, 4, stretch);
            tex(32, texX)
                .addBackPlane(-2, 0, 2, 4, 4, stretch)
                .addFrontPlane(-2, 0, 6, 4, 4, stretch);
            tex(32, 0)
                .addBottomPlane(-2, 4, 2, 4, 4, stretch);
        }
    }
}
