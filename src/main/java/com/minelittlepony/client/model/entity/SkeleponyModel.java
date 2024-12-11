package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.SkeleponyRenderer;

public class SkeleponyModel<T extends SkeleponyRenderer.State> extends AlicornModel<T> {
    public SkeleponyModel(ModelPart tree) {
        super(tree, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BipedEntityModel.ArmPose getArmPose(PlayerEntityRenderState state, Arm arm) {
        boolean isMain = arm == state.mainArm;

        if (isMain) {
            ItemStack mainHand = state.getMainHandStack();
            if (!mainHand.isEmpty()) {
                return mainHand.getItem() == Items.BOW && ((T)state).isAttacking ? ArmPose.BOW_AND_ARROW : ArmPose.ITEM;
            }
        }

        return super.getArmPose(state, arm);
    }
}
