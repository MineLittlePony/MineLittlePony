package com.voxelmodpack.hdskins.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.SkinUploader;
import com.voxelmodpack.hdskins.resources.LocalTexture;
import com.voxelmodpack.hdskins.resources.LocalTexture.IBlankSkinSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("EntityConstructor")
public class EntityPlayerModel extends EntityLivingBase implements IBlankSkinSupplier {

    public static final ResourceLocation NO_SKIN = new ResourceLocation("hdskins", "textures/mob/noskin.png");
    public static final ResourceLocation NO_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    private final Map<EntityEquipmentSlot, ItemStack> armour = Maps.newEnumMap(ImmutableMap.of(
            EntityEquipmentSlot.HEAD, ItemStack.EMPTY,
            EntityEquipmentSlot.CHEST, ItemStack.EMPTY,
            EntityEquipmentSlot.LEGS, ItemStack.EMPTY,
            EntityEquipmentSlot.FEET, ItemStack.EMPTY,
            EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY
    ));

    protected final LocalTexture skin;
    protected final LocalTexture elytra;


    private final GameProfile profile;

    protected boolean previewThinArms = false;
    protected boolean previewSleeping = false;
    protected boolean previewRiding = false;

    public EntityPlayerModel(GameProfile gameprofile) {
        super(DummyWorld.INSTANCE);

        profile = gameprofile;

        skin = new LocalTexture(profile, Type.SKIN, this);
        elytra = new LocalTexture(profile, Type.ELYTRA, this);
    }

    public CompletableFuture<Void> reloadRemoteSkin(SkinUploader uploader, SkinManager.SkinAvailableCallback listener) {
        return uploader.loadTextures(profile).thenAcceptAsync(ptm -> {
            skin.setRemote(ptm, listener);
            elytra.setRemote(ptm, listener);
        }, Minecraft.getMinecraft()::addScheduledTask); // run on main thread
    }

    public void setLocalTexture(File skinTextureFile, Type type) {
        if (type == Type.SKIN) {
            skin.setLocal(skinTextureFile);
        } else if (type == Type.ELYTRA) {
            elytra.setLocal(skinTextureFile);
        }
    }

    @Override
    public ResourceLocation getBlankSkin(Type type) {
        return type == Type.SKIN ? NO_SKIN : NO_ELYTRA;
    }

    public boolean isUsingLocalTexture() {
        return skin.usingLocal() || elytra.usingLocal();
    }

    public boolean isTextureSetupComplete() {
        return skin.uploadComplete() && elytra.uploadComplete();
    }

    public boolean isUsingRemoteTexture() {
        return skin.hasRemoteTexture() || elytra.hasRemoteTexture();
    }

    public void releaseTextures() {
        skin.clearLocal();
        elytra.clearLocal();
    }

    public LocalTexture getLocal(Type type) {
        return type == Type.SKIN ? skin : elytra;
    }

    public void setPreviewThinArms(boolean thinArms) {
        previewThinArms = thinArms;
    }

    public boolean usesThinSkin() {
        if (skin.uploadComplete() && skin.getRemote().hasModel()) {
            return skin.getRemote().usesThinArms();
        }

        return previewThinArms;
    }

    public void setSleeping(boolean sleep) {
        previewSleeping = sleep;
    }

    public void setRiding(boolean ride) {
        previewRiding = ride;
    }

    @Override
    public boolean isRiding() {
        return previewRiding;
    }

    @Override
    public boolean isPlayerSleeping() {
        return !previewRiding && previewSleeping;
    }

    @Override
    public boolean isSneaking() {
        return !previewRiding && !previewSleeping && super.isSneaking();
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

        swingProgress = swingProgressInt / 8F;

        motionY *= 0.98;
        if (Math.abs(motionY) < 0.003) {
            motionY = 0;
        }

        if (posY == 0 && isJumping && !previewSleeping && !previewRiding) {
            jump();
        }


        motionY -= 0.08D;
        motionY *= 0.9800000190734863D;

        posY += motionY;

        if (posY < 0) {
            posY = 0;
        }
        onGround = posY == 0;

        ticksExisted++;
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return Minecraft.getMinecraft().gameSettings.mainHand;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return armour.values();
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return armour.get(slotIn);
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        armour.put(slotIn, stack);
    }
}
