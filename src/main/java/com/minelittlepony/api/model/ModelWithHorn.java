package com.minelittlepony.api.model;

import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public interface ModelWithHorn<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends PonyModel<T> {
    default boolean isCasting(T state) {
        return state.leftArmPose != ArmPose.EMPTY || state.rightArmPose != ArmPose.EMPTY;
    }
}
