package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.capabilities.IModelArmor;
import com.minelittlepony.render.PonyRenderer;

public class ModelPonyArmor extends AbstractPonyModel implements IModelArmor {

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
    protected void renderLegs(float scale) {
        super.renderLegs(scale);
    }

    public <T extends ModelBiped & IModel> void synchroniseLegs(T mainModel) {
        copyModelAngles(mainModel.bipedRightArm, bipedRightArm);
        copyModelAngles(mainModel.bipedLeftArm, bipedLeftArm);
        copyModelAngles(mainModel.bipedRightLeg, bipedRightLeg);
        copyModelAngles(mainModel.bipedLeftLeg, bipedLeftLeg);
    }

    @Override
    protected void initHead(float yOffset, float stretch) {
        super.initHead(yOffset, stretch * 1.1F);
        ((PonyRenderer)bipedHead).child()
                .tex(0, 4).box(2, -6, 1, 2, 2, 2, stretch * 0.5F)
                          .box(-4, -6, 1, 2, 2, 2, stretch * 0.5F);
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

        bipedLeftLeg = new PonyRenderer(this, 0, 16).flip();
        bipedRightLeg = new PonyRenderer(this, 0, 16);
    }

    @Override
    public void setInVisible() {
        setVisible(false);
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
