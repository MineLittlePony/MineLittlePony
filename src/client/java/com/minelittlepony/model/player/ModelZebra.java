package com.minelittlepony.model.player;

import net.minecraft.client.renderer.GlStateManager;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.render.model.PonyRenderer;

public class ModelZebra extends ModelEarthPony {

    public PonyRenderer bristles;

    public ModelZebra(boolean useSmallArms) {
        super(useSmallArms);
    }

    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new Armour(), new Armour());
    }

    @Override
    public void transform(BodyPart part) {
        if (part == BodyPart.HEAD || part == BodyPart.NECK) {
            GlStateManager.translate(0, -0.1F, 0);
        }
        if (part == BodyPart.NECK) {
             GlStateManager.scale(1, 1.3F, 1);
        }
        super.transform(part);
    }

    @Override
    protected void initHead(float yOffset, float stretch) {
        super.initHead(yOffset, stretch);

        bristles = new PonyRenderer(this, 56, 32);
        bipedHead.addChild(bristles);

        bristles.offset(-1, -1, -3)
                .box(0, -10, 2, 2, 6, 2, stretch)
                .box(0, -10, 4, 2, 8, 2, stretch)
                .box(0, -8, 6, 2, 6, 2, stretch)
                .rotateAngleX = 0.3F;
        bristles.child(0).offset(-1.01F, 2, -7) //0.01 to prevent z-fighting
                .box(0, -10, 4, 2, 8, 2, stretch)
                .box(0, -8, 6, 2, 6, 2, stretch)
                .rotateAngleX = -1F;
    }

    class Armour extends ModelPonyArmor {

        @Override
        public void transform(BodyPart part) {
            if (part == BodyPart.HEAD || part == BodyPart.NECK) {
                GlStateManager.translate(0, -0.1F, 0);
            }
            if (part == BodyPart.NECK) {
                 GlStateManager.scale(1, 1.3F, 1);
            }
            super.transform(part);
        }
    }
}
