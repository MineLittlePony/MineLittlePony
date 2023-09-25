package com.minelittlepony.client.compat.hdskins;

import net.minecraft.client.world.ClientWorld;

import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.PreviewModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.hdskins.client.gui.player.*;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins;

import java.util.UUID;

/**
 * Dummy model used for the skin uploading screen.
 */
class DummyPony extends DummyPlayer implements PreviewModel, PonyManager.ForcedPony, EquineRenderManager.RegistrationHandler {

    public DummyPony(ClientWorld world, PlayerSkins<?> textures) {
        super(world, textures);
        setUuid(UUID.randomUUID()); // uuid must be random so animations aren't linked between the two previews
    }

    @Override
    public boolean shouldUpdateRegistration(Pony pony) {
        return false;
    }

    @Override
    public boolean forceSeapony() {
        return getTextures().getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType;
    }
}
