package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.armour.*;
import com.minelittlepony.client.model.AbstractPonyModel;

public class PonyArmourModel<T extends LivingEntity> extends AbstractPonyModel<T> implements IArmourModel {

    private ModelPart chestPiece;

    private ModelPart steveRightLeg;
    private ModelPart steveLeftLeg;

    public PonyArmourModel(ModelPart tree) {
        super(tree);
        chestPiece = tree.getChild("chestpiece");
        steveRightLeg = tree.getChild("steve_right_leg");
        steveLeftLeg = tree.getChild("steve_left_leg");

        bodyRenderList.clear();
        bodyRenderList.add(body, chestPiece);
        legsRenderList.add(steveLeftLeg, steveRightLeg);
    }

    public IArmourTextureResolver getArmourTextureResolver() {
        return DefaultArmourTextureResolver.INSTANCE;
    }

    @Override
    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        super.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);

        chestPiece.pitch = rotateAngleX;
        chestPiece.pivotY = rotationPointY;
        chestPiece.pivotZ = rotationPointZ;
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
    public boolean setVisibilities(EquipmentSlot slot, ArmourLayer layer, ArmourVariant variant) {
        setVisible(false);
        chestPiece.visible = false;
        head.visible = false;
        neck.visible = false;
        steveLeftLeg.visible = false;
        steveRightLeg.visible = false;

        switch (layer) {
            case OUTER:
                switch (slot) {
                    case HEAD:
                        head.visible = true;
                        return true;
                    case FEET:
                        showLeggings(layer, variant);
                        return true;
                    case CHEST:
                        body.visible = variant == ArmourVariant.LEGACY;
                        chestPiece.visible = variant == ArmourVariant.NORMAL;
                        return true;
                    default:
                        return false;
                }
            case INNER:
                switch (slot) {
                    case LEGS:
                        showLeggings(layer, variant);
                        return true;
                    case CHEST:
                        body.visible = variant == ArmourVariant.LEGACY;
                        chestPiece.visible = variant == ArmourVariant.NORMAL;
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    protected void showLeggings(ArmourLayer layer, ArmourVariant variant) {
        rightArm.visible = true;
        leftArm.visible = true;
        rightLeg.visible = variant == ArmourVariant.NORMAL;
        leftLeg.visible = variant == ArmourVariant.NORMAL;
        steveLeftLeg.visible = variant == ArmourVariant.LEGACY;
        steveRightLeg.visible = variant == ArmourVariant.LEGACY;
    }
}
