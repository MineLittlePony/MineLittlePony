package com.minelittlepony.settings;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.settings.ValueConfig.Setting;

import java.util.function.Function;

public enum PonySettings implements Setting {
    SIZES(config -> config.sizes),
    SNUZZLES(config -> config.snuzzles),
    HD(config -> config.hd),
    SHOWSCALE(config -> config.showscale),
    FPSMAGIC(config -> config.fpsmagic),
    PONYSKULLS(config -> config.ponyskulls);

    private final Value<Boolean> setting;

    PonySettings(Function<PonyConfig, Value<Boolean>> func) {
        setting = func.apply(MineLittlePony.getConfig());
    }

    public Value<Boolean> setting() {
        return setting;
    }
}
