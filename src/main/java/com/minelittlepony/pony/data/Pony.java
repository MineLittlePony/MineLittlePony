package com.minelittlepony.pony.data;

import com.google.common.base.MoreObjects;
import com.minelittlepony.MineLittlePony;
import com.minelittlepony.mixin.MixinThreadDownloadImageData;
import com.minelittlepony.model.ModelWrapper;
import com.voxelmodpack.hdskins.DynamicTextureImage;
import com.voxelmodpack.hdskins.ThreadDownloadImageETag;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Pony {

    private static final AtomicInteger ponyCount = new AtomicInteger();

    private final int ponyId = ponyCount.getAndIncrement();

    private final ResourceLocation texture;
    private final IPonyData metadata;

    private final boolean smallArms;

    public Pony(ResourceLocation resource, boolean slim) {
        texture = resource;
        metadata = checkSkin(texture);
        smallArms = slim;
    }

    private IPonyData checkSkin(ResourceLocation resource) {
        IPonyData data = checkPonyMeta(resource);
        if (data != null) {
            return data;
        }

        BufferedImage skinImage = getBufferedImage(resource);
        return this.checkSkin(skinImage);
    }

    @Nullable
    private IPonyData checkPonyMeta(ResourceLocation resource) {
        try {
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
            if (res.hasMetadata()) {
                PonyData data = res.getMetadata(PonyDataSerialzier.NAME);
                if (data != null) {
                    return data;
                }
            }
        } catch (FileNotFoundException e) {
            // Ignore uploaded texture
        } catch (IOException e) {
            MineLittlePony.logger.warn("Unable to read {} metadata", resource, e);
        }
        return null;
    }

    @Nullable
    private BufferedImage getBufferedImage(@Nonnull ResourceLocation resource) {
        try {
            IResource skin = Minecraft.getMinecraft().getResourceManager().getResource(resource);
            BufferedImage skinImage = TextureUtil.readBufferedImage(skin.getInputStream());
            MineLittlePony.logger.debug("Obtained skin from resource location {}", resource);

            return skinImage;
        } catch (IOException ignored) { }

        try {
            ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(resource);

            if (texture instanceof MixinThreadDownloadImageData) {
                return ((MixinThreadDownloadImageData) texture).getBufferedImage();
            } else if (texture instanceof ThreadDownloadImageETag) {
                return ((ThreadDownloadImageETag) texture).getBufferedImage();
            } else if (texture instanceof DynamicTextureImage) {
                return ((DynamicTextureImage) texture).getImage();
            }
        } catch (Exception ignored) { }

        return null;
    }

    private IPonyData checkSkin(BufferedImage bufferedimage) {
        if (bufferedimage == null) {
            return new PonyData();
        }
        MineLittlePony.logger.debug("\tStart skin check for pony #{} with image {}.", ponyId, bufferedimage);
        return PonyData.parse(bufferedimage);
    }

    public boolean isPegasusFlying(EntityLivingBase entity) {
        return getRace(false).hasWings() &&
                !(entity.onGround || entity.isRiding() || entity.isOnLadder() || entity.isInWater());
    }

    public boolean isSwimming(EntityLivingBase entity) {
        return isFullySubmerged(entity) && !(entity.onGround || entity.isOnLadder());
    }

    public boolean isFullySubmerged(EntityLivingBase entity) {
        return entity.isInWater() && entity.getEntityWorld().getBlockState(new BlockPos(getVisualEyePosition(entity))).getMaterial() == Material.WATER;
    }

    protected Vec3d getVisualEyePosition(EntityLivingBase entity) {
        PonySize size = entity.isChild() ? PonySize.FOAL : metadata.getSize();

        return new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight() * size.getScaleFactor(), entity.posZ);
    }

    public boolean isWearingHeadgear(EntityLivingBase entity) {
        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        return !(item instanceof ItemArmor) || ((ItemArmor)item).getEquipmentSlot() != EntityEquipmentSlot.HEAD;
    }

    public ModelWrapper getModel(boolean ignorePony) {
        return getRace(ignorePony).getModel().getModel(smallArms);
    }

    public PonyRace getRace(boolean ignorePony) {
        return metadata.getRace().getEffectiveRace(MineLittlePony.getConfig().getEffectivePonyLevel(ignorePony));
    }

    public boolean usesThinArms() {
        return smallArms;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public IPonyData getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("texture", texture)
                .add("metadata", metadata)
                .toString();
    }
}
