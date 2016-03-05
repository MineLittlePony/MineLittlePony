package com.brohoof.minelittlepony.renderer.layer;

import static net.minecraft.client.renderer.GlStateManager.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.ducks.IRenderPony;
import com.brohoof.minelittlepony.model.BodyPart;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.ModelHumanPlayer;
import com.brohoof.minelittlepony.model.pony.ModelPlayerPony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldPonyItem implements LayerRenderer<EntityLivingBase> {

    private final RendererLivingEntity<? extends EntityLivingBase> livingPonyEntity;
    private LayerHeldItem held;

    public LayerHeldPonyItem(RendererLivingEntity<? extends EntityLivingBase> livingPony) {
        this.livingPonyEntity = livingPony;
        this.held = new LayerHeldItem(livingPony);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float p_177141_2_, float p_177141_3_,
            float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        PlayerModel pony = ((IRenderPony) livingPonyEntity).getPony();
        if (pony.getModel() instanceof ModelHumanPlayer) {
            held.doRenderLayer(entity, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_,
                    p_177141_7_, scale);
            return;
        }
        if (!pony.getModel().isSleeping) {
            if (pony.getModel().metadata.getRace().hasHorn() && pony.getModel().metadata.getGlowColor() != 0) {
                ModelPlayerPony model = (ModelPlayerPony) pony.getModel();
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
            pony.getModel().transform(BodyPart.LEGS);
            if (pony.getModel().isChild) {
                translate(0.0F, 0.625F, 0.0F);
                rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                scale(0.5F, 0.5F, 0.5F);
            }
            box.postRender(scalefactor * 0.0625F);

            translate(posx, posy, posz);
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).fishEntity != null) {
                drop = new ItemStack(Items.stick);
            }

            Item item = drop.getItem();
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().getRenderType() == 2) {
                translate(0.0F, 0.1875F, -0.3125F);
                rotate(20.0F, 1.0F, 0.0F, 0.0F);
                rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float f8 = 0.375F;
                scale(-f8, -f8, f8);
            }

            Minecraft.getMinecraft().getItemRenderer().renderItem(entity, drop, TransformType.THIRD_PERSON);

            if (pony.getModel().metadata.getRace().hasHorn() && pony.getModel().metadata.getGlowColor() != 0) {
                this.renderItemGlow(entity, drop, pony.getModel().metadata.getGlowColor());
            }

            popMatrix();
        }
    }

    public void renderItemGlow(EntityLivingBase entity, ItemStack drop, int glowColor) {
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);

        float red = (glowColor >> 16 & 255) / 255.0F;
        float green = (glowColor >> 8 & 255) / 255.0F;
        float blue = (glowColor & 255) / 255.0F;
        float alpha = 0.2F;
        // disableLighting();
        enableBlend();
        blendFunc(GL11.GL_CONSTANT_COLOR, 1);
        GL14.glBlendColor(red, green, blue, alpha);
        IBakedModel model = getItemModel(Minecraft.getMinecraft().getRenderItem(), entity, drop);

        scale(2, 2, 2);

        model.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.THIRD_PERSON);
        applyTransform(model.getItemCameraTransforms(), TransformType.THIRD_PERSON);
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        scale(1.1, 1.1, 1.1);
        if (model.isGui3d()) {
            // disabling textures for items messes up bounds
            disableTexture2D();
        }
        translate(0, .01, .01);
        renderItem.renderItem(drop, model);
        translate(.01, -.01, -.02);
        // scale(1.1, 1.1, 1.1);
        renderItem.renderItem(drop, model);
        disableBlend();
        enableLighting();
        enableTexture2D();
        popAttrib();

        // I hate rendering
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

    private void applyTransform(ItemCameraTransforms camera, TransformType type) {
        camera.applyTransform(type);
    }
}
