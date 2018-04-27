package com.minelittlepony.render.layer;

import static net.minecraft.client.renderer.GlStateManager.disableLighting;
import static net.minecraft.client.renderer.GlStateManager.enableLighting;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL14;

import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.ponies.ModelPlayerPony;
import com.minelittlepony.pony.data.IPonyData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;

public class LayerHeldPonyItemMagical<T extends EntityLivingBase> extends LayerHeldPonyItem<T> {

    public LayerHeldPonyItemMagical(RenderLivingBase<T> livingPony) {
        super(livingPony);
    }

    private boolean isUnicorn() {
        ModelBase model = getRenderer().getMainModel();
        return model instanceof AbstractPonyModel && ((AbstractPonyModel) model).metadata.hasMagic();
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
            IPonyData metadata = ((AbstractPonyModel) getRenderer().getMainModel()).metadata;
            renderItemGlow(entity, drop, transform, hand, metadata.getGlowColor());
        }
    }

    /**
     * Renders the main arm
     */
    protected void renderArm(EnumHandSide side) {
        AbstractPonyModel thePony = ((IRenderPony) getRenderer()).getPlayerModel().getModel();
        if (thePony.metadata.hasMagic()) {
            ModelPlayerPony playerModel = (ModelPlayerPony) thePony;
            ModelRenderer unicornarm = side == EnumHandSide.LEFT ? playerModel.unicornArmLeft : playerModel.unicornArmRight;
            unicornarm.postRender(0.0625F);
        } else {
            super.renderArm(side);
        }
    }

    public void renderItemGlow(T entity, ItemStack drop, TransformType transform, EnumHandSide hand, int glowColor) {

        // enchantments mess up the rendering
        ItemStack drop2 = drop.copy();
        if (drop2.hasEffect()) {
            removeEnch(drop2.getTagCompound());
        }
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

        translate(0, .01, .01);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);
        translate(.01, -.01, -.02);
        renderItem.renderItem(drop, entity, transform, hand == EnumHandSide.LEFT);

        ((IRenderItem) renderItem).useTransparency(false);
        enableLighting();
        popMatrix();

        // I hate rendering
    }

    private void removeEnch(@Nullable NBTTagCompound tag) {
        if (tag != null) {
            tag.removeTag("ench");
        }
    }
}
