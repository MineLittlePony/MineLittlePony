package com.minelittlepony.client.model.entities;

import com.minelittlepony.client.model.armour.ModelPonyArmour;
import com.minelittlepony.client.model.armour.ArmourWrapper;
import com.minelittlepony.client.model.components.SeaponyTail;
import com.minelittlepony.client.model.races.ModelUnicorn;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPony;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

public class ModelSeapony<T extends LivingEntity> extends ModelUnicorn<T> {

    PonyRenderer bodyCenter;

    PlaneRenderer leftFin;
    PlaneRenderer centerFin;
    PlaneRenderer rightFin;

    public ModelSeapony(boolean smallArms) {
        super(smallArms);
        textureHeight = 64;
    }

    public ModelSeapony() {
        this(false);
    }

    @Override
    public IEquestrianArmour<?> createArmour() {
        return new ArmourWrapper<>(new Armour(), new Armour());
    }

    @Override
    public void updateLivingState(T entity, IPony pony) {
        super.updateLivingState(entity, pony);

        // Seaponies can't sneak, silly
        isSneaking = false;
        isCrouching = false;
    }

    @Override
    protected void ponySleep() {
       // noop
    }

    @Override
    protected void ponyRide() {
        // noop
    }

    @Override
    protected void initLegs(float yOffset, float stretch) {
        super.initLegs(yOffset, stretch);
        // hide the back legs
        leftLeg.visible = false;
        rightLeg.visible = false;
        leftLegOverlay.visible = false;
        rightLegOverlay.visible = false;

        centerFin = new PlaneRenderer(this, 58, 28)
                .rotate(PI / 2 - 0.1F, 0, 0).around(0, 6, 9)
                .east(0, -6, 0, 12, 6, stretch);

        leftFin = new PlaneRenderer(this, 56, 16)
                .rotate(0, FIN_ROT_Y, 0).around(3, -6, 3)
               .flipZ().east(0, 0, 0, 12, 8, stretch);

        rightFin = new PlaneRenderer(this, 56, 16)
                .rotate(0, -FIN_ROT_Y, 0).around(-3, -6, 3)
                .west(0, 0, 0, 12, 8, stretch);
    }

    @Override
    protected void initTail(float yOffset, float stretch) {
        tail = new SeaponyTail(this);
        tail.init(yOffset, stretch);
    }

    @Override
    protected void initBody(float yOffset, float stretch) {
        super.initBody(yOffset, stretch);
        bodyCenter = new PonyRenderer(this, 0, 48)
                .around(0, 6, 1)
                .box(-3, -1, 0, 6, 7, 9, stretch).flip();
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        float flapMotion = MathHelper.cos(ticks / 10) / 5;

        if (isSleeping()) {
            flapMotion /= 2;
        }

        float finAngle = FIN_ROT_Y + flapMotion;

        leftFin.yaw = finAngle;
        rightFin.yaw = -finAngle;

        if (!isSleeping()) {
            centerFin.roll = flapMotion;
        }

        if (!entity.isInWater()) {
            leftArm.pitch -= 0.5F;
            rightArm.pitch -= 0.5F;
        }

        if (!entity.isInWater() || entity.onGround) {
            leftArm.yaw -= 0.5F;
            rightArm.yaw += 0.5F;
        }
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, T entity) {
        super.rotateLegs(move, swing, ticks, entity);
        leftArm.pitch -= 1.4F;
        leftArm.yaw -= 0.3F;
        rightArm.pitch -= 1.4F;
        rightArm.yaw += 0.3F;
    }

    @Override
    protected void rotateLegsSwimming(float move, float swing, float ticks, T entity) {
        super.rotateLegsOnGround(move, swing, ticks, entity);
    }

    @Override
    public void render(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        setVisible(leftArmOverlay.visible);

        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);
    }

    @Override
    public void transform(BodyPart part) {
        GlStateManager.translatef(0, 0.6F, 0);

        super.transform(part);
    }

    @Override
    protected void renderBody(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        body.render(scale);
        bodyCenter.render(scale);
        body.applyTransform(scale);

        tail.renderPart(scale, entity.getUuid());

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.enableBlend();


        leftFin.render(scale);
        centerFin.render(scale);
        rightFin.render(scale);

        GlStateManager.disableBlend();
        GL11.glPopAttrib();

    }

    @Override
    public boolean canCast() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        // hide the back legs
        leftLeg.visible = false;
        rightLeg.visible = false;
        leftLegOverlay.visible = false;
        rightLegOverlay.visible = false;
    }

    class Armour extends ModelPonyArmour<T> {

        @Override
        public void showBoots() {
            rightArm.visible = true;
            leftArm.visible = true;
        }

        @Override
        public void updateLivingState(T entity, IPony pony) {
            super.updateLivingState(entity, pony);

            // Seaponies can't sneak, silly
            isSneaking = false;
        }

        @Override
        protected void rotateLegsSwimming(float move, float swing, float ticks, T entity) {
            super.rotateLegsOnGround(move, swing, ticks, entity);
        }

        @Override
        public void transform(BodyPart part) {
            GlStateManager.translatef(0, 0.6F, 0);

            super.transform(part);
        }
    }
}
