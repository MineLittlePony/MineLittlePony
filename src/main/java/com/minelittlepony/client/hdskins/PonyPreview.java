package com.minelittlepony.client.hdskins;

import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.dummy.PlayerPreview;
import com.minelittlepony.hdskins.profile.SkinType;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;

class PonyPreview extends PlayerPreview {

    public static final Identifier NO_SKIN_STEVE_PONY = new Identifier("minelittlepony", "textures/mob/noskin.png");
    public static final Identifier NO_SKIN_ALEX_PONY = new Identifier("minelittlepony", "textures/mob/noskin_alex.png");
    public static final Identifier NO_SKIN_SEAPONY = new Identifier("minelittlepony", "textures/mob/noskin_seapony.png");

    private final DummyPony localPony = new DummyPony(DummyPony.TYPE, localTextures);
    private final DummyPony remotePony = new DummyPony(DummyPony.TYPE, remoteTextures);

    public void setWet(boolean isWet) {
        localPony.setWet(isWet);
        remotePony.setWet(isWet);
    }

    @Override
    public Identifier getBlankSteveSkin(SkinType type) {
        if (type == SkinType.SKIN) {
            // Initialization order means this method might be called before class members have been initialized.
            // This is something that needs to be fixed in HDSkins
            return localPony != null && localPony.wet ? NO_SKIN_SEAPONY : NO_SKIN_STEVE_PONY;
        }
        return super.getBlankSteveSkin(type);
    }

    @Override
    public Identifier getBlankAlexSkin(SkinType type) {
        if (type == SkinType.SKIN) {
            return localPony != null && localPony.wet ? NO_SKIN_SEAPONY : NO_SKIN_ALEX_PONY;
        }
        return getBlankSteveSkin(type);
    }


    protected DummyPlayer ponify(DummyPlayer human, DummyPlayer pony) {
        Identifier loc = human.getTextures().get(SkinType.SKIN).getId();

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
