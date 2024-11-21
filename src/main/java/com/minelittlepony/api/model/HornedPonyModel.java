package com.minelittlepony.api.model;

import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.util.Arm;

public interface HornedPonyModel<T extends EntityRenderState & PonyModel.AttributedHolder> extends PonyModel<T> {
    /**
     * Returns true if this model is currently using magic (horn is lit).
     */
    default boolean isCasting(T state) {
        return state instanceof PlayerEntityRenderState s
                && (getArmPoseForSide(s, Arm.LEFT) != ArmPose.EMPTY || getArmPoseForSide(s, Arm.RIGHT) != ArmPose.EMPTY);
    }
}
