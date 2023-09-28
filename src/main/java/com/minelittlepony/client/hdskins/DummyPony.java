package com.minelittlepony.client.hdskins;

import net.minecraft.client.world.ClientWorld;

import com.minelittlepony.api.model.PreviewModel;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.IPreviewModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.hdskins.client.VanillaModels;
import com.minelittlepony.hdskins.client.gui.player.*;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins;

import java.util.UUID;

/**
 * Dummy model used for the skin uploading screen.
 */
class DummyPony extends DummyPlayer implements PreviewModel, IPonyManager.ForcedPony {

    public DummyPony(ClientWorld world, PlayerSkins<?> textures) {
        super(world, textures);
        setUuid(UUID.randomUUID()); // uuid must be random so animations aren't linked between the two previews
    }

    @Override
    public boolean isSubmergedInWater() {
        return getTextures().getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType || super.isSubmergedInWater();
    }

    @Override
    public String getModel() {
        if (getTextures().getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType) {
            return VanillaModels.isSlim(getTextures().getSkinVariant()) ? "slimseapony" : "seapony";
        }
        return IPony.getManager()
                .getPony(this)
                .metadata()
                .getRace()
                .getModelId(VanillaModels.isSlim(getTextures().getSkinVariant()));
    }
}
