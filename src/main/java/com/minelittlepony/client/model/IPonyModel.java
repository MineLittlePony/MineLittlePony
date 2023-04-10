package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.ICapitated;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.mson.api.MsonModel;

public interface IPonyModel<T extends LivingEntity> extends IModel, ICapitated<ModelPart>, ModelWithArms, MsonModel {

    void copyAttributes(BipedEntityModel<T> other);

    void updateLivingState(T entity, IPony pony, ModelAttributes.Mode mode);

    ModelPart getBodyPart(BodyPart part);

    void postItemRender(IPony pony, T entity, ItemStack drop, ModelTransformationMode transform, Arm hand, MatrixStack stack, VertexConsumerProvider renderContext);
}
