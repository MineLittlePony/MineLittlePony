package com.minelittlepony.model.player;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.render.PonyRenderer;

import com.minelittlepony.render.plane.PlaneRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

public class ModelEarthPony extends AbstractPonyModel {

    private final boolean smallArms;

    public PonyRenderer bipedCape;
    public PlaneRenderer bag;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (bipedCape != null) {
            bipedCape.rotationPointY = isSneak ? 2 : isRiding ? -4 : 0;
        }
        if (bag != null && metadata.hasBags()) {
            float angleY = 0;
            if (swingProgress > -9990.0F && !metadata.hasMagic()) {
                angleY = MathHelper.sin(MathHelper.sqrt(swingProgress) * PI * 2) * 0.02F;
            }
            bag.rotateAngleY = angleY;
        }
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);

        bipedBody.postRender(this.scale);
        if (bag != null && metadata.hasBags()) {
            bag.render(scale);
        }
    }

    @Override
    protected void initTextures() {
        super.initTextures();
        if (metadata.hasBags()) {
            bag = new PlaneRenderer(this, 56, 19);
        }
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        super.initPositions(yOffset, stretch);

        if (bag == null || !metadata.hasBags()) {
            return;
        }
        bag.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
                .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .tex(56, 25).addBackPlane(-7, -5, -4, 3, 6, stretch) //right bag front
                .addBackPlane(4, -5, -4, 3, 6, stretch) //left bag front
                .tex(59, 25).addBackPlane(-7, -5, 4, 3, 6, stretch) //right bag back
                .addBackPlane(4, -5, 4, 3, 6, stretch) //left bag back
                .tex(56, 19).addWestPlane(-7, -5, -4, 6, 8, stretch) //right bag outside
                .addWestPlane(7, -5, -4, 6, 8, stretch) //left bag outside
                .addWestPlane(-4.01f, -5, -4, 6, 8, stretch) //right bag inside
                .addWestPlane(4.01f, -5, -4, 6, 8, stretch) //left bag inside
                .tex(56, 31).addTopPlane(-4, -4.5F, -1, 8, 1, stretch) //strap front
                .addTopPlane(-4, -4.5F, 0, 8, 1, stretch) //strap back
                .addBackPlane(-4, -4.5F, 0, 8, 1, stretch)
                .addFrontPlane(-4, -4.5F, 0, 8, 1, stretch)
                .child(0).tex(56, 16).addTopPlane(2, -5, -13, 8, 3, stretch) //left bag top
                .addTopPlane(2, -5, -2, 8, 3, stretch) //right bag top
                .tex(56, 22).flipZ().addBottomPlane(2, 1, -13, 8, 3, stretch) //left bag bottom
                .flipZ().addBottomPlane(2, 1, -2, 8, 3, stretch) //right bag bottom
                .rotateAngleY = 4.712389F;
    }

    protected float getLegOutset() {
        if (smallArms) {
            if (isSleeping) return 2.6f;
            if (isCrouching()) return 1;
            return 4;
        }
        return super.getLegOutset();
    }

    protected int getArmWidth() {
        return smallArms ? 3 : super.getArmWidth();
    }

    protected float getLegRotationX() {
        return smallArms ? 2 : super.getLegRotationX();
    }

    protected float getArmRotationY() {
        return smallArms ? 8.5f : super.getArmRotationY();
    }

    protected void initHeadTextures() {
        super.initHeadTextures();
        bipedCape = new PonyRenderer(this, 0, 0).size(64, 32);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        super.initHeadPositions(yOffset, stretch);
        bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
    }

    @Override
    public void renderCape(float scale) {
        bipedCape.render(scale);
    }
}
