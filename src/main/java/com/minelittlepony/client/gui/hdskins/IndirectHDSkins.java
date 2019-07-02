package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.client.MineLPClient;

import java.util.function.Supplier;

public final class IndirectHDSkins {
    public static Supplier<MineLPClient> getConstructor() {
        return MineLPHDSkins::new;
    }
}
