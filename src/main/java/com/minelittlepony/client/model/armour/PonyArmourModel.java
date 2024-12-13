package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EquipmentSlot;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PonyArmourModel<S extends PonyRenderState> extends AbstractPonyModel<S> {

    public PonyArmourModel(ModelPart tree) {
        super(tree, false);
    }

    public boolean setAngles(PlayerEntityRenderState state, EquipmentSlot slot, ArmourLayer layer, PonyModel<?> mainModel) {
        setAngles(state);
        if (!setVisibilities(slot, layer)) {
            return false;
        }
        if (mainModel instanceof ClientPonyModel abs) {
            abs.copyTransforms(this);
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
