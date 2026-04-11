package com.minelittlepony.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Modified from ModelElytra.
 */
public class PonyElytra extends ElytraModel {
    public PonyElytra(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        boolean crouching = state.isCrouching;
        state.isCrouching = false;
        super.setupAnim(state);
        state.isCrouching = crouching;
    }
}
