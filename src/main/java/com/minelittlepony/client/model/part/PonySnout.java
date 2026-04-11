package com.minelittlepony.client.model.part;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.mson.api.model.PartBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class PonySnout implements SubModel<PonyRenderState>, MsonModel {

    private final ModelPart mare;
    private final ModelPart stallion;

    public PonySnout(ModelPart tree) {
        mare = tree.getChild("mare");
        stallion = tree.getChild("stallion");
    }

    @Override
    public void init(ModelView context) {
        PartBuilder head = context.getThis();
        head.addChild("mare", mare);
        head.addChild("stallion", stallion);
    }

    public void rotate(float x, float y, float z) {
        mare.setRotation(x, y, z);
        stallion.setRotation(x, y, z);
    }

    @Override
    public void accept(PoseStack stack, VertexConsumer vertices, int overlay, int light, int color) {
    }

    @Override
    public void setVisible(boolean visible, PonyRenderState state) {
        visible = !state.attributes.isHorsey
                && !state.attributes.metadata.race().isHuman()
                && PonyConfig.getInstance().snuzzles.get();
        Gender gender = state.attributes.metadata.gender();

        mare.visible = (visible && gender.isMare());
        stallion.visible = (visible && gender.isStallion());
    }
}
