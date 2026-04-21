package com.minelittlepony.api.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;

public interface ModelWithHorn<T extends HumanoidRenderState & PonyModel.AttributedHolder> extends PonyModel<T> {

    SubModel<T> getHorn();

    ModelPart getLevitation(HumanoidArm side);

    ModelPart getPhysicalArm(HumanoidArm side);
}
