package com.minelittlepony.model.components;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.capabilities.ICapitated;
import com.minelittlepony.pony.data.PonyGender;
import com.minelittlepony.render.plane.PlaneRenderer;
import net.minecraft.client.model.ModelBase;

public class PonySnout {

    public boolean isHidden = false;

    private PlaneRenderer mare;
    private PlaneRenderer stallion;

    private final ICapitated head;

    public <T extends ModelBase & ICapitated> PonySnout(T pony) {
        this(pony, 0, 0, 0);
    }

    public <T extends ModelBase & ICapitated> PonySnout(T pony, int x, int y, int z) {
        head = pony;

        mare = new PlaneRenderer(pony).offset(HEAD_CENTRE_X + x, HEAD_CENTRE_Y + y, HEAD_CENTRE_Z + z);
        stallion = new PlaneRenderer(pony).offset(HEAD_CENTRE_X + x, HEAD_CENTRE_Y + y, HEAD_CENTRE_Z + z);

        pony.getHead().addChild(stallion);
        pony.getHead().addChild(mare);
    }

    public void rotate(float x, float y, float z) {
        mare.rotate(x, y, z);
        stallion.rotate(x, y, z);
    }

    public void init(float yOffset, float stretch) {
        mare.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
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
        stallion.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .tex(10, 13) .addBackPlane(-2, 1, -5, 4, 3, stretch)
                .tex(10, 13)  .addTopPlane(-2, 1, -5, 4, 1, stretch)
                .tex(18, 7).addBottomPlane(-2, 4, -5, 4, 1, stretch)
                .tex(10, 13) .addWestPlane(-2, 1, -5, 3, 1, stretch)
                .tex(13, 13) .addEastPlane( 2, 1, -5, 3, 1, stretch);
    }

    public void setGender(PonyGender gender) {
        boolean show = !head.hasHeadGear() && !isHidden && MineLittlePony.getConfig().getSnuzzles().get();

        mare.isHidden = !show || gender == PonyGender.STALLION;
        stallion.isHidden = !show || gender == PonyGender.MARE;
    }
}
