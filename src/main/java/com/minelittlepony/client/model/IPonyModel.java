package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.mson.api.MsonModel;

public interface IPonyModel<T extends LivingEntity> extends PonyModelConstants, IModel, ICapitated<ModelPart>, MsonModel {

    void copyAttributes(BipedEntityModel<T> other);

    void updateLivingState(T entity, IPony pony, EquineRenderManager.Mode mode);

    ModelPart getBodyPart(BodyPart part);
}
