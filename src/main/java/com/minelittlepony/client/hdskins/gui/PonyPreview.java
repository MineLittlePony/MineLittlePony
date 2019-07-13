package com.minelittlepony.client.hdskins.gui;

import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.hdskins.dummy.DummyPlayer;
import com.minelittlepony.hdskins.dummy.PlayerPreview;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

class PonyPreview extends PlayerPreview {

    public static final Identifier NO_SKIN_PONY = new Identifier("minelittlepony", "textures/mob/noskin.png");
    public static final Identifier NO_SKIN_SEAPONY = new Identifier("minelittlepony", "textures/mob/noskin_seapony.png");

    private final DummyPony localPony = new DummyPony(localTextures);
    private final DummyPony remotePony = new DummyPony(remoteTextures);

    public void setWet(boolean isWet) {
        localPony.setWet(isWet);
        remotePony.setWet(isWet);
    }

    @Override
    public Identifier getBlankSkin(Type type) {
        if (type == Type.SKIN) {
            // Initialization order means this method might be called before class members have been initialized.
            // This is something that needs to be fixed in HDSkins
            return localPony != null && localPony.wet ? NO_SKIN_SEAPONY : NO_SKIN_PONY;
        }
        return super.getBlankSkin(type);
    }

    protected DummyPlayer ponify(DummyPlayer human, DummyPlayer pony) {
        Identifier loc = human.getTextures().get(Type.SKIN).getId();

        if (loc == null) {
            return pony;
        }

        IPony thePony = MineLittlePony.getInstance().getManager().getPony(loc);

        Race race = thePony.getRace(true);

        if (race.isHuman()) {
            return human;
        }

        return pony;
    }

    @Override
    public DummyPlayer getRemote() {
        return ponify(super.getRemote(), remotePony);
    }


    @Override
    public DummyPlayer getLocal() {
        return ponify(super.getLocal(), localPony);
    }
}
