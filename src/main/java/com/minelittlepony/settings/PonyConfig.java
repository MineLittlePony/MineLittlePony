package com.minelittlepony.settings;

import net.minecraft.util.math.MathHelper;

/**
 * Storage container for MineLP client settings.
 */
public class PonyConfig extends JsonConfig {

    private final Setting<PonyLevel> ponyLevel = new Value<>("ponylevel", PonyLevel.PONIES);
    private final Setting<Float> scaleFactor = new Value<>("globalScaleFactor", 0.9F);

    public PonyConfig() {
        initWith(PonySettings.values());
    }

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
        return ponyLevel.get();
    }

    /**
     * Sets the pony level. Want MOAR PONEHS? Well here you go.
     *
     * @param ponylevel
     */
    public void setPonyLevel(PonyLevel ponylevel) {
        ponyLevel.set(ponylevel);
    }

    public void setGlobalScaleFactor(float f) {
        f = Math.round(MathHelper.clamp(f, 0.1F, 3) * 100) / 100F;

        scaleFactor.set(f);
        PonySettings.SHOWSCALE.set(f != 1);
    }

    /**
     * Gets the universal scale factor used to determine how tall ponies are.
     */
    public float getGlobalScaleFactor() {
        return PonySettings.SHOWSCALE.get() ? scaleFactor.get() : 1;
    }
}
