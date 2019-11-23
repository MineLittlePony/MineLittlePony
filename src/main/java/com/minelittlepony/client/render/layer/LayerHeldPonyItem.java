package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

import static com.mojang.blaze3d.platform.GlStateManager.*;

public class LayerHeldPonyItem<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    public LayerHeldPonyItem(IPonyRender<T, M> livingPony) {
        super(livingPony);
    }

    protected ItemStack getLeftItem(T entity) {
        boolean main = entity.getMainArm() == Arm.LEFT;

        return main ? entity.getMainHandStack() : entity.getOffHandStack();
    }

    protected ItemStack getRightItem(T entity) {
        boolean main = entity.getMainArm() == Arm.RIGHT;

        return main ? entity.getMainHandStack() : entity.getOffHandStack();
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {

        ItemStack left = getLeftItem(entity);
        ItemStack right = getRightItem(entity);

        if (!left.isEmpty() || !right.isEmpty()) {
            M model = getModel();

            stack.push();

            model.transform(BodyPart.LEGS, stack);

            renderHeldItem(entity, right, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, stack, renderContext, lightUv);
            renderHeldItem(entity, left, ModelTransformation.Type.THIRD_PERSON_LEFT_HAND, Arm.LEFT, stack, renderContext, lightUv);

            stack.pop();
        }
    }

    private void renderHeldItem(T entity, ItemStack drop, ModelTransformation.Type transform, Arm arm, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        if (!drop.isEmpty()) {
            pushMatrix();
            renderArm(arm, stack);

            if (getMainModel().getAttributes().isCrouching) {
                translatef(0, 0.2F, 0);
            }

            float left = arm == Arm.LEFT ? 1 : -1;

            if (entity.hasVehicle()) {
                stack.translate(left / 10, -0.2F, -0.5F);
            }

            stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90));
            stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(left * 180));
            stack.translate(left * -0.2F, 0, 0);

            preItemRender(entity, drop, transform, arm, stack);
            MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(entity, drop, transform, arm == Arm.LEFT, stack, renderContext, lightUv);
            postItemRender(entity, drop, transform, arm, stack, renderContext);

            popMatrix();
        }
    }

    protected void preItemRender(T entity, ItemStack drop, ModelTransformation.Type transform, Arm hand, MatrixStack stack) {
        stack.translate(0, 0.125F, -1);
    }

    protected void postItemRender(T entity, ItemStack drop, ModelTransformation.Type transform, Arm hand, MatrixStack stack, VertexConsumerProvider renderContext) {
    }

    /**
     * Renders the main arm
     */
    protected void renderArm(Arm arm, MatrixStack stack) {
        getModel().setArmAngle(arm, stack);
    }
}
