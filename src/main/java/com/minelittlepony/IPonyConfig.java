package com.minelittlepony;

import com.minelittlepony.pony.data.PonyLevel;

public interface IPonyConfig {
    default boolean getSizes() { return true; }
    default boolean getSnuzzles() { return true; }
    default boolean getHD() { return true; }
    default boolean getShowScale() { return true; }
    default boolean getFPSMagic() { return true; }
    default boolean getPonySkulls() { return true; }
    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    default PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : getPonyLevel();
    }
    /**
     * Actually gets the pony level value. No option to ignore reality here.
     */
    PonyLevel getPonyLevel();
    default float getGlobalScaleFactor() {
        return getShowScale() ? 0.9F : 1;
    }
}
