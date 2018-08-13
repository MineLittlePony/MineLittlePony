package com.minelittlepony.render.layer;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.render.PonySkullRenderer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerPonyCustomHead<T extends EntityLivingBase> implements LayerRenderer<T> {

    private RenderLivingBase<T> renderer;

    public LayerPonyCustomHead(RenderLivingBase<T> renderPony) {
        renderer = renderPony;
    }

    @Override
    public void doRenderLayer(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!itemstack.isEmpty()) {
            AbstractPonyModel model = getModel().getBody();
            Item item = itemstack.getItem();

            pushMatrix();

            model.transform(BodyPart.HEAD);
            model.bipedHead.postRender(0.0625f);

            if (model instanceof AbstractPonyModel) {
                translate(0, 0.2F, 0);
            } else {
                translate(0, 0, 0.15F);
            }

            color(1, 1, 1, 1);

            if (item == Items.SKULL) {
                boolean isVillager = entity instanceof EntityVillager || entity instanceof EntityZombieVillager;

                renderSkull(itemstack, isVillager, move);
            } else if (!(item instanceof ItemArmor) || ((ItemArmor)item).getEquipmentSlot() != EntityEquipmentSlot.HEAD) {
                renderBlock(entity, itemstack);
            }

            popMatrix();
        }

    }

    private void renderBlock(T entity, ItemStack itemstack) {
        rotate(180, 0, 1, 0);
        scale(0.625, -0.625F, -0.625F);
        translate(0, 0.4F, -0.21F);

        Minecraft.getMinecraft().getItemRenderer().renderItem(entity, itemstack, TransformType.HEAD);
    }

    private void renderSkull(ItemStack itemstack, boolean isVillager, float limbSwing) {
        translate(0, 0, -0.14F);
        float f = 1.1875f;
        scale(f, -f, -f);
        if (isVillager) {
            translate(0, 0.0625F, 0);
        }
        translate(0, 0, -0.05F);
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

        PonySkullRenderer.resolve().renderSkull(-0.5F, 0, -0.45F, EnumFacing.UP, 180, itemstack.getMetadata(), profile, -1, limbSwing);
    }

    private ModelWrapper getModel() {
        return ((IRenderPony<?>) renderer).getModelWrapper();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

}
