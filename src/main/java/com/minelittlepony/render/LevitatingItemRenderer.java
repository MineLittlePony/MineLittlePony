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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LevitatingItemRenderer {
    public static final LevitatingItemRenderer instance = new LevitatingItemRenderer();

    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, TransformType transform, EnumHandSide hand, int glowColor) {

        // enchantments mess up the rendering
        drop = stackWithoutEnchantment(drop);

        pushMatrix();
        disableLighting();

        GL14.glBlendColor(Color.r(glowColor), Color.g(glowColor), Color.b(glowColor), 0.2F);

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        ((IRenderItem) renderItem).useTransparency(true);

        scale(1.1, 1.1, 1.1);

        translate(0, 0.01F, 0.01F);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);
        translate(0.01F, -0.01F, -0.02F);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);

        ((IRenderItem) renderItem).useTransparency(false);
        enableLighting();
        popMatrix();

        // I hate rendering
    }

    public void renderItemInFirstPerson(ItemRenderer renderer, AbstractClientPlayer entity, ItemStack stack, TransformType transform, boolean left) {

        Pony pony = MineLittlePony.getInstance().getManager().getPony(entity);

        pushMatrix();



        if (pony.getMetadata().hasMagic()) {

            float ticks = ((IMinecraft)Minecraft.getMinecraft()).getTimer().elapsedPartialTicks - entity.ticksExisted;

            float floatAmount = (float)Math.sin(ticks / 9) / 40;
            float driftAmount = (float)Math.cos(ticks / 10) / 40;

            translate(driftAmount - floatAmount / 4, floatAmount, -0.6F);

            if (left) {
                rotate(-60, 0, 1, 0);
                rotate(30, 0, 0, 1);
            } else {
                rotate(60, 0, 1, 0);
                rotate(-30, 0, 0, 1);
            }
        }

        renderer.renderItemSide(entity, stack, transform, left);

        if (pony.getMetadata().hasMagic()) {

            disableLighting();

            IRenderItem renderItem = (IRenderItem)Minecraft.getMinecraft().getRenderItem();
            renderItem.useTransparency(true);

            int glowColor = pony.getMetadata().getGlowColor();

            GL14.glBlendColor(Color.r(glowColor), Color.g(glowColor), Color.b(glowColor), 0.2F);

            scale(1.1, 1.1, 1.1);

            translate(0, 0.01F, 0.01F);
            renderer.renderItemSide(entity, stack, transform, left);
            translate(0.01F, -0.01F, -0.02F);
            renderer.renderItemSide(entity, stack, transform, left);

            renderItem.useTransparency(false);
            enableLighting();

        }

        popMatrix();

        // I hate rendering
    }

    private ItemStack stackWithoutEnchantment(ItemStack original) {
        if (original.isItemEnchanted()) {
            original = original.copy();
            original.getTagCompound().removeTag("ench");
        }
        return original;
    }
}
