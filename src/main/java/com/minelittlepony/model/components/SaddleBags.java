package com.minelittlepony.model.components;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.render.plane.PlaneRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class SaddleBags implements IModelPart {

    private PlaneRenderer leftBag;
    private PlaneRenderer rightBag;

    private PlaneRenderer strap;

    private boolean hangLow = false;

    public SaddleBags(AbstractPonyModel model) {
        leftBag = new PlaneRenderer(model, 56, 19);
        rightBag = new PlaneRenderer(model, 56, 19);
        strap = new PlaneRenderer(model, 56, 19);
    }

    @Override
    public void init(float yOffset, float stretch) {
        float y = -0.5F;
        int x = 4;
        int z = -2;

        strap.offset(-x, y + 0.2F, z + 3).around(0, 4, 4)
        .tex(56, 31).addTopPlane(0, 0, 0, 8, 1, stretch)
                    .addTopPlane(0, 0, 1, 8, 1, stretch)
                   .addBackPlane(0, 0, 2, 8, 1, stretch)
                  .addFrontPlane(0, 0, 0, 8, 1, stretch);

        leftBag.offset(x, y, z).around(0, 4, 4)
                .tex(56, 25).addBackPlane(0, 0, 0, 3, 6, stretch)
                .tex(59, 25).addBackPlane(0, 0, 8, 3, 6, stretch)
                .tex(56, 19).addWestPlane(3, 0, 0, 6, 8, stretch)
                            .addWestPlane(0, 0, 0, 6, 8, stretch)
                .child(0).offset(z, y, -x).tex(56, 16)
                                     .addTopPlane(0, 0, -3, 8, 3, stretch)
              .tex(56, 22).flipZ().addBottomPlane(0, 6, -3, 8, 3, stretch)
                         .rotateAngleY = ROTATE_270;

        x += 3;

        rightBag.offset(-x, y, z).around(0, 4, 4)
                .tex(56, 25).addBackPlane(0, 0, 0, 3, 6, stretch)
                .tex(59, 25).addBackPlane(0, 0, 8, 3, 6, stretch)
                .tex(56, 19).addWestPlane(3, 0, 0, 6, 8, stretch)
                            .addWestPlane(0, 0, 0, 6, 8, stretch)
                   .child(0).offset(z, y, x).tex(56, 16)
                            .flipZ().addTopPlane(0, 0, -3, 8, 3, stretch)
             .tex(56, 22).flipZ().addBottomPlane(0, 6, -3, 8, 3, stretch)
                 .rotateAngleY = ROTATE_270;


    }

    @Override
    public void setRotationAndAngles(boolean rainboom, float move, float swing, float bodySwing, float ticks) {
        float pi = PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662f;
        float srt = swing / 6;

        bodySwing = MathHelper.cos(mve + pi) * srt;

        leftBag.rotateAngleX = bodySwing;
        rightBag.rotateAngleX = bodySwing;

        leftBag.rotateAngleZ = bodySwing;
        rightBag.rotateAngleZ = -bodySwing;
    }

    public void sethangingLow(boolean veryLow) {
        hangLow = veryLow;
    }

    @Override
    public void renderPart(float scale) {
        if (hangLow) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0.3F, 0);
        }

        leftBag.render(scale);
        rightBag.render(scale);
        strap.render(scale);

        if (hangLow) {
            GlStateManager.popMatrix();
        }
    }

}
