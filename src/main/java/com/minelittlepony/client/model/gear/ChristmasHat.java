package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.util.Color;

import java.util.Calendar;
import java.util.UUID;

public class ChristmasHat extends AbstractWearableGear {

    private static boolean dayChecked = false;
    private static boolean dayResult = false;
    private static boolean isChristmasDay() {
        if (!dayChecked) {
            dayChecked = true;
            Calendar cal = Calendar.getInstance();
            dayResult = cal.get(Calendar.MONTH) == Calendar.DECEMBER
                     && cal.get(Calendar.DAY_OF_MONTH) == 25;
        }


        return dayResult;
    }

    private final ModelPart left;
    private final ModelPart right;

    private int tint;

    public ChristmasHat(ModelPart tree) {
        super(Wearable.ANTLERS, BodyPart.HEAD);
        left = tree.getChild("left");
        right = tree.getChild("right");
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return isChristmasDay() || super.canRender(model, entity);
    }

    @Override
    public void pose(IModel model, Entity entity, boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float pi = MathHelper.PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662f;
        float srt = swing / 10;

        bodySwing = MathHelper.cos(mve + pi) * srt;

        bodySwing += 0.1F;

        tint = model.getMetadata().getGlowColor();
        left.roll = bodySwing;
        right.roll = -bodySwing;
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        if (tint != 0) {
            red = Color.r(tint);
            green = Color.g(tint);
            blue = Color.b(tint);
        }

        left.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        right.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }
}
