package com.minelittlepony.settings;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.util.settings.Config;
import com.minelittlepony.common.util.settings.Setting;

/**
 * Mod settings.
 */
public enum PonySettings implements Setting<Boolean> {
    SIZES,
    SNUZZLES,
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
