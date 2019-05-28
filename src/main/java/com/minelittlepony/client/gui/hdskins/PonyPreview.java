package com.minelittlepony.client.gui.hdskins;

import net.minecraft.util.Identifier;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.pony.Pony;
import com.minelittlepony.hdskins.dummy.DummyPlayer;
import com.minelittlepony.hdskins.dummy.PlayerPreview;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

public class PonyPreview extends PlayerPreview {

    public static final Identifier NO_SKIN_PONY = new Identifier("minelittlepony", "textures/mob/noskin.png");
    public static final Identifier NO_SKIN_SEAPONY = new Identifier("minelittlepony", "textures/mob/noskin_seapony.png");

    private final DummyPony localPlayer = new DummyPony(localTextures);
    private final DummyPony remotePlayer = new DummyPony(remoteTextures);

    public void setWet(boolean isWet) {
        localPlayer.setWet(isWet);
        remotePlayer.setWet(isWet);
    }

    @Override
    public Identifier getBlankSkin(Type type) {
        if (type == Type.SKIN) {
            return localPlayer.wet ? NO_SKIN_SEAPONY : NO_SKIN_PONY;
        }
        return super.getBlankSkin(type);
    }

    protected DummyPlayer ponify(DummyPlayer human, DummyPlayer pony) {
        Identifier loc = human.getTextures().get(Type.SKIN).getId();

        if (loc == null || Pony.getBufferedImage(loc) == null) {
            return pony;
        }

        IPony thePony = MineLittlePony.getInstance().getManager().getPony(loc);

        Race race = thePony.getRace(false);

        if (race.isHuman()) {
            return human;
        }

        return pony;
    }

    @Override
    public DummyPlayer getRemote() {
        return ponify(super.getRemote(), remotePlayer);
    }


    @Override
    public DummyPlayer getLocal() {
        return ponify(super.getLocal(), localPlayer);
    }
}
