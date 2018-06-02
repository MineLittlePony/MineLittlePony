package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.render.plane.PlaneRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

public class PonyAccessory {

    private final AbstractPonyModel theModel;

    public PlaneRenderer bag;

    public <T extends AbstractPonyModel> PonyAccessory(T model) {
        theModel = model;

        if (theModel.metadata.hasBags()) {
            bag = new PlaneRenderer(theModel, 56, 19);
        }
    }

    public void setRotationAndAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        float angleY = 0;
        if (theModel.swingProgress > -9990.0F && !theModel.metadata.hasMagic()) {
            angleY = MathHelper.sin(MathHelper.sqrt(theModel.swingProgress) * PI * 2) * 0.02F;
        }
        bag.rotateAngleY = angleY;

    }

    public void render(float scale) {
        if (bag != null && theModel.metadata.hasBags()) {
            bag.render(scale);
        }
    }

    public void initPositions(float yOffset, float stretch) {
        if (bag != null && theModel.metadata.hasBags()) {
            bag.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
                    .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                    .tex(56, 25).addBackPlane(-7, -5, -4, 3, 6, stretch) //right bag front
                    .addBackPlane(4, -5, -4, 3, 6, stretch) //left bag front
                    .tex(59, 25).addBackPlane(-7, -5, 4, 3, 6, stretch) //right bag back
                    .addBackPlane(4, -5, 4, 3, 6, stretch) //left bag back
                    .tex(56, 19).addWestPlane(-7, -5, -4, 6, 8, stretch) //right bag outside
                    .addWestPlane(7, -5, -4, 6, 8, stretch) //left bag outside
                    .addWestPlane(-4.01f, -5, -4, 6, 8, stretch) //right bag inside
                    .addWestPlane(4.01f, -5, -4, 6, 8, stretch) //left bag inside
                    .tex(56, 31).addTopPlane(-4, -4.5F, -1, 8, 1, stretch) //strap front
                    .addTopPlane(-4, -4.5F, 0, 8, 1, stretch) //strap back
                    .addBackPlane(-4, -4.5F, 0, 8, 1, stretch)
                    .addFrontPlane(-4, -4.5F, 0, 8, 1, stretch)
                    .child(0).tex(56, 16).addTopPlane(2, -5, -13, 8, 3, stretch) //left bag top
                    .addTopPlane(2, -5, -2, 8, 3, stretch) //right bag top
                    .tex(56, 22).flipZ().addBottomPlane(2, 1, -13, 8, 3, stretch) //left bag bottom
                    .flipZ().addBottomPlane(2, 1, -2, 8, 3, stretch) //right bag bottom
                    .rotateAngleY = 4.712389F;
        }
    }
}
