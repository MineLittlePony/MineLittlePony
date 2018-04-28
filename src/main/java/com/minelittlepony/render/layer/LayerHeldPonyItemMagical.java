package com.minelittlepony.render.layer;

import static net.minecraft.client.renderer.GlStateManager.disableLighting;
import static net.minecraft.client.renderer.GlStateManager.enableLighting;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import org.lwjgl.opengl.GL14;

import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.model.capabilities.IModelUnicorn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class LayerHeldPonyItemMagical<T extends EntityLivingBase> extends LayerHeldPonyItem<T> {

    public LayerHeldPonyItemMagical(RenderLivingBase<T> livingPony) {
        super(livingPony);
    }

    protected boolean isUnicorn() {
        ModelBase model = getMainModel();
        return model instanceof IModelUnicorn && ((IModelUnicorn) model).canCast();
    }

    protected void preItemRender(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
        if (isUnicorn()) {
            GlStateManager.translate(hand == EnumHandSide.LEFT ? -0.6F : 0.1F, 1, -0.5F);
        } else {
            super.preItemRender(entity, drop, transform, hand);
        }
    }

    protected void postItemRender(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
        if (isUnicorn()) {
            renderItemGlow(entity, drop, transform, hand, getPonyModel().metadata.getGlowColor());
        }
    }

    /**
     * Renders the main arm
     */
    protected void renderArm(EnumHandSide side) {
        if (isUnicorn()) {
            ((IModelUnicorn)getMainModel()).getUnicornArmForSide(side).postRender(0.0625F);
        } else {
            super.renderArm(side);
        }
    }

    public void renderItemGlow(T entity, ItemStack drop, TransformType transform, EnumHandSide hand, int glowColor) {

        // enchantments mess up the rendering
        drop = stackWithoutEnchantment(drop);

        float red = (glowColor >> 16 & 255) / 255.0F;
        float green = (glowColor >> 8 & 255) / 255.0F;
        float blue = (glowColor & 255) / 255.0F;
        float alpha = 0.2F;

        pushMatrix();
        disableLighting();

        GL14.glBlendColor(red, green, blue, alpha);

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

    private ItemStack stackWithoutEnchantment(ItemStack original) {
        ItemStack copy = original.copy();
        if (copy.isItemEnchanted()) {
            copy.getTagCompound().removeTag("ench");
        }
        return copy;
    }
}
