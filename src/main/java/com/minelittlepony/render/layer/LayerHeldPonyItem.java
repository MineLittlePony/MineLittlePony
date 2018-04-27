package com.minelittlepony.render.layer;

import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ponies.ModelPlayerPony;
import com.minelittlepony.pony.data.IPonyData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL14;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerHeldPonyItem<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    public LayerHeldPonyItem(RenderLivingBase<T> livingPony) {
        super(livingPony, new LayerHeldItem(livingPony));
    }

    @Override
    public void doPonyRender(T entity, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale) {
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

            renderHeldItem(entity, right, TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            renderHeldItem(entity, left, TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);

            popMatrix();
        }
    }

    private void renderHeldItem(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
        if (!drop.isEmpty()) {
            GlStateManager.pushMatrix();
            postRenderArm(hand);

            if (entity.isSneaking()) {
                GlStateManager.translate(0, 0.2F, 0);
            }

            GlStateManager.rotate(-90, 1, 0, 0);
            GlStateManager.rotate(180, 0, 1, 0);

            boolean isUnicorn = isUnicorn(getRenderer().getMainModel());
            boolean isLeft = hand == EnumHandSide.LEFT;

            if (isUnicorn) {
                GlStateManager.translate(isLeft ? -0.6F : 0.1F, 1, -0.5F);
            } else {
                GlStateManager.translate(0.0425F, 0.125F, -1);
            }

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, drop, transform, isLeft);

            if (isUnicorn) {
                IPonyData metadata = ((AbstractPonyModel) getRenderer().getMainModel()).metadata;
                renderItemGlow(entity, drop, transform, hand, metadata.getGlowColor());
            }
            GlStateManager.popMatrix();
        }
    }

    private static boolean isUnicorn(ModelBase model) {
        return model instanceof AbstractPonyModel && ((AbstractPonyModel) model).metadata.hasMagic();
    }

    /**
     * Renders the main arm
     */
    protected void postRenderArm(EnumHandSide side) {
        AbstractPonyModel thePony = ((IRenderPony) getRenderer()).getPlayerModel().getModel();
        if (thePony.metadata.hasMagic()) {
            ModelPlayerPony playerModel = (ModelPlayerPony) thePony;
            ModelRenderer unicornarm = side == EnumHandSide.LEFT ? playerModel.unicornArmLeft : playerModel.unicornArmRight;
            unicornarm.postRender(0.0625F);
        } else {
            ((ModelBiped) getRenderer().getMainModel()).postRenderArm(0.0625F, side);
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

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
