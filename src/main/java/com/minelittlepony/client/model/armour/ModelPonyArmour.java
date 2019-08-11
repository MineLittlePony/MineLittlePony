package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.armour.ArmourVariant;
import com.minelittlepony.model.armour.IArmour;

public class ModelPonyArmour<T extends LivingEntity> extends AbstractPonyModel<T> implements IArmour {

    public PonyRenderer chestPiece;

    public Cuboid steveRightLeg;
    public Cuboid steveLeftLeg;

    private ArmourVariant variant = ArmourVariant.NORMAL;

    public ModelPonyArmour() {
        super(false);
        textureHeight = 32;
    }

    @Override
    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        super.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);

        chestPiece.pitch = rotateAngleX;
        chestPiece.rotationPointY = rotationPointY;
        chestPiece.rotationPointZ = rotationPointZ;
    }

    @Override
    protected void renderBody(float scale) {
        if (variant == ArmourVariant.LEGACY) {
            body.render(scale);
            upperTorso.render(scale);
        } else {
            chestPiece.render(scale);
        }
    }

    @Override
    protected void renderLegs(float scale) {
        super.renderLegs(scale);
        steveLeftLeg.render(scale);
        steveRightLeg.render(scale);
    }

    @Override
    public void setVariant(ArmourVariant variant) {
        this.variant = variant;
    }

    @Override
    public void synchroniseLegs(IModel model) {

        if (model instanceof BipedEntityModel) {
            @SuppressWarnings("unchecked")
            BipedEntityModel<T> mainModel = (BipedEntityModel<T>)model;
            body.copyRotation(mainModel.body);
            rightArm.copyRotation(mainModel.rightArm);
            leftArm.copyRotation(mainModel.leftArm);
            rightLeg.copyRotation(mainModel.rightLeg);
            leftLeg.copyRotation(mainModel.leftLeg);

            steveLeftLeg.copyRotation(mainModel.leftLeg);
            steveRightLeg.copyRotation(mainModel.rightLeg);
        }
    }

    @Override
    protected void initEars(PonyRenderer head, float yOffset, float stretch) {
        stretch /= 2;
        head.tex(0, 0).box(-4, -6, 1, 2, 2, 2, stretch)  // right ear
            .tex(0, 4).box( 2, -6, 1, 2, 2, 2, stretch); // left ear
    }

    @Override
    protected void initBody(float yOffset, float stretch) {
        super.initBody(yOffset, stretch);

        chestPiece = new PonyRenderer(this, 16, 8)
                .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                 .box(-4, 4, -2, 8, 8, 16, stretch);

        // fits the legacy player's torso to our pony bod.
        upperTorso = new PlaneRenderer(this, 24, 0);
        upperTorso.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
                  .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                  .tex(32, 23).east( 4, -4, -4, 8, 8, stretch)
                              .west(-4, -4, -4, 8, 8, stretch)
                  .tex(32, 23).south(-4, -4,  4, 8, 8, stretch)
                  .tex(32, 23).top(-4, -4, -8, 8, 12, stretch);
        // it's a little short, so the butt tends to show. :/
    }

    @Override
    protected void preInitLegs() {
        leftArm = new PonyRenderer(this, 0, 16).flip();
        rightArm = new PonyRenderer(this, 0, 16);

        leftLeg = new PonyRenderer(this, 48, 8).flip();
        rightLeg = new PonyRenderer(this, 48, 8);

        steveLeftLeg = new PonyRenderer(this, 0, 16).flip();
        steveRightLeg = new PonyRenderer(this, 0, 16);
    }

    @Override
    protected void initLegs(float yOffset, float stretch) {
        super.initLegs(yOffset, stretch);

        int armLength = attributes.armLength;
        int armWidth = attributes.armWidth;
        int armDepth = attributes.armDepth;

        float rarmX = attributes.armRotationX;

        float armX = THIRDP_ARM_CENTRE_X;
        float armY = THIRDP_ARM_CENTRE_Y;
        float armZ = BODY_CENTRE_Z / 2 - 1 - armDepth;

        steveLeftLeg .setRotationPoint( rarmX, yOffset, 0);
        steveRightLeg.setRotationPoint(-rarmX, yOffset, 0);

        steveLeftLeg .addBox(armX,            armY, armZ, armWidth, armLength, armDepth, stretch);
        steveRightLeg.addBox(armX - armWidth, armY, armZ, armWidth, armLength, armDepth, stretch);
    }

    @Override
    public void setInVisible() {
        setVisible(false);
        body.visible = true;
        chestPiece.visible = false;
        head.visible = false;
        neck.visible = false;
        tail.setVisible(false);
        upperTorso.field_3664 = true;
        snout.isHidden = true;
        steveLeftLeg.visible = false;
        steveRightLeg.visible = false;
    }

    @Override
    public void showBoots() {
        rightArm.visible = true;
        leftArm.visible = true;
        rightLeg.visible = variant == ArmourVariant.NORMAL;
        leftLeg.visible = variant == ArmourVariant.NORMAL;
        steveLeftLeg.visible = variant == ArmourVariant.LEGACY;
        steveRightLeg.visible = variant == ArmourVariant.LEGACY;
    }

    @Override
    public void showLeggings() {
        showBoots();
    }

    @Override
    public void showChestplate() {
        chestPiece.visible = true;
        neck.visible = true;
    }

    @Override
    public void showSaddle() {
        chestPiece.visible = true;
        neck.visible = true;

        if (variant == ArmourVariant.LEGACY) {
            upperTorso.field_3664 = false;
            upperTorso.visible = true;
        }
    }

    @Override
    public void showHelmet() {
        head.visible = true;
    }
}
