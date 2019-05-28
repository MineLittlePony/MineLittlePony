package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.hdskins.dummy.DummyPlayer;
import com.minelittlepony.hdskins.dummy.TextureProxy;
import com.minelittlepony.hdskins.resources.LocalTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

/**
 * Dummy model used for the skin uploading screen.
 */
public class DummyPony extends DummyPlayer {

    public boolean wet = false;

    public DummyPony(TextureProxy textures) {
        super(textures);
    }

    public void setWet(boolean wet) {
        this.wet = wet;

        LocalTexture skin = getTextures().get(Type.SKIN);

        if (wet && skin.getId() == PonyPreview.NO_SKIN_PONY) {
            skin.reset();
        }

        if (!wet && skin.getId() == PonyPreview.NO_SKIN_SEAPONY) {
            skin.reset();
        }
    }
}
