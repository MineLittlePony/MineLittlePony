package com.minelittlepony.client.hdskins;

import net.minecraft.client.world.ClientWorld;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.IPreviewModel;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.pony.Pony;
import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.dummy.TextureProxy;

/**
 * Dummy model used for the skin uploading screen.
 */
class DummyPony extends DummyPlayer implements IPreviewModel, ModelAttributes.Swimmer, IPonyManager.ForcedPony, Pony.RegistrationHandler {

    public DummyPony(ClientWorld world, TextureProxy textures) {
        super(world, textures);
    }

    @Override
    public boolean shouldUpdateRegistration(Pony pony) {
        return false;
    }

    @Override
    public boolean isSubmergedInWater() {
        return getTextures().getSkinType() == MineLPHDSkins.seaponySkinType || super.isSubmergedInWater();
    }

    @Override
    public String getModel() {
        if (getTextures().getSkinType() == MineLPHDSkins.seaponySkinType) {
            return getTextures().usesThinSkin() ? "slimseapony" : "seapony";
        }
        return MineLittlePony.getInstance().getManager()
                .getPony(this)
                .getMetadata()
                .getRace()
                .getModelId(getTextures().usesThinSkin());
    }
}
