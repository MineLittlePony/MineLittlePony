package com.minelittlepony.renderer.layer;

import com.minelittlepony.PonyData;
import com.minelittlepony.ducks.IRenderItem;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.pony.ModelHumanPlayer;
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
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL14;

import javax.annotation.Nonnull;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerHeldPonyItem implements LayerRenderer<EntityLivingBase> {

    protected final RenderLivingBase<? extends EntityLivingBase> livingPonyEntity;
    private LayerHeldItem held;

    public LayerHeldPonyItem(RenderLivingBase<? extends EntityLivingBase> livingPony) {
        this.livingPonyEntity = livingPony;
        this.held = new LayerHeldItem(livingPony);
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entity, float p_177141_2_, float p_177141_3_,
                              float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ModelBase model = livingPonyEntity.getMainModel();
        if (model instanceof ModelHumanPlayer) {
            held.doRenderLayer(entity, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_,
                    p_177141_7_, scale);
            return;
        }
        boolean mainRight = entity.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack itemMain = entity.getHeldItemMainhand();
        ItemStack itemOff = entity.getHeldItemOffhand();
        ItemStack left = mainRight ? itemOff : itemMain;
        ItemStack right = mainRight ? itemMain : itemOff;

        if (left != null || right != null) {
            pushMatrix();
            if (model instanceof AbstractPonyModel) {
                ((AbstractPonyModel) model).transform(BodyPart.LEGS);
            }

            if (this.livingPonyEntity.getMainModel().isChild) {
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
        if (drop != null) {
            GlStateManager.pushMatrix();
            translateToHand(hand);

            if (entity.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean isUnicorn = isUnicorn(this.livingPonyEntity.getMainModel());
            boolean isLeft = hand == EnumHandSide.LEFT;
            if (isUnicorn) {
                GlStateManager.translate(isLeft ? -0.6F : 0.1F, 1, -.5);
            } else {
                GlStateManager.translate(0.0425F, 0.125F, -1.00F);
            }
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, drop, transform, isLeft);

            if (isUnicorn) {
                PonyData metadata = ((AbstractPonyModel) this.livingPonyEntity.getMainModel()).metadata;
                this.renderItemGlow(entity, drop, transform, hand, metadata.getGlowColor());
            }
            GlStateManager.popMatrix();
        }
    }

    private static boolean isUnicorn(ModelBase model) {
        return model instanceof AbstractPonyModel && ((AbstractPonyModel) model).metadata.hasMagic();
    }

    protected void translateToHand(EnumHandSide hand) {
        AbstractPonyModel thePony = ((IRenderPony) this.livingPonyEntity).getPony().getModel();
        if (thePony.metadata.hasMagic()) {
            ModelPlayerPony playerModel = (ModelPlayerPony) thePony;
            ModelRenderer unicornarm = hand == EnumHandSide.LEFT ? playerModel.unicornArmLeft : playerModel.unicornArmRight;
            unicornarm.postRender(0.0625F);
        } else {
            ((ModelBiped) this.livingPonyEntity.getMainModel()).postRenderArm(0.0625F, hand);
        }
    }

    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, ItemCameraTransforms.TransformType transform, EnumHandSide hand, int glowColor) {

        // enchantments mess up the rendering
        ItemStack drop2 = drop.copy();
        if (drop2.hasEffect())
            drop2.setTagInfo("ench", null);

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

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
