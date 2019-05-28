package com.minelittlepony.client.gui.hdskins;

import net.minecraft.util.Identifier;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.pony.Pony;
import com.minelittlepony.hdskins.gui.EntityPlayerModel;
import com.minelittlepony.hdskins.gui.PlayerPreview;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

public class PonyPreview extends PlayerPreview {

    private final EntityPonyModel localPlayer = new EntityPonyModel(minecraft.getSession().getProfile());
    private final EntityPonyModel remotePlayer = new EntityPonyModel(minecraft.getSession().getProfile());

    public void setWet(boolean isWet) {
        ((EntityPonyModel)localPlayer).setWet(isWet);
        ((EntityPonyModel)remotePlayer).setWet(isWet);
    }

    protected EntityPlayerModel ponify(EntityPlayerModel entity, EntityPlayerModel pony) {
        Identifier loc = entity.getTexture(Type.SKIN).getTexture();
        if (loc == null || Pony.getBufferedImage(loc) == null) {
            return entity;
        }

        IPony thePony = MineLittlePony.getInstance().getManager().getPony(loc);

        Race race = thePony.getRace(false);

        if (race.isHuman()) {
            return entity;
        }

        return pony;
    }

    @Override
    public EntityPlayerModel getRemote() {
        return ponify(super.getRemote(), remotePlayer);
    }


    @Override
    public EntityPlayerModel getLocal() {
        return ponify(super.getLocal(), localPlayer);
    }
}
