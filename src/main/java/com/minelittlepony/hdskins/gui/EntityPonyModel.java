package com.minelittlepony.hdskins.gui;

import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;

import net.minecraft.util.ResourceLocation;

/**
 * Dummy model used for the skin uploading screen.
 */
public class EntityPonyModel extends EntityPlayerModel {

    public static final ResourceLocation NO_SKIN_PONY = new ResourceLocation("minelittlepony", "textures/mob/noskin.png");

    public EntityPonyModel(GameProfile profile) {
        super(profile);
    }

    @Override
    protected ResourceLocation getBlankSkin() {
        return NO_SKIN_PONY;
    }
}
