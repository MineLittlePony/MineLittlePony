package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.BodyPart;
import com.brohoof.minelittlepony.model.ModelPony;
import com.brohoof.minelittlepony.model.PonyModelConstants;
import com.brohoof.minelittlepony.renderer.AniParams;
import com.brohoof.minelittlepony.renderer.CompressiveRendering;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class PegasusWings implements IPonyPart, PonyModelConstants {

    private ModelPony pony;

    public ModelRenderer[] leftWing;
    public ModelRenderer[] rightWing;

    public ModelRenderer[] leftWingExt;
    public ModelRenderer[] rightWingExt;

    public CompressiveRendering compressiveLeftWing;
    public CompressiveRendering compressiveRightWing;

    @Override
    public void init(ModelPony pony, float yOffset, float stretch) {
        this.pony = pony;

        this.leftWing = new ModelRenderer[3];
        this.rightWing = new ModelRenderer[3];
        this.leftWingExt = new ModelRenderer[6];
        this.rightWingExt = new ModelRenderer[6];
        this.compressiveLeftWing = new CompressiveRendering(pony);
        this.compressiveRightWing = new CompressiveRendering(pony);

        for (int i = 0; i < leftWing.length; i++) {
            this.leftWing[i] = new ModelRenderer(pony, 56, 16);
            this.leftWing[i].mirror = true;
            this.compressiveLeftWing.addCompressed(this.leftWing[i]);
        }
        for (int i = 0; i < rightWing.length; i++) {
            this.rightWing[i] = new ModelRenderer(pony, 56, 16);
            this.compressiveRightWing.addCompressed(this.rightWing[i]);
        }
        for (int i = 0; i < leftWingExt.length; i++) {
            this.leftWingExt[i] = new ModelRenderer(pony, 56, 19);
            this.leftWingExt[i].mirror = true;
            this.compressiveLeftWing.addExpanded(this.leftWingExt[i]);
        }
        for (int i = 0; i < rightWingExt.length; i++) {
            this.rightWingExt[i] = new ModelRenderer(pony, 56, 19);
            this.compressiveRightWing.addExpanded(this.rightWingExt[i]);
        }

        this.compressiveLeftWing.setChance(2);
        this.compressiveRightWing.setChance(2);

        initPositions(yOffset, stretch);
    }

    private void initPositions(float yOffset, float stretch) {
        this.leftWing[0].addBox(4.0F, 5.0F, 2.0F, 2, 6, 2, stretch);
        this.leftWing[0].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.leftWing[0].rotateAngleX = ROTATE_90;
        this.leftWing[1].addBox(4.0F, 5.0F, 4.0F, 2, 8, 2, stretch);
        this.leftWing[1].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.leftWing[1].rotateAngleX = ROTATE_90;
        this.leftWing[2].addBox(4.0F, 5.0F, 6.0F, 2, 6, 2, stretch);
        this.leftWing[2].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.leftWing[2].rotateAngleX = ROTATE_90;
        this.rightWing[0].addBox(-6.0F, 5.0F, 2.0F, 2, 6, 2, stretch);
        this.rightWing[0].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.rightWing[0].rotateAngleX = ROTATE_90;
        this.rightWing[1].addBox(-6.0F, 5.0F, 4.0F, 2, 8, 2, stretch);
        this.rightWing[1].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.rightWing[1].rotateAngleX = ROTATE_90;
        this.rightWing[2].addBox(-6.0F, 5.0F, 6.0F, 2, 6, 2, stretch);
        this.rightWing[2].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.rightWing[2].rotateAngleX = ROTATE_90;
        this.leftWingExt[0].addBox(0.0F, 6.0F, 0.0F, 1, 8, 2, stretch + 0.1F);
        this.leftWingExt[0].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset, LEFT_WING_EXT_RP_Z);
        this.leftWingExt[1].addBox(0.0F, -1.2F, -0.2F, 1, 8, 2, stretch - 0.2F);
        this.leftWingExt[1].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset, LEFT_WING_EXT_RP_Z);
        this.leftWingExt[2].addBox(0.0F, 1.8F, 1.3F, 1, 8, 2, stretch - 0.1F);
        this.leftWingExt[2].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset, LEFT_WING_EXT_RP_Z);
        this.leftWingExt[3].addBox(0.0F, 5.0F, 2.0F, 1, 8, 2, stretch);
        this.leftWingExt[3].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset, LEFT_WING_EXT_RP_Z);
        this.leftWingExt[4].addBox(0.0F, 0.0F, -0.2F, 1, 6, 2, stretch + 0.3F);
        this.leftWingExt[4].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset, LEFT_WING_EXT_RP_Z);
        this.leftWingExt[5].addBox(0.0F, 0.0F, 0.2F, 1, 3, 2, stretch + 0.2F);
        this.leftWingExt[5].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset, LEFT_WING_EXT_RP_Z);
        this.rightWingExt[0].addBox(0.0F, 6.0F, 0.0F, 1, 8, 2, stretch + 0.1F);
        this.rightWingExt[0].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset, RIGHT_WING_EXT_RP_Z);
        this.rightWingExt[1].addBox(0.0F, -1.2F, -0.2F, 1, 8, 2, stretch - 0.2F);
        this.rightWingExt[1].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset, RIGHT_WING_EXT_RP_Z);
        this.rightWingExt[2].addBox(0.0F, 1.8F, 1.3F, 1, 8, 2, stretch - 0.1F);
        this.rightWingExt[2].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset, RIGHT_WING_EXT_RP_Z);
        this.rightWingExt[3].addBox(0.0F, 5.0F, 2.0F, 1, 8, 2, stretch);
        this.rightWingExt[3].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset, RIGHT_WING_EXT_RP_Z);
        this.rightWingExt[4].addBox(0.0F, 0.0F, -0.2F, 1, 6, 2, stretch + 0.3F);
        this.rightWingExt[4].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset, RIGHT_WING_EXT_RP_Z);
        this.rightWingExt[5].addBox(0.0F, 0.0F, 0.2F, 1, 3, 2, stretch + 0.2F);
        this.rightWingExt[5].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset, RIGHT_WING_EXT_RP_Z);
    }

    @Override
    public void animate(PonyData metadata, AniParams ani) {

        float bodySwingRotation = 0.0F;
        if (pony.swingProgress > -9990.0F && (!metadata.getRace().hasHorn() || metadata.getGlowColor() == 0)) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt_float(pony.swingProgress) * 3.1415927F * 2.0F) * 0.2F;
        }
        for (int i = 0; i < this.leftWing.length; ++i) {
            this.leftWing[i].rotateAngleY = bodySwingRotation * 0.2F;
        }
        for (int i = 0; i < this.rightWing.length; ++i) {
            this.rightWing[i].rotateAngleY = bodySwingRotation * 0.2F;
        }
        if (pony.isSneak && !pony.isFlying) {
            this.sneak();
        } else if (metadata.getRace().hasWings()) {
            this.unsneak(ani.tick);

        }

        // Special
        this.leftWingExt[1].rotateAngleX -= 0.85F;
        this.leftWingExt[2].rotateAngleX -= 0.75F;
        this.leftWingExt[3].rotateAngleX -= 0.5F;
        this.leftWingExt[5].rotateAngleX -= 0.85F;
        this.rightWingExt[1].rotateAngleX -= 0.85F;
        this.rightWingExt[2].rotateAngleX -= 0.75F;
        this.rightWingExt[3].rotateAngleX -= 0.5F;
        this.rightWingExt[5].rotateAngleX -= 0.85F;

    }

    @Override
    public void render(PonyData data, float scale) {
        pony.transform(BodyPart.BODY);
        if (data.getRace() != null && data.getRace().hasWings()) {
            if (!pony.isFlying && !pony.isSneak) {
                this.setExtendingWings(true);

                for (int k1 = 0; k1 < this.leftWing.length; ++k1) {
                    this.leftWing[k1].render(scale);
                }

                for (int k1 = 0; k1 < this.rightWing.length; ++k1) {
                    this.rightWing[k1].render(scale);
                }
            } else {
                this.setExtendingWings(false);

                for (int k1 = 0; k1 < this.leftWingExt.length; ++k1) {
                    this.leftWingExt[k1].render(scale);
                }

                for (int i = 0; i < this.rightWingExt.length; ++i) {
                    this.rightWingExt[i].render(scale);
                }
            }
        }
    }

    private void setExtendingWings(boolean compress) {
        this.compressiveLeftWing.setIsCompressed(compress);
        this.compressiveRightWing.setIsCompressed(compress);
    }

    public void setWingCompression(boolean pegasus) {
        if (pegasus) {
            this.compressiveLeftWing.init_Safe();
            this.compressiveRightWing.init_Safe();
        } else {
            this.compressiveLeftWing.deInit_Safe();
            this.compressiveRightWing.deInit_Safe();
        }
    }

    private void sneak() {
        for (int i = 0; i < this.leftWingExt.length; ++i) {
            this.leftWingExt[i].rotationPointY = LEFT_WING_RP_Y_SNEAK;
            this.leftWingExt[i].rotationPointZ = LEFT_WING_RP_Z_SNEAK;
            this.leftWingExt[i].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
            this.leftWingExt[i].rotateAngleZ = LEFT_WING_ROTATE_ANGLE_Z_SNEAK;
        }

        for (int i = 0; i < this.leftWingExt.length; ++i) {
            this.rightWingExt[i].rotationPointY = RIGHT_WING_RP_Y_SNEAK;
            this.rightWingExt[i].rotationPointZ = RIGHT_WING_RP_Z_SNEAK;
            this.rightWingExt[i].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
            this.rightWingExt[i].rotateAngleZ = RIGHT_WING_ROTATE_ANGLE_Z_SNEAK;
        }
    }

    private void unsneak(float tick) {
        if (!pony.isFlying) {
            for (int i = 0; i < this.leftWing.length; ++i) {
                this.leftWing[i].rotationPointY = WING_FOLDED_RP_Y;
                this.leftWing[i].rotationPointZ = WING_FOLDED_RP_Z;
            }

            for (int i = 0; i < this.rightWing.length; ++i) {
                this.rightWing[i].rotationPointY = WING_FOLDED_RP_Y;
                this.rightWing[i].rotationPointZ = WING_FOLDED_RP_Z;
            }
        } else {
            float WingRotateAngleZ = MathHelper.sin(tick * 0.536F) * 1.0F;

            for (int i = 0; i < this.leftWingExt.length; ++i) {
                this.leftWingExt[i].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
                this.leftWingExt[i].rotateAngleZ = -WingRotateAngleZ - ROTATE_270 - 0.4F;
                this.leftWingExt[i].rotationPointY = LEFT_WING_RP_Y_NOTSNEAK;
                this.leftWingExt[i].rotationPointZ = LEFT_WING_RP_Z_NOTSNEAK;
            }

            for (int i = 0; i < this.rightWingExt.length; ++i) {
                this.rightWingExt[i].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
                this.rightWingExt[i].rotateAngleZ = WingRotateAngleZ + ROTATE_270 + 0.4F;
                this.rightWingExt[i].rotationPointY = RIGHT_WING_RP_Y_NOTSNEAK;
                this.rightWingExt[i].rotationPointZ = RIGHT_WING_RP_Z_NOTSNEAK;
            }
        }
    }

}
