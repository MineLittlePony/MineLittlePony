package com.minelittlepony;

import com.google.gson.annotations.Expose;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.settings.Value;
import com.minelittlepony.settings.ValueConfig;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

import java.util.function.Function;

/**
 * Storage container for MineLP client settings.
 */
@ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
public class PonyConfig extends ValueConfig {

    @Expose private PonyLevel ponylevel = PonyLevel.PONIES;

    @Expose private final Value<Boolean> sizes = Value.of(true);
    @Expose private final Value<Boolean> snuzzles = Value.of(true);
    @Expose private final Value<Boolean> hd = Value.of(true);
    @Expose private final Value<Boolean> showscale = Value.of(true);
    @Expose private final Value<Boolean> fpsmagic = Value.of(true);
    @Expose private final Value<Boolean> ponyskulls = Value.of(true);

    @Expose private final Value<Boolean> villagers = Value.of(true);
    @Expose private final Value<Boolean> zombies = Value.of(true);
    @Expose private final Value<Boolean> pigzombies = Value.of(true);
    @Expose private final Value<Boolean> skeletons = Value.of(true);
    @Expose private final Value<Boolean> illagers = Value.of(true);
    @Expose private final Value<Boolean> guardians = Value.of(true);

    public enum PonySettings implements Value<Boolean> {

        SIZES(PonyConfig::getSizes),
        SNUZZLES(PonyConfig::getSnuzzles),
        HD(PonyConfig::getHd),
        SHOWSCALE(PonyConfig::getShowscale),
        FPSMAGIC(PonyConfig::getFpsmagic),
        PONYSKULLS(PonyConfig::getPonyskulls);

        private Value<Boolean> config;

        PonySettings(Function<PonyConfig, Value<Boolean>> config) {
            this.config = config.apply(MineLittlePony.getConfig());
        }

        @Override
        public Boolean get() {
            return config.get();
        }

        @Override
        public void set(Boolean value) {
            config.set(value);
        }
    }


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

    public Value<Boolean> getSizes() {
        return sizes;
    }

    public Value<Boolean> getSnuzzles() {
        return snuzzles;
    }

    public Value<Boolean> getHd() {
        return hd;
    }

    public Value<Boolean> getShowscale() {
        return showscale;
    }

    public Value<Boolean> getFpsmagic() {
        return fpsmagic;
    }

    public Value<Boolean> getPonyskulls() {
        return ponyskulls;
    }

    public Value<Boolean> getVillagers() {
        return villagers;
    }

    public Value<Boolean> getZombies() {
        return zombies;
    }

    public Value<Boolean> getPigzombies() {
        return pigzombies;
    }

    public Value<Boolean> getSkeletons() {
        return skeletons;
    }

    public Value<Boolean> getIllagers() {
        return illagers;
    }

    public Value<Boolean> getGuardians() {
        return guardians;
    }
}
