package com.minelittlepony.minelp.model;

import static net.minecraft.client.renderer.GlStateManager.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.minelittlepony.minelp.PonyManager;
import com.minelittlepony.minelp.renderer.AniParams;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public abstract class ModelPony extends ModelPlayer {
    public String texture;
    protected float strech = 0.0F;
    protected float scale = 0.0625F;
    public boolean issneak = false;
    public boolean isArmour = false;
    public int glowColor = -12303190;
    public final float pi = 3.141593F;
    public boolean isPegasus;
    public boolean isUnicorn;
    public boolean isMale;
    public int wantTail;
    public int size;
    public boolean isVillager;
    public int villagerProfession;
    public boolean isFlying;
    public boolean isGlow;
    public boolean isSleeping;
    public int heldItemLeft;
    public int heldItemRight;
    public boolean aimedBow;

    public ModelPony(String texture) {
        super(0, false);
        this.texture = texture;
    }

    public void setStrech(float strech) {
        this.strech = strech;
    }

    public final void init() {
        init(0);
    }

    public final void init(float var1) {
        init(var1, 0);
    }

    public abstract void init(float var1, float var2);

    public void animate(AniParams var1) {};

    public void render(AniParams var1) {};

    @Override
    public void render(Entity player, float Move, float Moveswing, float Loop, float Right, float Down, float Scale) {
        PonyManager.getInstance();
        if (player instanceof AbstractClientPlayer) {
            setModelVisibilities((AbstractClientPlayer) player);
        }
        if (!doCancelRender()) {
            AniParams ani = new AniParams(Move, Moveswing, Loop, Right, Down);
            this.animate(ani);
            this.render(ani);
        } else {
            super.render(player, Move, Moveswing, Loop, Right, Down, Scale);
        }
    }

    protected void setModelVisibilities(AbstractClientPlayer clientPlayer) {
        ModelPlayer modelplayer = this;

        if (clientPlayer.isSpectator()) {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        } else {
            ItemStack itemstack = clientPlayer.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = clientPlayer.isSneaking();

            if (itemstack == null) {
                modelplayer.heldItemRight = 0;
            } else {
                modelplayer.heldItemRight = 1;

                if (clientPlayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3;
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    public void renderDrop(RenderManager rendermanager, ItemRenderer itemrenderer, EntityLivingBase entity) {}

    protected void renderDrop(ItemRenderer itemrenderer, EntityLivingBase entity, ModelRenderer box,
            float scalefactor, float posx, float posy, float posz) {
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
            itemrenderer.renderItem(entity, drop, TransformType.THIRD_PERSON);

            if (isUnicorn && glowColor != 0) {
                this.renderItemGlow(itemrenderer, entity, drop);
            }

            popMatrix();
        }
    }

    public void renderItemGlow(ItemRenderer itemRenderer, EntityLivingBase entity, ItemStack drop) {
        // FIXME doesn't blend
        pushMatrix();
        GL11.glPushAttrib(24577);
        GL11.glDisable(2896);
        float red = (glowColor >> 16 & 255) / 255.0F;
        float green = (glowColor >> 8 & 255) / 255.0F;
        float blue = (glowColor & 255) / 255.0F;
        float alpha = 0.2F;
        enableBlend();
        GL11.glEnable(3042);
        GL14.glBlendColor(red, green, blue, alpha);
        blendFunc('\u8001', 1);
        color(red, green, blue, alpha);
        // translate(1.1F, 1.1F, 1.1F);
        if (!(drop.getItem() instanceof ItemBlock) || !drop.getItem().isFull3D()) {
            translate(0.02F, -0.06F, -0.02F);
        }
        // itemRenderer.renderItem(entity, drop, TransformType.THIRD_PERSON);
        popAttrib();
        popMatrix();
    }

    public void renderEars(EntityLivingBase entity, float par2) {}

    public void renderCloak(EntityPlayer player, float par2) {}

    public void renderStaticCloak(EntityLiving player, float par2) {}

    protected boolean doCancelRender() {
        return false;
    }

}
