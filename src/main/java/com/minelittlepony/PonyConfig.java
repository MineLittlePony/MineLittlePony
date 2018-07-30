package com.minelittlepony;

import com.google.gson.annotations.Expose;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.settings.ValueConfig;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

/**
 * Storage container for MineLP client settings.
 */
@ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
public class PonyConfig extends ValueConfig {

    @Expose
    private PonyLevel ponylevel = PonyLevel.PONIES;

    public enum PonySettings implements ValueConfig.Flag {
        SIZES,
        SNUZZLES,
        HD,
        SHOWSCALE,
        FPSMAGIC,
        PONYSKULLS;

        @Override
        public ValueConfig config() {
            return MineLittlePony.getConfig();
        }
    }

    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    public PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : getPonyLevel();
    }

    public PonyLevel getPonyLevel() {
        if (ponylevel == null) {
            ponylevel = PonyLevel.PONIES;
        }
        return ponylevel;
    }

    /**
     * Sets the pony level. Want MOAR PONEHS? Well here you go.
     *
     * @param ponylevel
     */
    public void setPonyLevel(PonyLevel newlevel) {
        ponylevel = newlevel;
        write();
    }
}
