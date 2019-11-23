package com.minelittlepony.client.model.entities;

import com.minelittlepony.client.model.armour.ModelPonyArmour;
import com.minelittlepony.client.model.armour.ArmourWrapper;
import com.minelittlepony.client.model.races.ModelUnicorn;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPony;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class ModelSeapony<T extends LivingEntity> extends ModelUnicorn<T> {

    private ModelPart bodyCenter;

    private ModelPart leftFin;
    private ModelPart centerFin;
    private ModelPart rightFin;

    public ModelSeapony(boolean smallArms) {
        super(smallArms);
        textureHeight = 64;
    }

    public ModelSeapony() {
        this(false);
    }

    @Override
    public IEquestrianArmour<?> createArmour() {
        return new ArmourWrapper<>(Armour::new);
    }

    @Override
    public void updateLivingState(T entity, IPony pony) {
        super.updateLivingState(entity, pony);

        // Seaponies can't sneak, silly
        isSneaking = false;
        attributes.isCrouching = false;
    }

    @Override
    protected void ponySleep() {}

    @Override
    protected void ponyRide() {}

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);

        float flapMotion = MathHelper.cos(ticks / 10) / 5;

        if (attributes.isSleeping) {
            flapMotion /= 2;
        }

        float finAngle = FIN_ROT_Y + flapMotion;

        leftFin.yaw = finAngle;
        rightFin.yaw = -finAngle;

        if (!attributes.isSleeping) {
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
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        setVisible(leftSleeve.visible);

        super.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    @Override
    public void transform(BodyPart part, MatrixStack stack) {
        stack.translate(0, 0.6F, 0);
        super.transform(part, stack);
    }

    @Override
    protected void renderBody(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        torso.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        bodyCenter.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        torso.rotate(stack);

        tail.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes.interpolatorId);

        GlStateManager.enableBlend();


        leftFin.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        centerFin.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        rightFin.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);

        GlStateManager.disableBlend();
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
        leftPantLeg.visible = false;
        rightPantLeg.visible = false;

        bodyCenter.visible = visible;

        leftFin.visible = visible;
        centerFin.visible = visible;
        rightFin.visible = visible;
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
        public void transform(BodyPart part, MatrixStack stack) {
            stack.translate(0, 0.6F, 0);

            super.transform(part, stack);
        }
    }
}
