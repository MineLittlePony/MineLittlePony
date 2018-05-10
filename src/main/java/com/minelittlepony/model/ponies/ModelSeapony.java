package com.minelittlepony.model.ponies;

import com.minelittlepony.model.player.ModelUnicorn;
import com.minelittlepony.render.PonyRenderer;

import net.minecraft.entity.Entity;

public class ModelSeapony extends ModelUnicorn {

    PonyRenderer bodyCenter;
    PonyRenderer bodyRear;
    PonyRenderer bodyBack;

    PonyRenderer tail;
    PonyRenderer tailFin;

    PonyRenderer leftFin;
    PonyRenderer centerFin;
    PonyRenderer rightFin;

    public ModelSeapony() {
        super(false);
        textureHeight = 64;
    }

    @Override
    protected void initLegTextures() {
        super.initLegTextures();

        // hide the back legs
        bipedLeftLeg.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLegwear.showModel = false;
        bipedRightLegwear.showModel = false;

        centerFin = new PonyRenderer(this, 58, 36);
        leftFin = new PonyRenderer(this, 56, 8);
        rightFin = new PonyRenderer(this, 56, 8);
    }

    @Override
    protected void initLegPositions(float yOffset, float stretch) {
        super.initLegPositions(yOffset, stretch);

        centerFin.around(0, 1, 9)
                .addBox(0, 0, 0, 0, 6, 6).flipX();

        leftFin.rotate(0, 0.5235988F, 0).around(3, -6, 3)
                  .addBox(0, 0, 0, 0, 12, 8).flipX();

        rightFin.rotate(0, -0.5235988F, 0).around(-3, -6, 3)
                .addBox(0, 0, 0, 0, 12, 8).flipX();
    }

    @Override
    protected void initTailTextures() {
        tail = new PonyRenderer(this, 24, 0);
        tailFin = new PonyRenderer(this, 56, 20);
    }

    @Override
    protected void initTailPositions(float yOffset, float stretch) {
        tail.rotate(1.570796F, 0, 0).around(-1, 12.5F, 14)
            .addBox(0, 0, 0, 2, 6, 1).flipX();
        tailFin.rotate(0, 0, -1.570796F).around(-2, 12, 18)
            .addBox(0, -5, 0, 0, 14, 8).flipX();
    }

    @Override
    protected void initBodyTextures() {
        super.initBodyTextures();

        bodyCenter = new PonyRenderer(this, 0, 48);
        bodyBack = new PonyRenderer(this, 0, 16);
        bodyRear = new PonyRenderer(this, 0, 38);

    }

    @Override
    protected void initBodyPositions(float yOffset, float stretch) {
        super.initBodyPositions(yOffset, stretch);

        bodyCenter.around(0, 6, 1)
                  .addBox(-3, -1, 0, 6, 7, 9).flipX();

        bodyBack.around(-4, 2, -1)
                  .addBox(0, 0, 0, 8, 8, 4);

        bodyRear.rotate(1.570796F, 0, 0).around(-2, 14, 8)
                  .addBox(0, 0, 0, 4, 6, 4).flipX();
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        bipedBody.render(scale);
        if (textureHeight == 64) {
            bipedBodyWear.render(scale);
        }

        bodyCenter.render(scale);
        bodyRear.render(scale);
        bodyBack.render(scale);

        bipedBody.postRender(scale);

        tail.render(scale);
        tailFin.render(scale);
    }

    @Override
    protected void renderLegs() {
        super.renderLegs();

        leftFin.render(scale);
        centerFin.render(scale);
        rightFin.render(scale);
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);
    }

    @Override
    public boolean canFly() {
        return false;
    }
}
