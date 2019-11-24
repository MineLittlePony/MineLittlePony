package com.minelittlepony.client.model.part;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.model.IPart;

import java.util.UUID;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SeaponyTail implements IPart {

    private static final float TAIL_ROTX = PI / 2;

    private ModelPart tailBase;

    private ModelPart tailTip;
    private ModelPart tailFins;

    private IPonyModel<?> model;

    public SeaponyTail(AbstractPonyModel<?> model) {
        this.model = model;
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float rotation = model.getAttributes().isSleeping ? 0 : MathHelper.sin(ticks * 0.536f) / 4;

        tailBase.pitch = TAIL_ROTX + rotation;
        tailTip.pitch = rotation;
        tailFins.pitch = rotation - TAIL_ROTX;
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        tailBase.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

}
