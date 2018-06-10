package com.minelittlepony.settings;

import com.minelittlepony.pony.data.PonyLevel;
import net.minecraftforge.common.config.Config;

/**
 * Storage container for MineLP client settings.
 */
@Config(modid = "minelittlepony", name = "options")
@Config.LangKey("minelp.options.options")
public class PonyConfig {

    @Config.LangKey("minelp.options.ponylevel")
    public static PonyLevel ponylevel = PonyLevel.PONIES;

    @Config.LangKey("minelp.options.sizes")
    public static boolean sizes = true;
    public static boolean snuzzles = true;
    public static boolean hd = true;
    public static boolean showscale = true;


    public enum PonySettings implements Setting {
        SIZES,
        SNUZZLES,
        HD,
        SHOWSCALE

    }

    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    public static PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : getPonyLevel();
    }

    /**
     * Actually gets the pony level value. No option to ignore reality here.
     */
    public static PonyLevel getPonyLevel() {
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
    public static void setPonyLevel(PonyLevel ponylevel) {
        PonyConfig.ponylevel = ponylevel;
    }

}
