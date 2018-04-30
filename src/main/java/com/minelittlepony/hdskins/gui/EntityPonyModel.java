package com.minelittlepony.hdskins.gui;

import java.io.File;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;

import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

/**
 * Dummy model used for the skin uploading screen.
 */
public class EntityPonyModel extends EntityPlayerModel {

    public static final ResourceLocation NO_SKIN_PONY = new ResourceLocation("minelittlepony", "textures/mob/noskin.png");

    public EntityPonyModel(GameProfile profile) {
        super(profile);
    }

    public void setLocalTexture(File skinTextureFile, Type type) {
        super.setLocalTexture(skinTextureFile, type);
    }

    public ResourceLocation getSkinTexture() {
        ResourceLocation skin = super.getSkinTexture();
        if (skin == NO_SKIN) {
            // We're a pony, might as well look like one.
            return NO_SKIN_PONY;
        }

        return skin;
    }

    public void swingArm() {
        super.swingArm();

        // Fixes the preview model swinging the wrong arm.
        // Who's maintaining HDSkins anyway?
        this.swingingHand = EnumHand.MAIN_HAND;
    }
}
