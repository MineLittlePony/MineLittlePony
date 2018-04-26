package com.minelittlepony.model.components;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.render.PonyRenderer;

public class ModelWing {
    public PonyRenderer extended;
    public PonyRenderer folded;

    private boolean mirror;
    
    public ModelWing(AbstractPonyModel pony, boolean mirror, float x, float y, float scale, int texY) {
        this.mirror = mirror;
        
        folded = new PonyRenderer(pony, 56, texY)
                .around(HEAD_RP_X, WING_FOLDED_RP_Y, WING_FOLDED_RP_Z);
        extended = new PonyRenderer(pony, 56, texY + 3)
                .around(HEAD_RP_X, WING_FOLDED_RP_Y, WING_FOLDED_RP_Z).mirror(mirror);
        
        addCloseWing(x, y, scale);
        addFeathers(mirror, y, scale);
    }
    
    private void addCloseWing(float x, float y, float scale) {
        folded.box(x, 5f, 2, 2, 6, 2, scale)
              .box(x, 5f, 4, 2, 8, 2, scale)
              .box(x, 5f, 6, 2, 6, 2, scale)
              .rotateAngleX = ROTATE_90;
    }

    private void addFeathers(boolean mirror, float y, float scale) {
        float r = mirror ? -1 : 1;
        extended.cubeList.clear();
        extended.around(r * LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + y, LEFT_WING_EXT_RP_Z);
        addFeather(0, r, y,  6,     0,    8, scale + 0.1F);
        addFeather(1, r, y, -1.2F, -0.2F, 8, scale + 0.2F) .rotateAngleX = -0.85F;
        addFeather(2, r, y,  1.8F,  1.3F, 8, scale - 0.1F) .rotateAngleX = -0.75F;
        addFeather(3, r, y,  5,     2,    8, scale)        .rotateAngleX = -0.5F;
        addFeather(4, r, y,  0,   -0.2F,  6, scale + 0.3F);
        addFeather(5, r, y,  0,    0.2F,  3, scale + 0.19F).rotateAngleX = -0.85F;
    }

    private PonyRenderer addFeather(int i, float r, float Y, float y, float z, int h, float scale) {
        return extended.child(i).around(0, 0, 0).box(-0.5f, y, z, 1, h, 2, scale);
    }

    public void rotateWalking(float swing) {
        folded.rotateAngleY = swing * 0.2F;
    }

    
    public void render(boolean extend, float scale) {
        extended.rotationPointX = (mirror ? -1 : 1) * LEFT_WING_EXT_RP_X;
        extended.rotationPointY = LEFT_WING_EXT_RP_Y;
        
        extended.rotateAngleY = 3;
        if (extend) {
            extended.render(scale);
        } else {
            folded.render(scale);
        }
    }

    public void rotateFlying(float angle) {
        extended.rotateAngleZ = angle;
    }

}
