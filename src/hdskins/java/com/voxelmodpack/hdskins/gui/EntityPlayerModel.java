package com.voxelmodpack.hdskins.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
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
import java.util.Map;

@SuppressWarnings("EntityConstructor")
public class EntityPlayerModel extends EntityLivingBase {

    public static final ResourceLocation NO_SKIN = new ResourceLocation("hdskins", "textures/mob/noskin.png");
    public static final ResourceLocation NO_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    private Map<EntityEquipmentSlot, ItemStack> armors = Maps.newEnumMap(ImmutableMap.of(
            EntityEquipmentSlot.HEAD, ItemStack.EMPTY,
            EntityEquipmentSlot.CHEST, ItemStack.EMPTY,
            EntityEquipmentSlot.LEGS, ItemStack.EMPTY,
            EntityEquipmentSlot.FEET, ItemStack.EMPTY,
            EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY
    ));

    private PreviewTexture remoteSkinTexture;
    private ResourceLocation remoteSkinResource;
    private ResourceLocation localSkinResource;
    private DynamicTexture localSkinTexture;
    private PreviewTexture remoteElytraTexture;
    private ResourceLocation remoteElytraResource;
    private ResourceLocation localElytraResource;
    private DynamicTexture localElytraTexture;
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
        this.remoteElytraResource = new ResourceLocation("elytras/preview_" + this.profile.getName() + ".png");
        this.localSkinResource = NO_SKIN;
        this.localElytraResource = NO_ELYTRA;
        this.textureManager.deleteTexture(this.remoteSkinResource);
        this.textureManager.deleteTexture(this.remoteElytraResource);
    }

    public void reloadRemoteSkin() {
        this.remoteSkin = true;
        if (this.remoteSkinTexture != null) {
            this.textureManager.deleteTexture(this.remoteSkinResource);
        }
        if (this.remoteElytraTexture != null) {
            this.textureManager.deleteTexture(this.remoteElytraResource);
        }

        this.remoteSkinTexture = HDSkinManager.getPreviewTexture(this.remoteSkinResource, this.profile, Type.SKIN, NO_SKIN);
        this.remoteElytraTexture = HDSkinManager.getPreviewTexture(this.remoteElytraResource, this.profile, Type.ELYTRA, NO_ELYTRA);

    }

    public void setLocalTexture(File skinTextureFile, Type type) {
        if (skinTextureFile.exists()) {
            if (type == Type.SKIN) {
                this.remoteSkin = false;
                if (this.localSkinTexture != null) {
                    this.textureManager.deleteTexture(this.localSkinResource);
                    this.localSkinTexture = null;
                }

                BufferedImage bufferedImage;
                try {
                    BufferedImage image = ImageIO.read(skinTextureFile);
                    bufferedImage = new ImageBufferDownloadHD().parseUserSkin(image);
                    assert bufferedImage != null;
                } catch (IOException var4) {
                    this.localSkinResource = NO_SKIN;
                    var4.printStackTrace();
                    return;
                }

                this.localSkinTexture = new DynamicTextureImage(bufferedImage);
                this.localSkinResource = this.textureManager.getDynamicTextureLocation("localSkinPreview", this.localSkinTexture);
                this.hasLocalTexture = true;
            } else if (type == Type.ELYTRA) {
                this.remoteSkin = false;
                if (this.localElytraTexture != null) {
                    this.textureManager.deleteTexture(this.localElytraResource);
                    this.localElytraTexture = null;
                }

                BufferedImage bufferedImage;
                try {
                    bufferedImage = ImageIO.read(skinTextureFile);
                } catch (IOException var4) {
                    this.localElytraResource = NO_ELYTRA;
                    var4.printStackTrace();
                    return;
                }

                this.localElytraTexture = new DynamicTextureImage(bufferedImage);
                this.localElytraResource = this.textureManager.getDynamicTextureLocation("localElytraPreview", this.localElytraTexture);
                this.hasLocalTexture = true;
            }
        }
    }

    public boolean isUsingLocalTexture() {
        return !this.remoteSkin && this.hasLocalTexture;
    }

//    @Override TODO
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
            this.localSkinResource = NO_SKIN;
            this.hasLocalTexture = false;
        }
        if (this.localElytraTexture != null) {
            this.textureManager.deleteTexture(this.localElytraResource);
            this.localElytraTexture = null;
            this.localElytraResource = NO_ELYTRA;
            this.hasLocalTexture = false;
        }
    }

    public ResourceLocation getSkinTexture() {
        return this.remoteSkin ? (this.remoteSkinTexture != null ? this.remoteSkinResource
                : DefaultPlayerSkin.getDefaultSkin(entityUniqueID)) : this.localSkinResource;
    }

    public ResourceLocation getElytraTexture() {
        return this.remoteSkin && this.remoteElytraTexture != null ? this.remoteElytraResource : localElytraResource;
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

//    @Override TODO
    public int getBrightnessForRender(float partialTicks) {
        return 0xf000f0;
    }


    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return armors.values();
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return armors.get(slotIn);
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        armors.put(slotIn, stack);
    }

}
