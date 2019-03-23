package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.armour.IArmour;

public class ModelPonyArmor extends AbstractPonyModel implements IArmour {

    public PonyRenderer chestPiece;

    public ModelPonyArmor() {
        super(false);
        textureHeight = 32;
    }

    @Override
    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        super.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);

        chestPiece.rotateAngleX = rotateAngleX;
        chestPiece.rotationPointY = rotationPointY;
        chestPiece.rotationPointZ = rotationPointZ;
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        chestPiece.render(scale);
    }

    @Override
    public void synchroniseLegs(IModel model) {

        if (model instanceof ModelBiped) {
            ModelBiped mainModel = (ModelBiped)model;
            copyModelAngles(mainModel.bipedBody, bipedBody);
            copyModelAngles(mainModel.bipedRightArm, bipedRightArm);
            copyModelAngles(mainModel.bipedLeftArm, bipedLeftArm);
            copyModelAngles(mainModel.bipedRightLeg, bipedRightLeg);
            copyModelAngles(mainModel.bipedLeftLeg, bipedLeftLeg);
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
        bipedLeftArm = new PonyRenderer(this, 0, 16).flip();
        bipedRightArm = new PonyRenderer(this, 0, 16);

        bipedLeftLeg = new PonyRenderer(this, 48, 8).flip();
        bipedRightLeg = new PonyRenderer(this, 48, 8);
    }

    @Override
    public void setInVisible() {
        setVisible(false);
        bipedBody.showModel = true;
        chestPiece.showModel = false;
        bipedHead.showModel = false;
        neck.showModel = false;
        tail.setVisible(false);
        upperTorso.isHidden = true;
        snout.isHidden = true;
    }

    @Override
    public void showBoots() {
        bipedRightArm.showModel = true;
        bipedLeftArm.showModel = true;
        bipedRightLeg.showModel = true;
        bipedLeftLeg.showModel = true;
    }

    @Override
    public void showLeggings() {
        showBoots();
    }

    @Override
    public void showChestplate() {
        chestPiece.showModel = true;
        neck.showModel = true;
    }

    @Override
    public void showSaddle() {
        chestPiece.showModel = true;
        neck.showModel = true;
    }

    @Override
    public void showHelmet() {
        bipedHead.showModel = true;
    }
}
