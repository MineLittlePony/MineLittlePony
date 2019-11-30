package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.model.ICompartmented;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.mson.api.MsonModel;
import com.minelittlepony.pony.IPony;

public interface IPonyModel<T extends LivingEntity> extends PonyModelConstants, IModel, ICapitated<ModelPart>, ICompartmented<ModelPart>, MsonModel {

    void updateLivingState(T entity, IPony pony, EquineRenderManager.Mode mode);

    @Override
    default boolean hasHeadGear() {
        return getAttributes().hasHeadGear;
    }
}
