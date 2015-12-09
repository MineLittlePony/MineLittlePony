package com.brohoof.minelittlepony.renderer.layer;

import static net.minecraft.client.renderer.GlStateManager.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.pm_newPonyAdv;
import com.brohoof.minelittlepony.renderer.IRenderPony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
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

        if (!pony.getModel().isSleeping) {
            if (pony.getModel().metadata.getRace().hasHorn() && pony.getModel().metadata.getGlowColor() != 0) {
                pm_newPonyAdv model = (pm_newPonyAdv) pony.getModel();
                if (pony.getModel().aimedBow) {
                    renderDrop(pony, entity, model.unicornarm, 1.0F, 0.15F, 0.9375F, 0.0625F);
                } else if (pony.getModel().metadata.getSize() == PonySize.FOAL) {
                    renderDrop(pony, entity, model.unicornarm, 1.0F, 0.35F, 0.5375F, -0.8F);
                } else {
                    renderDrop(pony, entity, model.unicornarm, 1.0F, 0.35F, 0.5375F, -0.45F);
                }
            } else if (pony.getModel().metadata.getSize() == PonySize.FOAL) {
                renderDrop(pony, entity, pony.getModel().bipedRightArm, 1.0F, 0.08F, 0.8375F, 0.0625F);
            } else {
                renderDrop(pony, entity, pony.getModel().bipedRightArm, 1.0F, -0.0625F, 0.8375F, 0.0625F);
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

            if (pony.getModel().metadata.getRace().hasHorn() && pony.getModel().metadata.getGlowColor() != 0) {
                this.renderItemGlow(entity, drop, pony.getModel().metadata.getGlowColor());
            }

            popMatrix();
        }
    }

    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, int glowColor) {
        pushMatrix();
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        // GlStateManager.disableLighting();
        float red = (glowColor >> 16 & 255) / 255.0F;
        float green = (glowColor >> 8 & 255) / 255.0F;
        float blue = (glowColor & 255) / 255.0F;
        float alpha = 0.2F;
        enableBlend();
        blendFunc(32769, 1);
        GL14.glBlendColor(red, green, blue, alpha);
        color(red, green, blue, alpha);
        IBakedModel model = getItemModel(Minecraft.getMinecraft().getRenderItem(), entity, drop);
        applyTransform(model.getItemCameraTransforms().thirdPerson);
        if (!model.isGui3d()) {
            scale(1.5, 1.5, 1.5);
        } else {
            translate(0,-0.01,0);
            scale(.9, .9, .9);
        }
        Minecraft.getMinecraft().getRenderItem().renderItem(drop, model);
        GL11.glPopAttrib();
        disableBlend();
        popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    // Copied from RenderItem.renderItemModelForEntity
    private IBakedModel getItemModel(RenderItem renderer, Entity entityToRenderFor, ItemStack stack) {
        IBakedModel ibakedmodel = renderer.getItemModelMesher().getItemModel(stack);

        if (entityToRenderFor instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityToRenderFor;
            Item item = stack.getItem();
            ModelResourceLocation modelresourcelocation = null;

            if (item == Items.fishing_rod && entityplayer.fishEntity != null) {
                modelresourcelocation = new ModelResourceLocation("fishing_rod_cast", "inventory");
            } else if (item == Items.bow && entityplayer.getItemInUse() != null) {
                int i = stack.getMaxItemUseDuration() - entityplayer.getItemInUseCount();

                if (i >= 18) {
                    modelresourcelocation = new ModelResourceLocation("bow_pulling_2", "inventory");
                } else if (i > 13) {
                    modelresourcelocation = new ModelResourceLocation("bow_pulling_1", "inventory");
                } else if (i > 0) {
                    modelresourcelocation = new ModelResourceLocation("bow_pulling_0", "inventory");
                }
            }

            if (modelresourcelocation != null) {
                ibakedmodel = renderer.getItemModelMesher().getModelManager().getModel(modelresourcelocation);
            }
        }
        return ibakedmodel;
    }

    // Adapted from RenderItem
    private void applyTransform(ItemTransformVec3f transform) {
        translate(transform.translation.x + RenderItem.debugItemOffsetX,
                transform.translation.y + RenderItem.debugItemOffsetY,
                transform.translation.z + RenderItem.debugItemOffsetZ);
        translate(0f, .063f, -0.18);

        rotate(transform.rotation.y + RenderItem.debugItemRotationOffsetY, 0.0F, 1.0F, 0.0F);
        rotate(transform.rotation.x + RenderItem.debugItemRotationOffsetX, 1.0F, 0.0F, 0.0F);
        rotate(transform.rotation.z + RenderItem.debugItemRotationOffsetZ, 0.0F, 0.0F, 1.0F);


    }
}
