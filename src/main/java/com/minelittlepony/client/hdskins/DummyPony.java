package com.minelittlepony.client.hdskins;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;

import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.dummy.TextureProxy;
import com.minelittlepony.hdskins.profile.SkinType;
import com.minelittlepony.hdskins.client.resources.LocalTexture;
import com.minelittlepony.model.IRotatedSwimmer;

/**
 * Dummy model used for the skin uploading screen.
 */
class DummyPony extends DummyPlayer implements IRotatedSwimmer {

    public static EntityType<DummyPony> TYPE = EntityType.Builder
            .<DummyPony>create((t, w) -> new DummyPony(t, null), EntityCategory.MISC)
            .disableSaving()
            .disableSummon()
            .build("minelittlepony:dummy_pony");

    public boolean wet = false;

    public DummyPony(EntityType<DummyPony> type, TextureProxy textures) {
        super(type, textures);
    }

    public void setWet(boolean wet) {
        this.wet = wet;

        LocalTexture skin = getTextures().get(SkinType.SKIN);

        if (wet != (skin.getId() == PonyPreview.NO_SKIN_SEAPONY)) {
            skin.reset();
        }
    }
}
