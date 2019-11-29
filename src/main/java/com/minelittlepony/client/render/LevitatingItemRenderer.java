package com.minelittlepony.client.render;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.pony.IPony;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class LevitatingItemRenderer {

    private static boolean usingTransparency;

    public static boolean usesTransparency() {
        return usingTransparency;
    }

    /**
     * Renders a magical overlay over an item in third person.
     */
    public void renderItemGlow(LivingEntity entity, ItemStack drop, ModelTransformation.Type transform, Arm hand, int glowColor, MatrixStack stack, VertexConsumerProvider renderContext) {

        // TODO: mixin into RenderLayer.getItemLayer(itemstack) to enable transparency
        usingTransparency = true;

        stack.push();
        setColor(glowColor);

        ItemRenderer renderItem = MinecraftClient.getInstance().getItemRenderer();

        stack.scale(1.1F, 1.1F, 1.1F);

        stack.translate(0.01F, 0.01F, 0.01F);
        renderItem.method_23177(entity, drop, transform, hand == Arm.LEFT, stack, renderContext, entity.world, 0x0F00F0, OverlayTexture.DEFAULT_UV);
        stack.translate(-0.02F, -0.02F, -0.02F);
        renderItem.method_23177(entity, drop, transform, hand == Arm.LEFT, stack, renderContext, entity.world, 0x0F00F0, OverlayTexture.DEFAULT_UV);


        unsetColor();
        stack.pop();

        usingTransparency = false;
    }

    private void setColor(int glowColor) {
        //GL14.glBlendColor(Color.r(glowColor), Color.g(glowColor), Color.b(glowColor), 0.2F);
    }

    private void unsetColor() {
        //GL14.glBlendColor(255, 255, 255, 1);
    }

    /**
     * Renders an item in first person optionally with a magical overlay.
     */
    public void renderItemInFirstPerson(FirstPersonRenderer renderer, @Nullable AbstractClientPlayerEntity entity, ItemStack stack, ModelTransformation.Type transform, boolean left, MatrixStack matrix, VertexConsumerProvider renderContext, @Nullable World world, int lightUv) {
        IPony pony = MineLittlePony.getInstance().getManager().getPony(entity);

        matrix.push();

        boolean doMagic = MineLittlePony.getInstance().getConfig().fpsmagic.get() && pony.getMetadata().hasMagic();

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        if (doMagic) {
            setupPerspective(itemRenderer, entity, stack, left, matrix);
        }

        itemRenderer.method_23177(entity, stack, transform, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV);

        if (doMagic) {
            usingTransparency = true;

            setColor(pony.getMetadata().getGlowColor());

            matrix.scale(1.1F, 1.1F, 1.1F);

            matrix.translate(0.015F, 0.01F, 0.01F);
            itemRenderer.method_23177(entity, stack, transform, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV);
            matrix.translate(-0.03F, -0.02F, -0.02F);
            itemRenderer.method_23177(entity, stack, transform, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV);

            usingTransparency = false;

            unsetColor();
        }

        matrix.pop();
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    private void setupPerspective(ItemRenderer renderer, LivingEntity entity, ItemStack item, boolean left, MatrixStack stack) {
        UseAction action = item.getUseAction();

        boolean doNormal = entity.getItemUseTime() <= 0 || action == UseAction.NONE || action == UseAction.CROSSBOW;

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            float ticks = MinecraftClient.getInstance().getTickDelta() - entity.age;

            float floatAmount = (float)Math.sin(ticks / 9) / 40;
            float driftAmount = (float)Math.cos(ticks / 6) / 40;

            boolean handHeldTool =
                       action == UseAction.BOW
                    || action == UseAction.CROSSBOW
                    || action == UseAction.BLOCK;

            stack.translate(driftAmount - floatAmount / 4, floatAmount, handHeldTool ? -0.3F : -0.6F);

            if (/*!renderer.hasDepthInGui(item) && */!handHeldTool) { // bows have to point forwards
                int sign = left ? 1 : -1;
                stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(sign * -60));
                stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(sign * 30));
            }
        }
    }
}
