package com.minelittlepony.client.model.part;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.mson.api.MsonModel;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SeaponyTail implements IPart, MsonModel {
    private final ModelPart tailBase;

    private final ModelPart tailTip;
    private final ModelPart tailFins;

    public SeaponyTail(ModelPart tree) {
        tailBase = tree.getChild("base");
        tailTip = tree.getChild("tip");
        tailFins = tree.getChild("fins");
    }

    @Override
    public void setPartAngles(ModelAttributes attributes, float limbAngle, float limbSpeed, float bodySwing, float animationProgress) {
        float rotation = attributes.isSleeping ? 0 : MathHelper.sin(animationProgress * 0.536f) / 4;

        tailBase.pitch = MathHelper.HALF_PI + rotation;
        tailTip.pitch = rotation;
        tailFins.pitch = rotation - MathHelper.HALF_PI;
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
        tailBase.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

}
