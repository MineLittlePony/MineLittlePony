package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
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
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {

        ItemStack left = getLeftItem(entity);
        ItemStack right = getRightItem(entity);

        if (!left.isEmpty() || !right.isEmpty()) {
            M model = getModel();

            pushMatrix();

            model.transform(BodyPart.LEGS);

            renderHeldItem(entity, right, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT);
            renderHeldItem(entity, left, ModelTransformation.Type.THIRD_PERSON_LEFT_HAND, Arm.LEFT);

            popMatrix();
        }
    }

    private void renderHeldItem(T entity, ItemStack drop, ModelTransformation.Type transform, Arm hand) {
        if (!drop.isEmpty()) {
            pushMatrix();
            renderArm(hand);

            if (getMainModel().getAttributes().isCrouching) {
                translatef(0, 0.2F, 0);
            }

            float left = hand == Arm.LEFT ? 1 : -1;

            if (entity.hasVehicle()) {
                translatef(left / 10, -0.2F, -0.5F);
            }

            rotatef(-90, 1, 0, 0);
            rotatef(left * 180, 0, 1, 0);
            translatef(left * -0.2F, 0, 0);

            preItemRender(entity, drop, transform, hand);
            MinecraftClient.getInstance().getItemRenderer().renderHeldItem(drop, entity, transform, hand == Arm.LEFT);
            postItemRender(entity, drop, transform, hand);

            popMatrix();
        }
    }

    protected void preItemRender(T entity, ItemStack drop, ModelTransformation.Type transform, Arm hand) {
        translatef(0, 0.125F, -1);
    }

    protected void postItemRender(T entity, ItemStack drop, ModelTransformation.Type transform, Arm hand) {
    }

    /**
     * Renders the main arm
     */
    protected void renderArm(Arm side) {
        getModel().setArmAngle(0.0625F, side);
    }
}
