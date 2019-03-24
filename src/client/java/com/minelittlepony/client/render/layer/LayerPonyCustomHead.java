package com.minelittlepony.client.render.layer;

import static net.minecraft.client.renderer.GlStateManager.*;

import com.minelittlepony.client.ducks.IRenderPony;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.IClientModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.model.BodyPart;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockSkull.ISkullType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class LayerPonyCustomHead<T extends EntityLivingBase> implements LayerRenderer<T> {

    private RenderLivingBase<T> renderer;

    public LayerPonyCustomHead(RenderLivingBase<T> renderPony) {
        renderer = renderPony;
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!itemstack.isEmpty()) {
            IClientModel model = getModel().getBody();
            Item item = itemstack.getItem();

            pushMatrix();

            model.transform(BodyPart.HEAD);
            model.getHead().postRender(0.0625f);

            if (model instanceof AbstractPonyModel) {
                translatef(0, 0.2F, 0);
            } else {
                translatef(0, 0, 0.15F);
            }

            color4f(1, 1, 1, 1);

            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof BlockAbstractSkull) {
                boolean isVillager = entity instanceof EntityVillager || entity instanceof EntityZombieVillager;

                renderSkull(itemstack, isVillager, move);
            } else if (!(item instanceof ItemArmor) || ((ItemArmor)item).getEquipmentSlot() != EntityEquipmentSlot.HEAD) {
                renderBlock(entity, itemstack);
            }

            popMatrix();
        }

    }

    private void renderBlock(T entity, ItemStack itemstack) {
        rotatef(180, 0, 1, 0);
        scalef(0.625F, -0.625F, -0.625F);
        translatef(0, 0.4F, -0.21F);

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entity, itemstack, TransformType.HEAD);
    }

    private void renderSkull(ItemStack itemstack, boolean isVillager, float limbSwing) {
        translatef(0, 0, -0.14F);
        float f = 1.1875f;
        scalef(f, -f, -f);
        if (isVillager) {
            translatef(0, 0.0625F, 0);
        }
        translatef(0, 0, -0.05F);
        GameProfile profile = null;

        if (itemstack.hasTag()) {
            NBTTagCompound nbt = itemstack.getTag();

            assert nbt != null;

            if (nbt.contains("SkullOwner", 10)) {
                profile = NBTUtil.readGameProfile(nbt.getCompound("SkullOwner"));
            } else if (nbt.contains("SkullOwner", 8)) {
                profile = TileEntitySkull.updateGameProfile(new GameProfile(null, nbt.getString("SkullOwner")));
                nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), profile));
            }
        }

        ISkullType type = ((BlockAbstractSkull) ((ItemBlock) itemstack.getItem()).getBlock()).getSkullType();

        PonySkullRenderer.resolve().render(-0.5F, 0, -0.45F, EnumFacing.UP, 180, type, profile, -1, limbSwing);
    }

    private ModelWrapper getModel() {
        return ((IRenderPony<?>) renderer).getModelWrapper();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

}
