package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Arm;

public interface ModelWithHooves<T extends EntityRenderState> extends ModelWithArms<PlayerEntityRenderState> {
    ModelPart getForeLeg(Arm side);

    ModelPart getHindLeg(Arm side);
}
