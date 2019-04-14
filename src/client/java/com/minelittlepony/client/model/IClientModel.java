package com.minelittlepony.client.model;

import net.minecraft.client.renderer.entity.model.ModelRenderer;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.model.IModel;

public interface IClientModel extends IModel, ICapitated<ModelRenderer> {

    ModelRenderer getBodyPart(BodyPart part);
}
