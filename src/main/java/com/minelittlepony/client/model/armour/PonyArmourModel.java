package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.armour.*;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.IPonyModel;

public class PonyArmourModel<T extends LivingEntity> extends AbstractPonyModel<T> implements IArmourModel<T> {

    public PonyArmourModel(ModelPart tree) {
        super(tree);
    }

    @Override
    public boolean poseModel(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch,
            EquipmentSlot slot, ArmourLayer layer,
            IPonyModel<T> mainModel) {

        if (!setVisibilities(slot, layer)) {
            return false;
        }
        mainModel.copyAttributes(this);
        setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch);
        if (mainModel instanceof BipedEntityModel<?> biped) {
            head.copyTransform(biped.head);
            body.copyTransform(biped.body);
            rightArm.copyTransform(biped.rightArm);
            leftArm.copyTransform(biped.leftArm);
            rightLeg.copyTransform(biped.rightLeg);
            leftLeg.copyTransform(biped.leftLeg);
        }
        return true;
    }

    public boolean setVisibilities(EquipmentSlot slot, ArmourLayer layer) {
        setVisible(false);
        body.visible = slot == EquipmentSlot.CHEST;
        head.visible = layer == ArmourLayer.OUTER && slot == EquipmentSlot.HEAD;

        if (slot == (layer == ArmourLayer.OUTER ? EquipmentSlot.FEET : EquipmentSlot.LEGS)) {
            rightArm.visible = true;
            leftArm.visible = true;
            rightLeg.visible = true;
            leftLeg.visible = true;
            return true;
        }

        return head.visible || body.visible;
    }
}
