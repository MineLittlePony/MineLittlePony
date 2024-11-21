package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PonyArmourModel<S extends PonyRenderState> extends AbstractPonyModel<S> {

    public PonyArmourModel(ModelPart tree) {
        super(tree, false);
    }

    public boolean poseModel(S state,
            EquipmentSlot slot, ArmourLayer layer,
            PonyModel<S> mainModel) {

        if (!setVisibilities(slot, layer)) {
            return false;
        }
        setAngles(state);
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
