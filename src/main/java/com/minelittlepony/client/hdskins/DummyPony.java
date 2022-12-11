package com.minelittlepony.client.hdskins;

import net.minecraft.client.world.ClientWorld;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.IPreviewModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.hdskins.client.dummy.*;

/**
 * Dummy model used for the skin uploading screen.
 */
class DummyPony extends DummyPlayer implements IPreviewModel, ModelAttributes.Swimmer, IPonyManager.ForcedPony, EquineRenderManager.RegistrationHandler {

    public DummyPony(ClientWorld world, PlayerSkins<?> textures) {
        super(world, textures);
    }

    @Override
    public boolean shouldUpdateRegistration(IPony pony) {
        return false;
    }

    @Override
    public boolean isSubmergedInWater() {
        return getTextures().getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType || super.isSubmergedInWater();
    }

    @Override
    public String getModel() {
        if (getTextures().getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType) {
            return getTextures().usesThinSkin() ? "slimseapony" : "seapony";
        }
        return IPony.getManager()
                .getPony(this)
                .metadata()
                .getRace()
                .getModelId(getTextures().usesThinSkin());
    }
}
