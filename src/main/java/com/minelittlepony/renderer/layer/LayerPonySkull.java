package com.minelittlepony.renderer.layer;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.model.pony.ModelPlayerPony;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerPonySkull implements LayerRenderer<EntityLivingBase> {

    private RenderLivingBase<? extends EntityLivingBase> renderer;

    public LayerPonySkull(RenderLivingBase<? extends EntityLivingBase> renderPony) {
        this.renderer = renderPony;
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entity, float limbSwing, float p_177141_3_,
                              float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (itemstack != null) {
            AbstractPonyModel model = getModel().getModel();
            Item item = itemstack.getItem();

            pushMatrix();
            boolean isVillager = entity instanceof EntityVillager || (entity instanceof EntityZombie && ((EntityZombie) entity).isVillager());

            model.transform(BodyPart.HEAD);
            model.bipedHead.postRender(0.0625f);
            if (model instanceof ModelPlayerPony) {
                translate(0, .2, 0);
            }
            color(1, 1, 1, 1);
            if (item instanceof ItemBlock) {
                renderBlock(entity, itemstack);
            } else if (item == Items.SKULL) {
                if (model instanceof ModelPlayerPony) {
                    translate(0, 0, -.15);
                }
                renderSkull(itemstack, isVillager, limbSwing);
            }
            popMatrix();
        }

    }

    private void renderBlock(EntityLivingBase entity, ItemStack itemstack) {
        // translate(0, -0.25, 0);
        rotate(180, 0, 1, 0);
        scale(0.625, -0.625, -0.625);
        translate(0, 0.4, -0.21);

        Minecraft.getMinecraft().getItemRenderer().renderItem(entity, itemstack, TransformType.HEAD);
    }

    private void renderSkull(ItemStack itemstack, boolean isVillager, float limbSwing) {
        float f = 1.1875f;
        scale(f, -f, -f);
        if (isVillager) {
            translate(0, 0.0625, 0);
        }
        translate(0, 0, -.05f);
        GameProfile profile = null;

        if (itemstack.hasTagCompound()) {
            NBTTagCompound nbt = itemstack.getTagCompound();

            assert nbt != null;
            if (nbt.hasKey("SkullOwner", 10)) {
                profile = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("SkullOwner"));
            } else if (nbt.hasKey("SkullOwner", 8)) {
                profile = TileEntitySkull.updateGameprofile(new GameProfile(null, nbt.getString("SkullOwner")));
                nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), profile));
            }
        }

        TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.45F, EnumFacing.UP, 180.0F, itemstack.getMetadata(), profile, -1, limbSwing);

    }

    private PlayerModel getModel() {
        return ((IRenderPony) renderer).getPony();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

}
