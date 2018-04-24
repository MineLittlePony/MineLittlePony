package com.minelittlepony.renderer.layer;

import com.minelittlepony.IPonyData;
import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.pony.ModelPlayerPony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL14;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerHeldPonyItem extends AbstractPonyLayer<EntityLivingBase> {

    public LayerHeldPonyItem(RenderLivingBase<? extends EntityLivingBase> livingPony) {
        super(livingPony, new LayerHeldItem(livingPony));
    }

    @Override
    public void doPonyRender(EntityLivingBase entity, float p_177141_2_, float p_177141_3_,
            float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ModelBase model = getRenderer().getMainModel();
        boolean mainRight = entity.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack itemMain = entity.getHeldItemMainhand();
        ItemStack itemOff = entity.getHeldItemOffhand();
        ItemStack left = mainRight ? itemOff : itemMain;
        ItemStack right = mainRight ? itemMain : itemOff;

        if (!left.isEmpty() || !right.isEmpty()) {
            pushMatrix();
            if (model instanceof AbstractPonyModel) {
                ((AbstractPonyModel) model).transform(BodyPart.LEGS);
            }

            if (model.isChild) {
                translate(0, 0.625, 0);
                rotate(-20, -1, 0, 0);
                scale(.5, .5, .5);
            }

            renderHeldItem(entity, right, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            renderHeldItem(entity, left, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);

            popMatrix();
        }
    }

    private void renderHeldItem(EntityLivingBase entity, ItemStack drop, ItemCameraTransforms.TransformType transform, EnumHandSide hand) {
        if (!drop.isEmpty()) {
            GlStateManager.pushMatrix();
            translateToHand(hand);

            if (entity.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean isUnicorn = isUnicorn(this.getRenderer().getMainModel());
            boolean isLeft = hand == EnumHandSide.LEFT;
            if (isUnicorn) {
                GlStateManager.translate(isLeft ? -0.6F : 0.1F, 1, -.5);
            } else {
                GlStateManager.translate(0.0425F, 0.125F, -1.00F);
            }
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, drop, transform, isLeft);

            if (isUnicorn) {
                IPonyData metadata = ((AbstractPonyModel) this.getRenderer().getMainModel()).metadata;
                this.renderItemGlow(entity, drop, transform, hand, metadata.getGlowColor());
            }
            GlStateManager.popMatrix();
        }
    }

    private static boolean isUnicorn(ModelBase model) {
        return model instanceof AbstractPonyModel && ((AbstractPonyModel) model).metadata.hasMagic();
    }

    protected void translateToHand(EnumHandSide hand) {
        AbstractPonyModel thePony = ((IRenderPony) this.getRenderer()).getPlayerModel().getModel();
        if (thePony.metadata.hasMagic()) {
            ModelPlayerPony playerModel = (ModelPlayerPony) thePony;
            ModelRenderer unicornarm = hand == EnumHandSide.LEFT ? playerModel.unicornArmLeft : playerModel.unicornArmRight;
            unicornarm.postRender(0.0625F);
        } else {
            ((ModelBiped) this.getRenderer().getMainModel()).postRenderArm(0.0625F, hand);
        }
    }

    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, ItemCameraTransforms.TransformType transform, EnumHandSide hand,
            int glowColor) {

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

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
