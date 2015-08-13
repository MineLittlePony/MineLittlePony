package com.minelittlepony.minelp.renderer.layer;

import static net.minecraft.client.renderer.GlStateManager.*;

import com.minelittlepony.minelp.model.PMAPI;
import com.minelittlepony.minelp.model.PlayerModel;
import com.minelittlepony.minelp.model.pony.pm_newPonyAdv;
import com.minelittlepony.minelp.renderer.IRenderPony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public class LayerHeldPonyItem implements LayerRenderer {

    private final RendererLivingEntity livingPonyEntity;
    private LayerHeldItem held;

    public LayerHeldPonyItem(RendererLivingEntity livingPony) {
        this.livingPonyEntity = livingPony;
        this.held = new LayerHeldItem(livingPony);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float p_177141_2_, float p_177141_3_,
            float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        PlayerModel pony = ((IRenderPony) livingPonyEntity).getPony();
        if (pony == PMAPI.human) {
            held.doRenderLayer(entity, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_,
                    p_177141_7_, scale);
            return;
        }

        if (!pony.model.isSleeping) {
            if (pony.model.isUnicorn && pony.model.glowColor != 0) {
                pm_newPonyAdv model = (pm_newPonyAdv) pony.model;
                if (pony.model.aimedBow) {
                    renderDrop(pony, entity, model.unicornarm, 1.0F, 0.15F, 0.9375F, 0.0625F);
                } else if (pony.model.size == 0) {
                    renderDrop(pony, entity, model.unicornarm, 1.0F, 0.35F, 0.5375F, -0.8F);
                } else {
                    renderDrop(pony, entity, model.unicornarm, 1.0F, 0.35F, 0.5375F, -0.45F);
                }
            } else if (pony.model.size == 0) {
                renderDrop(pony, entity, pony.model.bipedRightArm, 1.0F, 0.08F, 0.8375F, 0.0625F);
            } else {
                renderDrop(pony, entity, pony.model.bipedRightArm, 1.0F, -0.0625F, 0.8375F, 0.0625F);
            }
        }
    }

    protected void renderDrop(PlayerModel pony, EntityLivingBase entity, ModelRenderer box, float scalefactor,
            float posx, float posy, float posz) {
        ItemStack drop = entity.getHeldItem();
        if (drop != null) {
            pushMatrix();
            if (box != null) {
                box.postRender(scalefactor * 0.0625F);
            }

            translate(posx, posy, posz);
            EnumAction playerAction = null;
            if (entity instanceof EntityPlayer) {
                EntityPlayer is3D = (EntityPlayer) entity;
                if (is3D.fishEntity != null) {
                    drop = new ItemStack(Items.stick);
                }

                if (is3D.getItemInUseCount() > 0) {
                    playerAction = drop.getItemUseAction();
                }
            }

            if (drop.getItem() == Items.bow) {
                rotate(-20.0F, 0.0F, 1.0F, 0.0F);
                rotate(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (drop.getItem().isFull3D()) {
                if (drop.getItem().shouldRotateAroundWhenRendering()) {
                    rotate(180.0F, 0.0F, 0.0F, 1.0F);
                    translate(0.0F, -0.125F, 0.0F);
                }

                if (playerAction == EnumAction.BLOCK && entity instanceof EntityPlayer
                        && ((EntityPlayer) entity).getItemInUseCount() > 0) {
                    translate(0.05F, 0.0F, -0.1F);
                    rotate(-50.0F, 0.0F, 1.0F, 0.0F);
                    rotate(-10.0F, 1.0F, 0.0F, 0.0F);
                    rotate(-60.0F, 0.0F, 0.0F, 1.0F);
                }
            }

            float g;
            float b;
            int var20;

            var20 = drop.getItem().getColorFromItemStack(drop, 0);
            float var19 = (var20 >> 16 & 255) / 255.0F;
            g = (var20 >> 8 & 255) / 255.0F;
            b = (var20 & 255) / 255.0F;
            color(var19, g, b, 1.0F);
            Minecraft.getMinecraft().getItemRenderer().renderItem(entity, drop, TransformType.THIRD_PERSON);

            if (pony.model.isUnicorn && pony.model.glowColor != 0) {
                this.renderItemGlow(entity, drop, pony.model.glowColor);
            }

            popMatrix();
        }
    }

    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, int glowColor) {
        // TODO reimplement
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
