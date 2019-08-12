package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.capabilities.IModelArmor;
import com.minelittlepony.render.model.PlaneRenderer;
import com.minelittlepony.render.model.PonyRenderer;

public class ModelPonyArmor extends AbstractPonyModel implements IModelArmor {

    public PonyRenderer chestPiece;

    public ModelRenderer steveRightLeg;
    public ModelRenderer steveLeftLeg;

    private ArmourVariant variant = ArmourVariant.NORMAL;

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
        if (variant == ArmourVariant.LEGACY) {
            bipedBody.render(scale);
            upperTorso.render(scale);
        } else {
            chestPiece.render(scale);
        }
    }

    @Override
    public void setVariant(ArmourVariant variant) {
        this.variant = variant;
    }

    @Override
    public <T extends ModelBiped & IModel> void synchroniseLegs(T mainModel) {
        copyModelAngles(mainModel.bipedBody, bipedBody);
        copyModelAngles(mainModel.bipedRightArm, bipedRightArm);
        copyModelAngles(mainModel.bipedLeftArm, bipedLeftArm);
        copyModelAngles(mainModel.bipedRightLeg, bipedRightLeg);
        copyModelAngles(mainModel.bipedLeftLeg, bipedLeftLeg);
        copyModelAngles(mainModel.bipedLeftLeg, steveLeftLeg);
        copyModelAngles(mainModel.bipedRightLeg, steveRightLeg);
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
        bipedLeftArm = new PonyRenderer(this, 0, 16).flip();
        bipedRightArm = new PonyRenderer(this, 0, 16);

        bipedLeftLeg = new PonyRenderer(this, 48, 8).flip();
        bipedRightLeg = new PonyRenderer(this, 48, 8);

        steveLeftLeg = new PonyRenderer(this, 0, 16).flip();
        steveRightLeg = new PonyRenderer(this, 0, 16);
    }

    @Override
    protected void initLegs(float yOffset, float stretch) {
        super.initLegs(yOffset, stretch);

        int armLength = getArmLength();
        int armWidth = getArmWidth();
        int armDepth = getArmDepth();

        float rarmX = getLegRotationX();

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
        bipedBody.showModel = true;
        chestPiece.showModel = false;
        bipedHead.showModel = false;
        neck.showModel = false;
        tail.setVisible(false);
        upperTorso.isHidden = true;
        snout.isHidden = true;
        steveLeftLeg.showModel = false;
        steveRightLeg.showModel = false;
    }

    @Override
    public void showBoots() {
        bipedRightArm.showModel = true;
        bipedLeftArm.showModel = true;
        bipedRightLeg.showModel = variant == ArmourVariant.NORMAL;
        bipedLeftLeg.showModel = variant == ArmourVariant.NORMAL;
        steveLeftLeg.showModel = variant == ArmourVariant.LEGACY;
        steveRightLeg.showModel = variant == ArmourVariant.LEGACY;
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

        if (variant == ArmourVariant.LEGACY) {
            upperTorso.isHidden = false;
            upperTorso.showModel = true;
        }
    }

    @Override
    public void showHelmet() {
        bipedHead.showModel = true;
    }
}
