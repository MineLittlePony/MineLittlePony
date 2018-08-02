package com.voxelmodpack.hdskins.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.DynamicTextureImage;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.ImageBufferDownloadHD;
import com.voxelmodpack.hdskins.PreviewTexture;
import com.voxelmodpack.hdskins.PreviewTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

@SuppressWarnings("EntityConstructor")
public class EntityPlayerModel extends EntityLivingBase {

    public static final ResourceLocation NO_SKIN = new ResourceLocation("hdskins", "textures/mob/noskin.png");
    public static final ResourceLocation NO_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    private Map<EntityEquipmentSlot, ItemStack> armors = Maps.newEnumMap(ImmutableMap.of(
            EntityEquipmentSlot.HEAD, ItemStack.EMPTY,
            EntityEquipmentSlot.CHEST, ItemStack.EMPTY,
            EntityEquipmentSlot.LEGS, ItemStack.EMPTY,
            EntityEquipmentSlot.FEET, ItemStack.EMPTY,
            EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY));

    private PreviewTexture remoteSkinTexture;
    private ResourceLocation remoteSkinResource;
    protected ResourceLocation localSkinResource;
    private DynamicTexture localSkinTexture;
    private PreviewTexture remoteElytraTexture;
    private ResourceLocation remoteElytraResource;
    private ResourceLocation localElytraResource;
    private DynamicTexture localElytraTexture;
    private TextureManager textureManager;
    public final GameProfile profile;

    protected boolean remoteSkin = false;
    protected boolean hasLocalTexture = false;
    protected boolean previewThinArms = false;

    public EntityPlayerModel(GameProfile profile) {
        super(new DummyWorld());
        this.profile = profile;
        textureManager = Minecraft.getMinecraft().getTextureManager();
        remoteSkinResource = new ResourceLocation("skins/preview_" + this.profile.getName() + ".png");
        remoteElytraResource = new ResourceLocation("elytras/preview_" + this.profile.getName() + ".png");
        localSkinResource = getBlankSkin();
        localElytraResource = getBlankElytra();
        textureManager.deleteTexture(remoteSkinResource);
        textureManager.deleteTexture(remoteElytraResource);
    }

    public void reloadRemoteSkin(SkinManager.SkinAvailableCallback listener) {
        remoteSkin = true;
        if (remoteSkinTexture != null) {
            textureManager.deleteTexture(remoteSkinResource);
        }
        if (remoteElytraTexture != null) {
            textureManager.deleteTexture(remoteElytraResource);
        }

        PreviewTextureManager ptm = HDSkinManager.getPreviewTextureManager(profile);

        remoteSkinTexture = ptm.getPreviewTexture(remoteSkinResource, Type.SKIN, getBlankSkin(), listener);
        remoteElytraTexture = ptm.getPreviewTexture(remoteElytraResource, Type.ELYTRA, getBlankElytra(), null);

    }

    public void setLocalTexture(File skinTextureFile, Type type) {
        if (skinTextureFile.exists()) {
            if (type == Type.SKIN) {
                remoteSkin = false;
                if (localSkinTexture != null) {
                    textureManager.deleteTexture(localSkinResource);
                    localSkinTexture = null;
                }

                BufferedImage bufferedImage;
                try {
                    BufferedImage image = ImageIO.read(skinTextureFile);
                    bufferedImage = new ImageBufferDownloadHD().parseUserSkin(image);
                    assert bufferedImage != null;
                } catch (IOException var4) {
                    localSkinResource = getBlankSkin();
                    var4.printStackTrace();
                    return;
                }

                localSkinTexture = new DynamicTextureImage(bufferedImage);
                localSkinResource = textureManager.getDynamicTextureLocation("localSkinPreview", localSkinTexture);
                hasLocalTexture = true;
            } else if (type == Type.ELYTRA) {
                remoteSkin = false;
                if (localElytraTexture != null) {
                    textureManager.deleteTexture(localElytraResource);
                    localElytraTexture = null;
                }

                BufferedImage bufferedImage;
                try {
                    bufferedImage = ImageIO.read(skinTextureFile);
                } catch (IOException var4) {
                    localElytraResource = getBlankElytra();
                    var4.printStackTrace();
                    return;
                }

                localElytraTexture = new DynamicTextureImage(bufferedImage);
                localElytraResource = textureManager.getDynamicTextureLocation("localElytraPreview", localElytraTexture);
                hasLocalTexture = true;
            }
        }
    }

    protected ResourceLocation getBlankSkin() {
        return NO_SKIN;
    }

    protected ResourceLocation getBlankElytra() {
        return NO_ELYTRA;
    }

    public boolean isUsingLocalTexture() {
        return !remoteSkin && hasLocalTexture;
    }

    public boolean isTextureSetupComplete() {
        return remoteSkin && remoteSkinTexture != null && remoteSkinTexture.isTextureUploaded();
    }

    public void releaseTextures() {
        if (localSkinTexture != null) {
            textureManager.deleteTexture(localSkinResource);
            localSkinTexture = null;
            localSkinResource = getBlankSkin();
            hasLocalTexture = false;
        }
        if (localElytraTexture != null) {
            textureManager.deleteTexture(localElytraResource);
            localElytraTexture = null;
            localElytraResource = getBlankElytra();
            hasLocalTexture = false;
        }
    }

    public ResourceLocation getSkinTexture() {
        return remoteSkin ? remoteSkinTexture != null ? remoteSkinResource
                : DefaultPlayerSkin.getDefaultSkin(entityUniqueID) : localSkinResource;
    }

    public ResourceLocation getElytraTexture() {
        return remoteSkin && remoteElytraTexture != null ? remoteElytraResource : localElytraResource;
    }

    public void setPreviewThinArms(boolean thinArms) {
        previewThinArms = thinArms;
    }

    public boolean usesThinSkin() {
        if (isTextureSetupComplete() && remoteSkinTexture.hasModel()) {
            return remoteSkinTexture.usesThinArms();
        }

        return previewThinArms;
    }

    @Override
    public void swingArm(EnumHand hand) {
        super.swingArm(hand);
        if (!isSwingInProgress || swingProgressInt >= 4 || swingProgressInt < 0) {
            swingProgressInt = -1;
            isSwingInProgress = true;
            swingingHand = hand;
        }

    }

    public void updateModel() {
        prevSwingProgress = swingProgress;
        if (isSwingInProgress) {
            ++swingProgressInt;
            if (swingProgressInt >= 8) {
                swingProgressInt = 0;
                isSwingInProgress = false;
            }
        } else {
            swingProgressInt = 0;
        }

        swingProgress = swingProgressInt / 8.0F;
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return Minecraft.getMinecraft().gameSettings.mainHand;
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
