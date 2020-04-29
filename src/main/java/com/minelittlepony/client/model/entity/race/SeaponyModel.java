package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.model.armour.ArmourWrapper;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.mson.api.ModelContext;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class SeaponyModel<T extends LivingEntity> extends UnicornModel<T> {

    private ModelPart bodyCenter;

    private ModelPart leftFin;
    private ModelPart centerFin;
    private ModelPart rightFin;

    public SeaponyModel(boolean smallArms) {
        super(smallArms);
        textureHeight = 64;
    }

    public SeaponyModel() {
        this(false);
    }

    @Override
    public void init(ModelContext context) {
        super.init(context);
        bodyCenter = context.findByName("abdomin");
        leftFin = context.findByName("left_fin");
        rightFin = context.findByName("right_fin");
        centerFin = context.findByName("center_fin");
    }

    @Override
    public IEquestrianArmour<?> createArmour() {
        return new ArmourWrapper<>(Armour::new);
    }

    @Override
    public void updateLivingState(T entity, IPony pony, EquineRenderManager.Mode mode) {
        super.updateLivingState(entity, pony, mode);

        // Seaponies can't sneak, silly
        isSneaking = false;
        attributes.isCrouching = false;
    }

    @Override
    protected void ponySleep() {}

    @Override
    protected void ponySit() {}

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

        if (!entity.isSubmergedInWater()) {
            leftArm.pitch -= 0.5F;
            rightArm.pitch -= 0.5F;
        }

        if (!entity.isSubmergedInWater() || entity.isOnGround()) {
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
        leftFin.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        centerFin.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        rightFin.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    @Override
    public boolean canCast() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        upperTorsoOverlay.visible = false;

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

    class Armour extends PonyArmourModel<T> {

        @Override
        public void showBoots() {
            rightArm.visible = true;
            leftArm.visible = true;
        }

        @Override
        public void updateLivingState(T entity, IPony pony, EquineRenderManager.Mode mode) {
            super.updateLivingState(entity, pony, mode);

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
