package com.minelittlepony.hdskins.gui;

import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;

import net.minecraft.util.ResourceLocation;

/**
 * Dummy model used for the skin uploading screen.
 */
public class EntityPonyModel extends EntityPlayerModel {

    public static final ResourceLocation NO_SKIN_PONY = new ResourceLocation("minelittlepony", "textures/mob/noskin.png");
    public static final ResourceLocation NO_SKIN_SEAPONY = new ResourceLocation("minelittlepony", "textures/mob/noskin_seapony.png");

    public boolean wet = false;

    public EntityPonyModel(GameProfile profile) {
        super(profile);
    }

    @Override
    protected ResourceLocation getBlankSkin() {
        return wet ? NO_SKIN_SEAPONY : NO_SKIN_PONY;
    }

    public void setWet(boolean wet) {
        this.wet = wet;

        if (wet && localSkinResource == NO_SKIN_PONY) {
            localSkinResource = NO_SKIN_SEAPONY;
        }

        if (!wet && localSkinResource == NO_SKIN_SEAPONY) {
            localSkinResource = NO_SKIN_PONY;
        }
    }
}
