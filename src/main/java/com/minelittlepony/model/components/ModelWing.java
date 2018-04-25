package com.minelittlepony.model.components;

import net.minecraft.client.model.ModelRenderer;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;

// TODO: Combine each wing into one ModelRenderer with multiple boxes, not multiple ModelRenderers each with one box.
public class ModelWing {
    public ModelRenderer[] folded = new ModelRenderer[3],
                           extended = new ModelRenderer[6];

    public ModelWing(AbstractPonyModel pony, boolean mirror, float xOffset, float yOffset, float stretch, int texY) {

        // TODO: Don't add the model to the pony if you're just going to remove it again.
        for (int i = 0; i < folded.length; i++) {
            folded[i] = new ModelRenderer(pony, 56, texY);
            pony.boxList.remove(folded[i]);
        }

        for (int i = 0; i < extended.length; i++) {
            extended[i] = new ModelRenderer(pony, 56, texY + 3);
            pony.boxList.remove(extended[i]);
            // this seems to hide the wings being a different size when folded
            extended[i].mirror = mirror;
        }

        init(xOffset, yOffset, stretch);
        addFeathers(mirror ? -1 : 1, yOffset, stretch);
    }

    private void addFeathers(float r, float y, float scale) {
        addFeather(0, r, y,  6.0F,  0.0F, 8, scale + 0.1F);
        addFeather(1, r, y, -1.2F, -0.2F, 8, scale + 0.2F);
        addFeather(2, r, y,  1.8F,  1.3F, 8, scale - 0.1F);
        addFeather(3, r, y,  5.0F,  2.0F, 8, scale);
        addFeather(4, r, y,  0.0F, -0.2F, 6, scale + 0.3F);
        addFeather(5, r, y,  0.0F,  0.2F, 3, scale + 0.19F);
    }

    private void addFeather(int i, float r, float Y, float y, float z, int h, float scale) {
        extended[i].addBox(-0.5f, y, z, 1, h, 2, scale);
        extended[i].setRotationPoint(r * LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + Y, LEFT_WING_EXT_RP_Z);
    }

    private void init(float x, float y, float scale) {
        initFeather(folded[0], y, x, 2F, 6, scale);
        initFeather(folded[1], y, x, 4F, 8, scale);
        initFeather(folded[2], y, x, 6F, 6, scale);
    }

    private void initFeather(ModelRenderer wing, float y, float x, float z, int h, float scale) {
        wing.addBox(x, 5f, z, 2, h, 2, scale);
        wing.setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + y, WING_FOLDED_RP_Z);
        wing.rotateAngleX = ROTATE_90;
    }

    public void updateModelRotation(float swing) {
        for (ModelRenderer feather : folded) {
            feather.rotateAngleY = swing * 0.2F;
        }
    }

    public void rotate(float angle) {
        for (ModelRenderer feather : folded) {
            feather.rotateAngleX = angle;
        }
        // Special
        extended[1].rotateAngleX -= 0.85F;
        extended[2].rotateAngleX -= 0.75F;
        extended[3].rotateAngleX -= 0.5F;
        extended[5].rotateAngleX -= 0.85F;
    }

    public void render(boolean standing, float scale) {
        for (ModelRenderer feather : (standing ? folded : extended)) {
            feather.render(scale);
        }
    }

    public void rotateSneaked(float rotZ) {
        for (ModelRenderer feather : extended) {
            feather.rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
            feather.rotateAngleZ = rotZ;
        }
    }

    public void rotateUnsneaked(float angle) {
        for (ModelRenderer aLeftWingExt : extended) {
            aLeftWingExt.rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
            aLeftWingExt.rotateAngleZ = -angle;
        }
    }
}
