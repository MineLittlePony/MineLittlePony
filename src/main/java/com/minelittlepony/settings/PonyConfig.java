package com.minelittlepony.settings;

import net.minecraft.util.math.MathHelper;

import com.minelittlepony.common.util.settings.JsonConfig;
import com.minelittlepony.common.util.settings.Setting;
import com.minelittlepony.pony.meta.Size;

/**
 * Storage container for MineLP client settings.
 */
public class PonyConfig extends JsonConfig {

    /**
     * Sets the pony level. Want MOAR PONEHS? Well here you go.
     */
    public final Setting<PonyLevel> ponyLevel = value("ponylevel", PonyLevel.PONIES);
    private final Setting<Float> scaleFactor = value("globalScaleFactor", 0.9F);

    public final Setting<Boolean> sizes =      value("settings", "sizes", false);
    public final Setting<Boolean> snuzzles =   value("settings", "snuzzles", false);
    public final Setting<Boolean> fillycam =   value("settings", "fillycam", false);
    private final Setting<Boolean> showscale = value("settings", "showscale", false);
    public final Setting<Boolean> fpsmagic =   value("settings", "fpsmagic", false);
    public final Setting<Boolean> ponyskulls = value("settings", "ponyskulls", false);
    public final Setting<Boolean> frustrum =   value("settings", "frustrum", false);

    /**
     * Debug override for pony sizes.
     */
    public final Setting<Size> sizeOverride = value("sizeOverride", Size.UNSET);

    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    public PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : ponyLevel.get();
    }

    public float setGlobalScaleFactor(float f) {
        f = Math.round(MathHelper.clamp(f, 0.1F, 3) * 100) / 100F;

        scaleFactor.set(f);
        showscale.set(f != 1);

        return getGlobalScaleFactor();
    }

    /**
     * Gets the universal scale factor used to determine how tall ponies are.
     */
    public float getGlobalScaleFactor() {
        return showscale.get() ? scaleFactor.get() : 1;
    }
}
