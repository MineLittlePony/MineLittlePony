package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.IPegasus;
import com.minelittlepony.mson.api.ModelContext;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class PegasusModel<T extends LivingEntity> extends EarthPonyModel<T> implements IPegasus {

    private IPart wings;

    public PegasusModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public void init(ModelContext context) {
        super.init(context);
        wings = context.findByName("wings");
    }

    @Override
    public IPart getWings() {
        return wings;
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
