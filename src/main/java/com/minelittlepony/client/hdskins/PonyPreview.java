package com.minelittlepony.client.hdskins;

import net.minecraft.util.Identifier;

import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.dummy.PlayerPreview;
import com.minelittlepony.hdskins.profile.SkinType;

class PonyPreview extends PlayerPreview {

    public static final Identifier NO_SKIN_STEVE_PONY = new Identifier("minelittlepony", "textures/mob/noskin.png");
    public static final Identifier NO_SKIN_ALEX_PONY = new Identifier("minelittlepony", "textures/mob/noskin_alex.png");
    public static final Identifier NO_SKIN_SEAPONY = new Identifier("minelittlepony", "textures/mob/noskin_seapony.png");

    private final DummyPony localPony = new DummyPony(localTextures);
    private final DummyPony remotePony = new DummyPony(remoteTextures);

    public void setWet(boolean isWet) {
        localPony.setWet(isWet);
        remotePony.setWet(isWet);
    }

    @Override
    public Identifier getBlankSteveSkin(SkinType type) {
        if (type == SkinType.SKIN) {
            return NO_SKIN_STEVE_PONY;
        }
        if (type == MineLPHDSkins.seaponySkinType) {
            return NO_SKIN_SEAPONY;
        }
        return super.getBlankSteveSkin(type);
    }

    @Override
    public Identifier getBlankAlexSkin(SkinType type) {
        if (type == SkinType.SKIN) {
            return NO_SKIN_ALEX_PONY;
        }
        if (type == MineLPHDSkins.seaponySkinType) {
            return NO_SKIN_SEAPONY;
        }
        return getBlankSteveSkin(type);
    }

    @Override
    public DummyPlayer getRemote() {
        return remotePony;
    }


    @Override
    public DummyPlayer getLocal() {
        return localPony;
    }
}
