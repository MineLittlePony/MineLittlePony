package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.part.PonySnout;
import com.minelittlepony.model.IPart;
import com.minelittlepony.mson.api.ModelContext;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class EarthPonyModel<T extends LivingEntity> extends AbstractPonyModel<T> {

    private final boolean smallArms;

    protected IPart tail;
    protected PonySnout snout;
    protected IPart ears;

    public EarthPonyModel(ModelPart tree, boolean smallArms) {
        super(tree);
        this.smallArms = smallArms;
    }

    @Override
    public void init(ModelContext context) {
        super.init(context);

        tail = context.findByName("tail");
        snout = context.findByName("snout");
        ears = context.findByName("ears");
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);
        snout.setGender(getMetadata().getGender());
        cape.pivotY = sneaking ? 2 : riding ? -4 : 0;
    }

    @Override
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);
        tail.setRotationAndAngles(attributes.isSwimming || attributes.isGoingFast, attributes.interpolatorId, move, swing, bodySwing * 5, ticks);
    }

    @Override
    protected void renderBody(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.renderBody(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        tail.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes.interpolatorId);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        snout.setVisible(visible);
        tail.setVisible(visible);
    }

    @Override
    protected float getLegOutset() {
        if (smallArms) {
            return Math.max(1, super.getLegOutset() - 1);
        }
        return super.getLegOutset();
    }
}
