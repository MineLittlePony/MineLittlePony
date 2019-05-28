package com.minelittlepony.client.model.armour;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.armour.IArmour;

public class ModelPonyArmour<T extends LivingEntity> extends AbstractPonyModel<T> implements IArmour {

    public PonyRenderer chestPiece;

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
    protected void renderBody(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        chestPiece.render(scale);
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
    }

    @Override
    protected void preInitLegs() {
        leftArm = new PonyRenderer(this, 0, 16).flip();
        rightArm = new PonyRenderer(this, 0, 16);

        leftLeg = new PonyRenderer(this, 48, 8).flip();
        rightLeg = new PonyRenderer(this, 48, 8);
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
    }

    @Override
    public void showBoots() {
        rightArm.visible = true;
        leftArm.visible = true;
        rightLeg.visible = true;
        leftLeg.visible = true;
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
    }

    @Override
    public void showHelmet() {
        head.visible = true;
    }
}
