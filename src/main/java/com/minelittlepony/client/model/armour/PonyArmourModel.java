package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.armour.*;
import com.minelittlepony.client.model.AbstractPonyModel;

public class PonyArmourModel<T extends LivingEntity> extends AbstractPonyModel<T> implements IArmourModel {

    private ModelPart chestPiece;

    private ModelPart steveRightLeg;
    private ModelPart steveLeftLeg;

    private ArmourVariant variant = ArmourVariant.NORMAL;

    public PonyArmourModel(ModelPart tree) {
        super(tree);
        chestPiece = tree.getChild("chestpiece");
        steveRightLeg = tree.getChild("steve_right_leg");
        steveLeftLeg = tree.getChild("steve_left_leg");

        bodyRenderList.clear();
        bodyRenderList.add(body, upperTorso, chestPiece);
        legsRenderList.add(steveLeftLeg, steveRightLeg);
    }

    public IArmourTextureResolver getArmourTextureResolver() {
        return IArmourTextureResolver.DEFAULT;
    }

    @Override
    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        super.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);

        chestPiece.pitch = rotateAngleX;
        chestPiece.pivotY = rotationPointY;
        chestPiece.pivotZ = rotationPointZ;
    }

    @Override
    public void setVariant(ArmourVariant variant) {
        this.variant = variant;
    }

    @Override
    public void synchroniseAngles(IModel model) {
        if (model instanceof BipedEntityModel) {
            @SuppressWarnings("unchecked")
            BipedEntityModel<T> mainModel = (BipedEntityModel<T>)model;
            head.copyTransform(mainModel.head);
            head.copyTransform(mainModel.head);

            body.copyTransform(mainModel.body);
            rightArm.copyTransform(mainModel.rightArm);
            leftArm.copyTransform(mainModel.leftArm);
            rightLeg.copyTransform(mainModel.rightLeg);
            leftLeg.copyTransform(mainModel.leftLeg);

            steveLeftLeg.copyTransform(mainModel.leftLeg);
            steveRightLeg.copyTransform(mainModel.rightLeg);
        }
    }

    @Override
    public void setInVisible() {
        setVisible(false);
        chestPiece.visible = false;
        head.visible = false;
        neck.visible = false;
        upperTorso.visible = false;
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
        body.visible = variant == ArmourVariant.LEGACY;
        upperTorso.visible = variant == ArmourVariant.LEGACY;
        chestPiece.visible = variant == ArmourVariant.NORMAL;
    }

    @Override
    public void showSaddle() {
        body.visible = variant == ArmourVariant.LEGACY;
        upperTorso.visible = variant == ArmourVariant.LEGACY;
        chestPiece.visible = variant == ArmourVariant.NORMAL;
    }

    @Override
    public void showHelmet() {
        head.visible = true;
    }
}
