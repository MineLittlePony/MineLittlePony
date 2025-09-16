package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.AbstractPonyModel;

public class PonyArmourModel<T extends LivingEntity> extends AbstractPonyModel<T> {

    public PonyArmourModel(ModelPart tree) {
        super(tree);
    }

    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, PonyModel<T> mainModel) {
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
    }

    public boolean shouldRender(EquipmentSlot slot, ArmourLayer layer) {
        return slot == EquipmentSlot.CHEST
                || (layer == ArmourLayer.OUTER && slot == EquipmentSlot.HEAD)
                || (slot == (layer == ArmourLayer.OUTER ? EquipmentSlot.FEET : EquipmentSlot.LEGS));
    }

    public void setVisibilities(EquipmentSlot slot, ArmourLayer layer) {
        setVisible(false);
        body.visible = slot == EquipmentSlot.CHEST;
        head.visible = layer == ArmourLayer.OUTER && slot == EquipmentSlot.HEAD;

        if (slot == (layer == ArmourLayer.OUTER ? EquipmentSlot.FEET : EquipmentSlot.LEGS)) {
            rightArm.visible = true;
            leftArm.visible = true;
            rightLeg.visible = true;
            leftLeg.visible = true;
        }
    }
}
