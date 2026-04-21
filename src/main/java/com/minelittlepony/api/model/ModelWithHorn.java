package com.minelittlepony.api.model;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public interface ModelWithHorn<T extends HumanoidRenderState & PonyModel.AttributedHolder> extends PonyModel<T> {

    SubModel<T> getHorn();

}
