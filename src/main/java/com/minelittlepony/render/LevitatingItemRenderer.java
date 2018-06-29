package com.minelittlepony.render;

import org.lwjgl.opengl.GL14;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.util.coordinates.Color;
import com.mumfrey.liteloader.client.overlays.IMinecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import static net.minecraft.client.renderer.GlStateManager.*;

public class LevitatingItemRenderer {

    /**
     * Renders a magical overlay over an item in third person.
     */
    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, TransformType transform, EnumHandSide hand, int glowColor) {

        // enchantments mess up the rendering
        drop = stackWithoutEnchantment(drop);

        pushMatrix();
        disableLighting();
        setColor(glowColor);

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        ((IRenderItem) renderItem).useTransparency(true);
        PonySkullRenderer.ponyInstance.useTransparency(true);

        scale(1.1, 1.1, 1.1);

        translate(0, 0.01F, 0.01F);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);
        translate(0.01F, -0.01F, -0.02F);
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

        Pony pony = MineLittlePony.getInstance().getManager().getPony(entity);

        pushMatrix();

        boolean doMagic = MineLittlePony.getConfig().fpsmagic && pony.getMetadata().hasMagic();

        if (doMagic) {
            setupPerspective(entity, stack, left);
        }

        renderer.renderItemSide(entity, stack, transform, left);

        if (doMagic) {

            disableLighting();

            IRenderItem renderItem = (IRenderItem)Minecraft.getMinecraft().getRenderItem();
            renderItem.useTransparency(true);
            PonySkullRenderer.ponyInstance.useTransparency(true);

            setColor(pony.getMetadata().getGlowColor());

            scale(1.1, 1.1, 1.1);

            translate(0, 0.01F, 0.01F);
            renderer.renderItemSide(entity, stack, transform, left);
            translate(0.01F, -0.01F, -0.02F);
            renderer.renderItemSide(entity, stack, transform, left);

            renderItem.useTransparency(false);
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
    private void setupPerspective(EntityLivingBase entity, ItemStack stack, boolean left) {
        EnumAction action = stack.getItemUseAction();

        boolean doNormal = entity.getItemInUseCount() <= 0 || action == EnumAction.NONE;
        boolean doBow = doNormal && stack.getItemUseAction() == EnumAction.BOW;

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            float ticks = ((IMinecraft)Minecraft.getMinecraft()).getTimer().elapsedPartialTicks - entity.ticksExisted;

            float floatAmount = (float)Math.sin(ticks / 9) / 40;
            float driftAmount = (float)Math.cos(ticks / 6) / 40;

            translate(driftAmount - floatAmount / 4, floatAmount, doBow ? -0.3F : -0.6F);

            if (!stack.getItem().isFull3D() && !doBow) { // bows have to point forwards
                if (left) {
                    rotate(-60, 0, 1, 0);
                    rotate(30, 0, 0, 1);
                } else {
                    rotate(60, 0, 1, 0);
                    rotate(-30, 0, 0, 1);
                }
            }
        }
    }

    private ItemStack stackWithoutEnchantment(ItemStack original) {
        if (original.isItemEnchanted()) {
            original = original.copy();
            original.getTagCompound().removeTag("ench");
        }

        return original;
    }
}
