package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.hdskins.gui.EntityPlayerModel;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.util.Identifier;

/**
 * Dummy model used for the skin uploading screen.
 */
public class EntityPonyModel extends EntityPlayerModel {

    public static final Identifier NO_SKIN_PONY = new Identifier("minelittlepony", "textures/mob/noskin.png");
    public static final Identifier NO_SKIN_SEAPONY = new Identifier("minelittlepony", "textures/mob/noskin_seapony.png");

    public boolean wet = false;

    public EntityPonyModel(GameProfile profile) {
        super(profile);
    }

    @Override
    public Identifier getBlankSkin(Type type) {
        if (type == Type.SKIN) {
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
