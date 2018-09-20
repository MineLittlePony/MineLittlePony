package com.minelittlepony;

import com.google.gson.annotations.Expose;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.settings.SensibleConfig;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.voxelmodpack.hdskins.HDSkinManager;

/**
 * Storage container for MineLP client settings.
 */
@ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
public class PonyConfig extends SensibleConfig implements Exposable {

    @Expose private PonyLevel ponylevel = PonyLevel.PONIES;

    @Expose public boolean sizes = true;
    @Expose public boolean snuzzles = true;
    @Expose public boolean hd = true;
    @Expose public boolean showscale = true;
    @Expose public boolean fpsmagic = true;
    @Expose public boolean ponyskulls = true;
    @Expose public boolean frustrum = true;

    public enum PonySettings implements Setting {
        SIZES,
        SNUZZLES,
        HD,
        SHOWSCALE,
        FPSMAGIC,
        PONYSKULLS,
        FRUSTRUM;
    }

    @Expose public boolean villagers = true;
    @Expose public boolean zombies = true;
    @Expose public boolean pigzombies = true;
    @Expose public boolean skeletons = true;
    @Expose public boolean illagers = true;
    @Expose public boolean guardians = true;
    @Expose public boolean endermen = true;

    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    public PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : getPonyLevel();
    }

    /**
     * Actually gets the pony level value. No option to ignore reality here.
     */
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
        // only trigger reloads when the value actually changes
        if (ponylevel != this.ponylevel) {
            this.ponylevel = ponylevel;
            HDSkinManager.INSTANCE.parseSkins();
        }
    }

    public float getGlobalScaleFactor() {
        return showscale ? 0.9F : 1;
    }
}
