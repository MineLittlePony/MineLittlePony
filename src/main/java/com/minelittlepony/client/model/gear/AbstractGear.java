package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.Model;

import com.minelittlepony.model.gear.IGear;

public abstract class AbstractGear extends Model implements IGear {

    public AbstractGear() {
        textureWidth = 64;
        textureHeight = 64;

        init(0, 0);
    }
}
