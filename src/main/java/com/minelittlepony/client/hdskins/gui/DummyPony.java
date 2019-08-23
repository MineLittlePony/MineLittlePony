package com.minelittlepony.client.hdskins.gui;

import com.minelittlepony.hdskins.dummy.DummyPlayer;
import com.minelittlepony.hdskins.dummy.TextureProxy;
import com.minelittlepony.hdskins.profile.SkinType;
import com.minelittlepony.hdskins.resources.LocalTexture;

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

        LocalTexture skin = getTextures().get(SkinType.SKIN);

        if (wet && skin.getId() == PonyPreview.NO_SKIN_PONY) {
            skin.reset();
        }

        if (!wet && skin.getId() == PonyPreview.NO_SKIN_SEAPONY) {
            skin.reset();
        }
    }
}
