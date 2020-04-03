package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.model.IPart;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;
import com.minelittlepony.mson.api.model.MsonPart;
import com.minelittlepony.mson.api.model.BoxBuilder.ContentAccessor;

import java.util.UUID;

public class PonySnout implements IPart, MsonModel {

    private boolean visible = false;

    private ModelPart mare;
    private ModelPart stallion;

    @Override
    public void init(ModelContext context) {
        mare = context.findByName("mare");
        stallion = context.findByName("stallion");

        ContentAccessor head = context.getContext();
        head.children().add(mare);
        head.children().add(stallion);
    }

    public void rotate(float x, float y, float z) {
        ((MsonPart)mare).rotate(x, y, z);
        ((MsonPart)stallion).rotate(x, y, z);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setGender(Gender gender) {
        boolean show = visible && MineLittlePony.getInstance().getConfig().snuzzles.get();

        mare.visible = (show && gender.isMare());
        stallion.visible = (show && gender.isStallion());
    }
}
