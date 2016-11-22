package com.minelittlepony.model.part;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.PonyModelConstants;

public class PonyEars extends AbstractHeadPart implements PonyModelConstants {

    public PonyEars(AbstractPonyModel pony) {
        super(pony);
    }

    @Override
    public void init(float yOffset, float stretch) {

        this.pony.bipedHead.setTextureOffset(12, 16);
        this.pony.bipedHead.addBox(-4.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.pony.bipedHead.mirror = true;
        this.pony.bipedHead.addBox(2.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);

    }

}
