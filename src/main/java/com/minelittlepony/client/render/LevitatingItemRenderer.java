package com.minelittlepony.client.render;

import org.lwjgl.opengl.GL14;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.util.render.Color;
import com.minelittlepony.pony.IPony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.UseAction;

import static com.mojang.blaze3d.platform.GlStateManager.*;

public class LevitatingItemRenderer {

    private static boolean usingTransparency;

    public static boolean enableItemGlowRenderProfile() {
        if (usesTransparency()) {
            enableBlend();
            blendFuncSeparate(SourceFactor.CONSTANT_COLOR, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
        }

        return usesTransparency();
    }

    public static boolean usesTransparency() {
        return usingTransparency;
    }

    /**
     * Renders a magical overlay over an item in third person.
     */
    public void renderItemGlow(LivingEntity entity, ItemStack drop, ModelTransformation.Type transform, Arm hand, int glowColor) {
        pushMatrix();
        disableLighting();
        setColor(glowColor);

        ItemRenderer renderItem = MinecraftClient.getInstance().getItemRenderer();

        usingTransparency = true;

        scalef(1.1F, 1.1F, 1.1F);

        translatef(0.01F, 0.01F, 0.01F);
        renderItem.renderHeldItem(drop, entity, transform, hand == Arm.LEFT);
        translatef(-0.02F, -0.02F, -0.02F);
        renderItem.renderHeldItem(drop, entity, transform, hand == Arm.LEFT);

        usingTransparency = false;
        unsetColor();
        enableLighting();
        popMatrix();

        // I hate rendering
    }

    private void setColor(int glowColor) {
        Color.glBlendColour(glowColor, 0.2F);
    }

    private void unsetColor() {
        GL14.glBlendColor(255, 255, 255, 1);
    }

    /**
     * Renders an item in first person optionally with a magical overlay.
     */
    public void renderItemInFirstPerson(FirstPersonRenderer renderer, AbstractClientPlayerEntity entity, ItemStack stack, ModelTransformation.Type transform, boolean left) {
        IPony pony = MineLittlePony.getInstance().getManager().getPony(entity);

        pushMatrix();

        boolean doMagic = MineLittlePony.getInstance().getConfig().fpsmagic.get() && pony.getMetadata().hasMagic();

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        if (doMagic) {
            setupPerspective(itemRenderer, entity, stack, left);
        }

        renderer.renderItemFromSide(entity, stack, transform, left);

        if (doMagic) {
            disableLighting();

            usingTransparency = true;

            setColor(pony.getMetadata().getGlowColor());

            scalef(1.1F, 1.1F, 1.1F);

            translatef(0.015F, 0.01F, 0.01F);
            renderer.renderItemFromSide(entity, stack, transform, left);
            translatef(-0.03F, -0.02F, -0.02F);
            renderer.renderItemFromSide(entity, stack, transform, left);

            usingTransparency = false;

            unsetColor();
            enableLighting();
        }

        popMatrix();

        // I hate rendering
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    private void setupPerspective(ItemRenderer renderer, LivingEntity entity, ItemStack stack, boolean left) {
        UseAction action = stack.getUseAction();

        boolean doNormal = entity.getItemUseTime() <= 0 || action == UseAction.NONE || action == UseAction.CROSSBOW;

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            float ticks = MinecraftClient.getInstance().getTickDelta() - entity.age;

            float floatAmount = (float)Math.sin(ticks / 9) / 40;
            float driftAmount = (float)Math.cos(ticks / 6) / 40;

            boolean handHeldTool = action == UseAction.BOW
                    || action == UseAction.CROSSBOW
                    || action == UseAction.BLOCK;

            translatef(driftAmount - floatAmount / 4, floatAmount, handHeldTool ? -0.3F : -0.6F);

            if (!renderer.hasDepthInGui(stack) && !handHeldTool) { // bows have to point forwards
                if (left) {
                    rotatef(-60, 0, 1, 0);
                    rotatef(30, 0, 0, 1);
                } else {
                    rotatef(60, 0, 1, 0);
                    rotatef(-30, 0, 0, 1);
                }
            }
        }
    }
}
