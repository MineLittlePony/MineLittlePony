package com.minelittlepony.model.components;

import com.minelittlepony.pony.data.PonyGender;
import com.minelittlepony.render.model.PlaneRenderer;

import net.minecraft.client.model.ModelBase;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.capabilities.ICapitated;

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
            .tex(10, 14).south(-2, 2, -5, 4, 2, stretch)
            .tex(11, 13).south(-1, 1, -5, 2, 1, stretch)
            .tex(9, 14)   .top(-2, 2, -5, 1, 1, stretch)
            .tex(14, 14)  .top( 1, 2, -5, 1, 1, stretch)
            .tex(11, 12)  .top(-1, 1, -5, 2, 1, stretch)
            .tex(18, 7).bottom(-2, 4, -5, 4, 1, stretch)
            .tex(9, 14)  .west(-2, 2, -5, 2, 1, stretch)
            .tex(14, 14) .east( 2, 2, -5, 2, 1, stretch)
            .tex(11, 12) .west(-1, 1, -5, 1, 1, stretch)
            .tex(12, 12) .east( 1, 1, -5, 1, 1, stretch);
        stallion.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .tex(10, 13).south(-2, 1, -5, 4, 3, stretch)
                .tex(10, 13)  .top(-2, 1, -5, 4, 1, stretch)
                .tex(18, 7).bottom(-2, 4, -5, 4, 1, stretch)
                .tex(10, 13) .west(-2, 1, -5, 3, 1, stretch)
                .tex(13, 13) .east( 2, 1, -5, 3, 1, stretch);
    }

    public void setGender(PonyGender gender) {
        boolean show = !head.hasHeadGear() && !isHidden && MineLittlePony.getConfig().snuzzles;

        mare.isHidden = !show || gender == PonyGender.STALLION;
        stallion.isHidden = !show || gender == PonyGender.MARE;
    }
}
