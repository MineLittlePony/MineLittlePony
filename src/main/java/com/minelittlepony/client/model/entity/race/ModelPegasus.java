package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.client.model.part.PegasusWings;
import com.minelittlepony.model.IPart;
import com.minelittlepony.model.IPegasus;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class ModelPegasus<T extends LivingEntity> extends ModelEarthPony<T> implements IPegasus {

    protected IPart wings;

    public ModelPegasus(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public IPart getWings() {
        return wings;
    }

    protected void initWings(float yOffset, float stretch) {
        wings = new PegasusWings<>(this, yOffset, stretch);
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);
        getWings().setRotationAndAngles(attributes.isGoingFast, entity.getUuid(), move, swing, 0, ticks);
    }

    @Override
    protected void renderBody(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.renderBody(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        getWings().renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes.interpolatorId);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getWings().setVisible(visible);
    }
}
