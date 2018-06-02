package com.minelittlepony.model.components;

import com.minelittlepony.pony.data.PonyGender;
import com.minelittlepony.render.plane.PlaneRenderer;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;

public class PonySnout {

    private PlaneRenderer mare;
    private PlaneRenderer stallion;

    public PonySnout(AbstractPonyModel pony) {
        mare = new PlaneRenderer(pony);
        stallion = new PlaneRenderer(pony);

        pony.bipedHead.addChild(stallion);
        pony.bipedHead.addChild(mare);
    }

    public void rotate(float x, float y, float z) {
        mare.rotate(x, y, z);
        stallion.rotate(x, y, z);
    }

    public void init(float yOffset, float stretch) {
        mare.offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
            .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .tex(10, 14) .addBackPlane(-2, 2, -5, 4, 2, stretch)
            .tex(11, 13) .addBackPlane(-1, 1, -5, 2, 1, stretch)
            .tex(9, 14)   .addTopPlane(-2, 2, -5, 1, 1, stretch)
            .tex(14, 14)  .addTopPlane( 1, 2, -5, 1, 1, stretch)
            .tex(11, 12)  .addTopPlane(-1, 1, -5, 2, 1, stretch)
            .tex(18, 7).addBottomPlane(-2, 4, -5, 4, 1, stretch)
            .tex(9, 14)  .addWestPlane(-2, 2, -5, 2, 1, stretch)
            .tex(14, 14) .addEastPlane( 2, 2, -5, 2, 1, stretch)
            .tex(11, 12) .addWestPlane(-1, 1, -5, 1, 1, stretch)
            .tex(12, 12) .addEastPlane( 1, 1, -5, 1, 1, stretch);
        stallion.offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .tex(10, 13) .addBackPlane(-2, 1, -5, 4, 3, stretch)
                .tex(10, 13)  .addTopPlane(-2, 1, -5, 4, 1, stretch)
                .tex(18, 7).addBottomPlane(-2, 4, -5, 4, 1, stretch)
                .tex(10, 13) .addWestPlane(-2, 1, -5, 3, 1, stretch)
                .tex(13, 13) .addEastPlane( 2, 1, -5, 3, 1, stretch);
    }

    public void setGender(PonyGender gender) {
        mare.isHidden = gender == PonyGender.STALLION;
        stallion.isHidden = !mare.isHidden;
    }
}
