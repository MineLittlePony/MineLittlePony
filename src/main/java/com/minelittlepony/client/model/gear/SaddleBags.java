package com.minelittlepony.client.model.gear;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.IPegasus;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.util.MathUtil;

import java.util.UUID;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SaddleBags extends AbstractWearableGear {

    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/saddlebags.png");

    private final ModelPart leftBag;
    private final ModelPart rightBag;

    private final ModelPart strap;

    private boolean hangLow = false;

    private float dropAmount = 0;

    public SaddleBags(ModelPart tree, Wearable wearable) {
        super(wearable, BodyPart.BODY);
        strap = tree.getChild("strap");
        leftBag = tree.getChild("left_bag");
        rightBag = tree.getChild("right_bag");
    }

    @Override
    public void pose(IModel model, Entity entity, boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        hangLow = false;

        if (model instanceof IPegasus) {
            hangLow = model.canFly() && ((IPegasus)model).wingsAreOpen();
        }

        float pi = MathHelper.PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662f;
        float srt = swing / 10;

        bodySwing = MathHelper.cos(mve + pi) * srt;

        leftBag.pitch = bodySwing;
        rightBag.pitch = bodySwing;

        if (model instanceof IPegasus && model.isFlying()) {
            bodySwing = ((IPegasus)model).getWingRotationFactor(ticks) - MathUtil.Angles._270_DEG;
            bodySwing /= 10;
        }

        leftBag.roll = bodySwing;
        rightBag.roll = -bodySwing;

        leftBag.visible = wearable == Wearable.SADDLE_BAGS_BOTH || wearable == Wearable.SADDLE_BAGS_LEFT;
        rightBag.visible = wearable == Wearable.SADDLE_BAGS_BOTH || wearable == Wearable.SADDLE_BAGS_RIGHT;
        strap.visible = wearable == Wearable.SADDLE_BAGS_BOTH;

        dropAmount = hangLow ? 0.15F : 0;
        dropAmount = model.getMetadata().getInterpolator(interpolatorId).interpolate("dropAmount", dropAmount, 3);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {

        stack.push();
        if (wearable == Wearable.SADDLE_BAGS_BOTH) {
            stack.translate(0, 0, -0.2F);
        }

        stack.push();
        stack.translate(0, dropAmount, 0);

        if (wearable != Wearable.SADDLE_BAGS_BOTH) {
            stack.translate(0, 0.3F, -0.3F);
        }

        leftBag.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
        rightBag.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);

        stack.pop();
        strap.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);

        stack.pop();
    }
}
