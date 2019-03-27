package com.minelittlepony.client.render;

import org.lwjgl.opengl.GL14;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.MineLPClient;
import com.minelittlepony.client.ducks.IRenderItem;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.client.util.render.Color;
import com.minelittlepony.pony.IPony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import static net.minecraft.client.renderer.GlStateManager.*;

@SuppressWarnings("deprecation") // ItemCameraTransforms is deprecated by forge but we still need it.
public class LevitatingItemRenderer {

    public static void enableItemGlowRenderProfile() {
        enableBlend();
        blendFuncSeparate(SourceFactor.CONSTANT_COLOR, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
        Minecraft.getInstance().entityRenderer.disableLightmap();
    }

    /**
     * Renders a magical overlay over an item in third person.
     */
    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, TransformType transform, EnumHandSide hand, int glowColor) {
        pushMatrix();
        disableLighting();
        setColor(glowColor);

        ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
        ((IRenderItem) renderItem).useTransparency(true);
        PonySkullRenderer.ponyInstance.useTransparency(true);

        scalef(1.1F, 1.1F, 1.1F);

        translatef(0, 0.01F, 0.01F);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);
        translatef(0.01F, -0.01F, -0.02F);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);

        ((IRenderItem) renderItem).useTransparency(false);
        PonySkullRenderer.ponyInstance.useTransparency(false);
        unsetColor();
        enableLighting();
        popMatrix();

        // I hate rendering
    }

    private void setColor(int glowColor) {
        GL14.glBlendColor(Color.r(glowColor), Color.g(glowColor), Color.b(glowColor), 0.2F);
    }

    private void unsetColor() {
        GL14.glBlendColor(255, 255, 255, 1);
    }

    /**
     * Renders an item in first person optionally with a magical overlay.
     */
    public void renderItemInFirstPerson(ItemRenderer renderer, AbstractClientPlayer entity, ItemStack stack, TransformType transform, boolean left) {
        IPony pony = MineLittlePony.getInstance().getManager().getPony(entity);

        pushMatrix();

        boolean doMagic = MineLittlePony.getInstance().getConfig().fpsmagic && pony.getMetadata().hasMagic();

        if (doMagic) {
            setupPerspective(renderer, entity, stack, left);
        }

        renderer.renderItem(stack, entity, transform, left);

        if (doMagic) {
            disableLighting();

            ((IRenderItem)renderer).useTransparency(true);
            PonySkullRenderer.ponyInstance.useTransparency(true);

            setColor(pony.getMetadata().getGlowColor());

            scalef(1.1F, 1.1F, 1.1F);

            translatef(-0.015F, 0.01F, 0.01F);
            renderer.renderItem(stack, entity, transform, left);
            translatef(0.03F, -0.01F, -0.02F);
            renderer.renderItem(stack, entity, transform, left);

            ((IRenderItem)renderer).useTransparency(false);

            PonySkullRenderer.ponyInstance.useTransparency(false);

            unsetColor();
            enableLighting();
        }

        popMatrix();

        // I hate rendering
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    private void setupPerspective(ItemRenderer renderer, EntityLivingBase entity, ItemStack stack, boolean left) {
        EnumAction action = stack.getUseAction();

        boolean doNormal = entity.getItemInUseCount() <= 0 || action == EnumAction.NONE;

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            float ticks = MineLPClient.getInstance().getModUtilities().getRenderPartialTicks() - entity.ticksExisted;

            float floatAmount = (float)Math.sin(ticks / 9) / 40;
            float driftAmount = (float)Math.cos(ticks / 6) / 40;

            boolean handHeldTool = stack.getUseAction() == EnumAction.BOW
                    || stack.getUseAction() == EnumAction.BLOCK;

            translatef(driftAmount - floatAmount / 4, floatAmount, handHeldTool ? -0.3F : -0.6F);

            if (!renderer.shouldRenderItemIn3D(stack) && !handHeldTool) { // bows have to point forwards
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
