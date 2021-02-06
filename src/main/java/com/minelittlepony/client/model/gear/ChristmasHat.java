package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.PonyModelConstants;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.util.Color;

import java.util.Calendar;
import java.util.UUID;

public class ChristmasHat extends AbstractGear implements PonyModelConstants {

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

    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/antlers.png");

    private final ModelPart left;
    private final ModelPart right;

    private int tint;

    public ChristmasHat(ModelPart tree) {
        left = tree.getChild("left");
        right = tree.getChild("right");
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return isChristmasDay() || model.isWearing(Wearable.ANTLERS);
    }

    @Override
    public void setModelAttributes(IModel model, Entity entity) {
        tint = model.getMetadata().getGlowColor();
    }

    @Override
    public void pose(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float pi = PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662f;
        float srt = swing / 10;

        bodySwing = MathHelper.cos(mve + pi) * srt;

        bodySwing += 0.1F;

        left.roll = bodySwing;
        right.roll = -bodySwing;
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context) {
        return TEXTURE;
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
