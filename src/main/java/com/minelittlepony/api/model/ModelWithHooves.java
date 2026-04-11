package com.minelittlepony.api.model;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.HumanoidArm;

public interface ModelWithHooves<T extends EntityRenderState> extends ArmedModel<AvatarRenderState> {
    ModelPart getForeLeg(HumanoidArm side);

    ModelPart getHindLeg(HumanoidArm side);
}
