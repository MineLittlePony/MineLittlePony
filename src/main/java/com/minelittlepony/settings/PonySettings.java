package com.minelittlepony.settings;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.settings.SensibleConfig.Setting;

public enum PonySettings implements Setting {
    SIZES,
    SNUZZLES,
    HD,
    SHOWSCALE,
    FPSMAGIC,
    PONYSKULLS,
    FRUSTRUM;

    @Override
    public SensibleConfig config() {
        return MineLittlePony.getInstance().getConfig();
    }
}
