package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.AbstractPonyModel;

public class PonyArmourModel<T extends LivingEntity> extends AbstractPonyModel<T> {

    public PonyArmourModel(ModelPart tree) {
        super(tree);
    }

    public boolean shouldRender(EquipmentSlot slot, ArmourLayer layer) {
        return slot == EquipmentSlot.CHEST
                || (layer == ArmourLayer.OUTER && slot == EquipmentSlot.HEAD)
                || (slot == (layer == ArmourLayer.OUTER ? EquipmentSlot.FEET : EquipmentSlot.LEGS));
    }

    public void setVisibilities(EquipmentSlot slot, ArmourLayer layer) {
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
