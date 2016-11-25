package com.voxelmodpack.hdskins.gui;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.DynamicTextureImage;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.ImageBufferDownloadHD;
import com.voxelmodpack.hdskins.PreviewTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EntityPlayerModel extends EntityLivingBase {
    public static final ResourceLocation NOSKIN = new ResourceLocation("hdskins", "textures/mob/noskin.png");
    private PreviewTexture remoteSkinTexture;
    private ResourceLocation remoteSkinResource;
    private ResourceLocation localSkinResource;
    private DynamicTexture localSkinTexture;
    private TextureManager textureManager;
    public final GameProfile profile;
    public boolean isSwinging = false;
    protected boolean remoteSkin = false;
    protected boolean hasLocalTexture = false;

    public EntityPlayerModel(GameProfile profile) {
        super(null);
        this.profile = profile;
        this.textureManager = Minecraft.getMinecraft().getTextureManager();
        this.remoteSkinResource = new ResourceLocation("skins/preview_" + this.profile.getName() + ".png");
        this.localSkinResource = NOSKIN;
        this.textureManager.deleteTexture(this.remoteSkinResource);
    }

    public void reloadRemoteSkin() {
        this.remoteSkin = true;
        if (this.remoteSkinTexture != null) {
            this.textureManager.deleteTexture(this.remoteSkinResource);
        }

        this.remoteSkinTexture = HDSkinManager.getPreviewTexture(this.remoteSkinResource, this.profile);
    }

    public void setLocalSkin(File skinTextureFile) {
        if (skinTextureFile.exists()) {
            this.remoteSkin = false;
            if (this.localSkinTexture != null) {
                this.textureManager.deleteTexture(this.localSkinResource);
                this.localSkinTexture = null;
            }

            BufferedImage bufferedImage;
            try {
                BufferedImage image = ImageIO.read(skinTextureFile);
                bufferedImage = new ImageBufferDownloadHD().parseUserSkin(image);
            } catch (IOException var4) {
                this.localSkinResource = NOSKIN;
                var4.printStackTrace();
                return;
            }

            this.localSkinTexture = new DynamicTextureImage(bufferedImage);
            this.localSkinResource = this.textureManager.getDynamicTextureLocation("localSkinPreview", this.localSkinTexture);
            this.hasLocalTexture = true;
        }

    }

    public boolean isUsingLocalTexture() {
        return !this.remoteSkin && this.hasLocalTexture;
    }

    @Override
    public float getBrightness(float par1) {
        return 1.0F;
    }

    public boolean isTextureSetupComplete() {
        return (this.remoteSkin && this.remoteSkinTexture != null) && this.remoteSkinTexture.isTextureUploaded();
    }

    public void releaseTextures() {
        if (this.localSkinTexture != null) {
            this.textureManager.deleteTexture(this.localSkinResource);
            this.localSkinTexture = null;
            this.localSkinResource = NOSKIN;
            this.hasLocalTexture = false;
        }
    }

    public ResourceLocation getSkinTexture() {
        return this.remoteSkin ? (this.remoteSkinTexture != null ? this.remoteSkinResource
                : DefaultPlayerSkin.getDefaultSkin(entityUniqueID)) : this.localSkinResource;
    }

    public void swingArm() {
        if (!this.isSwinging || this.swingProgressInt >= 4 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwinging = true;
        }

    }

    public void updateModel() {
        this.prevSwingProgress = this.swingProgress;
        if (this.isSwinging) {
            ++this.swingProgressInt;
            if (this.swingProgressInt >= 8) {
                this.swingProgressInt = 0;
                this.isSwinging = false;
            }
        } else {
            this.swingProgressInt = 0;
        }

        this.swingProgress = this.swingProgressInt / 8.0F;
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return Minecraft.getMinecraft().gameSettings.mainHand;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 15728880;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return Iterables.cycle(null, null, null, null);
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {

    }

}
