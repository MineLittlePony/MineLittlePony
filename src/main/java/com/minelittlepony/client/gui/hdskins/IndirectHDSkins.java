package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.client.MineLPClient;
import com.minelittlepony.common.client.IModUtilities;

import java.util.function.Function;

public final class IndirectHDSkins {
    public static Function<IModUtilities, MineLPClient> getConstructor() {
        return MineLPHDSkins::new;
    }
}
