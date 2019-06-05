package com.minelittlepony.settings;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.settings.Config.Setting;

/**
 * Mod settings.
 */
public enum PonySettings implements Setting<Boolean> {
    SIZES,
    SNUZZLES,
    HD,
    FILLYCAM,
    SHOWSCALE,
    FPSMAGIC,
    PONYSKULLS,
    FRUSTRUM;

    @Override
    public Boolean getDefault() {
        return true;
    }

    @Override
    public Config config() {
        return MineLittlePony.getInstance().getConfig();
    }
}
