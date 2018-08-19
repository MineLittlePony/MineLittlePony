package com.minelittlepony.hdskins.gui;

import com.minelittlepony.avatar.texture.TextureType;
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
    public ResourceLocation getBlankSkin(TextureType type) {
        if (type == TextureType.SKIN) {
            return wet ? NO_SKIN_SEAPONY : NO_SKIN_PONY;
        }
        return super.getBlankSkin(type);
    }

    public void setWet(boolean wet) {
        this.wet = wet;

        if (wet && skin.getTexture() == NO_SKIN_PONY) {
            skin.reset();
        }

        if (!wet && skin.getTexture() == NO_SKIN_SEAPONY) {
            skin.reset();
        }
    }
}
