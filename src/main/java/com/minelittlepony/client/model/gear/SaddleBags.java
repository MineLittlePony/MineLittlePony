package com.minelittlepony.client.model.gear;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.IPegasus;
import com.minelittlepony.mson.api.ModelContext;

import java.util.UUID;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SaddleBags extends AbstractGear {

    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/saddlebags.png");

    private ModelPart leftBag;
    private ModelPart rightBag;

    private ModelPart strap;

    private boolean hangLow = false;

    float dropAmount = 0;

    private IModel model;

    @Override
    public void init(ModelContext context) {
        strap = context.findByName("strap");
        leftBag = context.findByName("left_bag");
        rightBag = context.findByName("right_bag");
    }

    @Override
    public void setLivingAnimations(IModel model, Entity entity) {
        this.model = model;

        hangLow = false;

        if (model instanceof IPegasus) {
            hangLow = model.canFly() && ((IPegasus)model).wingsAreOpen();
        }
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float pi = PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662f;
        float srt = swing / 10;

        bodySwing = MathHelper.cos(mve + pi) * srt;

        leftBag.pitch = bodySwing;
        rightBag.pitch = bodySwing;

        if (model instanceof IPegasus && model.isFlying()) {
            bodySwing = ((IPegasus)model).getWingRotationFactor(ticks) - ROTATE_270;
            bodySwing /= 10;
        }

        leftBag.roll = bodySwing;
        rightBag.roll = -bodySwing;

        dropAmount = hangLow ? 0.15F : 0;
    }

    public void sethangingLow(boolean veryLow) {
        hangLow = veryLow;
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        dropAmount = model.getMetadata().getInterpolator(interpolatorId).interpolate("dropAmount", dropAmount, 3);

        stack.push();
        stack.translate(0, dropAmount, 0);

        leftBag.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
        rightBag.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);

        stack.pop();
        strap.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.SADDLE_BAGS);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.BODY;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, IRenderContext<T, ?> context) {
        if (context.getEntityModel() != null && context.getEntityModel().getMetadata().getRace().isEquivalentTo(Race.CHANGELING)) {
            return TEXTURE;
        }
        return context.getDefaultTexture(entity, Wearable.SADDLE_BAGS);
    }
}
