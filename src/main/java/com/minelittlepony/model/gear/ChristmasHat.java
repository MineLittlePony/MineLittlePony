package com.minelittlepony.model.gear;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.pony.data.PonyWearable;
import com.minelittlepony.render.model.PonyRenderer;
import com.minelittlepony.util.render.Color;

import java.util.Calendar;

public class ChristmasHat extends AbstractGear {

    private static final ResourceLocation TEXTURE = new ResourceLocation("minelittlepony", "textures/models/antlers.png");

    private PonyRenderer left;
    private PonyRenderer right;

    private int tint;

    @Override
    public void init(float yOffset, float stretch) {
        this.boxList.clear();

        left = new PonyRenderer(this, 0, 0).size(16, 8)
                .around(-7, 0.5F, 0.5F)
                .offset(-7, 0, 0)
                .at(3, -4, 0)
                .box(0, 0, 0, 7, 1, 1, stretch)
                .tex(0, 2).box(0, -1, 0, 1, 1, 1, stretch)
                .tex(4, 2).box(2, -1, 0, 1, 1, 1, stretch)
                .tex(8, 2).box(4, -1, 0, 1, 1, 1, stretch);

        right = new PonyRenderer(this, 0, 4).size(16, 8)
                .around(7, 0.5F, 0.5F)
                .offset(0, 0, 0)
                .at(-3, -4, 0)
                .box(0, 0, 0, 7, 1, 1, stretch)
                .tex(0, 6).box(6, -1, 0, 1, 1, 1, stretch)
                .tex(4, 6).box(4, -1, 0, 1, 1, 1, stretch)
                .tex(8, 6).box(2, -1, 0, 1, 1, 1, stretch);
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return isChristmasDay() || model.isWearing(PonyWearable.ANTLERS);
    }

    @Override
    public void setLivingAnimations(IModel model, Entity entity) {
        tint = model.getMetadata().getGlowColor();
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, float move, float swing, float bodySwing, float ticks) {
        float pi = PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662f;
        float srt = swing / 10;

        bodySwing = MathHelper.cos(mve + pi) * srt;

        bodySwing += 0.1F;

        left.rotateAngleZ = bodySwing;
        right.rotateAngleZ = -bodySwing;
    }

    private boolean isChristmasDay() {
        Calendar cal = Calendar.getInstance();

        return cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) == 25;
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public ResourceLocation getTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    public void renderPart(float scale) {
        GlStateManager.pushAttrib();
        if (tint != 0) {
            Color.glColor(tint, 1);
        }

        left.render(scale);
        right.render(scale);

        GlStateManager.popAttrib();
    }

}
