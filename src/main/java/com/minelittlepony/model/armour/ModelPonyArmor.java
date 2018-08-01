package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.capabilities.IModelArmor;
import com.minelittlepony.render.PonyRenderer;

public class ModelPonyArmor extends AbstractPonyModel implements IModelArmor {

    public PonyRenderer flankGuard;

    public PonyRenderer saddle;

    public ModelPonyArmor() {
        super(false);
        textureHeight = 32;
    }

    @Override
    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        super.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);

        flankGuard.rotateAngleX = rotateAngleX;
        flankGuard.rotationPointY = rotationPointY;
        flankGuard.rotationPointZ = rotationPointZ;

        saddle.rotateAngleX = rotateAngleX;
        saddle.rotationPointY = rotationPointY;
        saddle.rotationPointZ = rotationPointZ;
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        flankGuard.render(scale);
        saddle.render(scale);
    }

    @Override
    protected void renderLegs(float scale) {
        if (!isSneak) {
            boolean isLegs = saddle.showModel;
            saddle.showModel = true;
            saddle.postRender(scale);
            saddle.showModel = isLegs;
        }

        super.renderLegs(scale);
    }

    @Override
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
                .tex(0, 4).box(2, -6, 1, 2, 2, 2, stretch / 2)
                .box(-4, -6, 1, 2, 2, 2, stretch / 2);
    }

    @Override
    protected void initBody(float yOffset, float stretch) {
        super.initBody(yOffset, stretch);

        flankGuard = new PonyRenderer(this, 0, 0)
                .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .box(-4, 4, 6, 8, 8, 8, stretch);
        saddle = new PonyRenderer(this, 16, 8)
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
    public void setVisible(boolean invisible) {
        super.setVisible(invisible);
        flankGuard.showModel = invisible;
        saddle.showModel = invisible;
        bipedHead.showModel = invisible;
        tail.setVisible(false);
        neck.isHidden = true;
        upperTorso.isHidden = true;
        snout.isHidden = true;
    }

    @Override
    public void showFeet(boolean show) {
        bipedRightArm.showModel = show;
        bipedLeftArm.showModel = show;
        bipedRightLeg.showModel = show;
        bipedLeftLeg.showModel = show;
    }

    @Override
    public void showLegs(boolean isPony) {
        bipedBody.showModel = true;
    }

    @Override
    public void showSaddle(boolean isPony) {
        flankGuard.showModel = !isPony;
        saddle.showModel = isPony;
    }

    @Override
    public void showHead(boolean show) {
        bipedHead.showModel = show;
    }
}
