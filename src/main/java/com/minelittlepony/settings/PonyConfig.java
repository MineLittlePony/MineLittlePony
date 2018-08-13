package com.minelittlepony.settings;

import com.google.gson.annotations.Expose;
import com.minelittlepony.pony.data.PonyLevel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

/**
 * Storage container for MineLP client settings.
 */
@ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
public class PonyConfig extends ValueConfig {

    @Expose private PonyLevel ponylevel = PonyLevel.PONIES;

    @Expose protected final Value<Boolean> sizes = Value.of(true);
    @Expose protected final Value<Boolean> snuzzles = Value.of(true);
    @Expose protected final Value<Boolean> hd = Value.of(true);
    @Expose protected final Value<Boolean> showscale = Value.of(true);
    @Expose protected final Value<Boolean> fpsmagic = Value.of(true);
    @Expose protected final Value<Boolean> ponyskulls = Value.of(true);

    @Expose protected final Value<Boolean> villagers = Value.of(true);
    @Expose protected final Value<Boolean> zombies = Value.of(true);
    @Expose protected final Value<Boolean> pigzombies = Value.of(true);
    @Expose protected final Value<Boolean> skeletons = Value.of(true);
    @Expose protected final Value<Boolean> illagers = Value.of(true);
    @Expose protected final Value<Boolean> guardians = Value.of(true);

    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    public PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : ponylevel;
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
    public void setPonyLevel(PonyLevel ponylevel) {
        this.ponylevel = ponylevel;
    }
}
