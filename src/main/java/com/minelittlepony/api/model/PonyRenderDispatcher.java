package com.minelittlepony.api.model;

import net.minecraft.client.model.Model;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;

public interface PonyRenderDispatcher {
    static PonyRenderDispatcher getInstance() {
        return MineLittlePony.getInstance().getRenderDispatcher();
    }

    <T extends Model<?> & PonyModel<?>> PlayerModelKey<T> getPlayerModel(Race race);
}
