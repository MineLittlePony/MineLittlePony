package com.minelittlepony.settings;

import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.common.util.settings.JsonConfig;
import com.minelittlepony.common.util.settings.Setting;

import java.nio.file.Path;

/**
 * Storage container for MineLP client settings.
 */
public class PonyConfig extends JsonConfig {
    /**
     * Sets the pony level. Want MOAR PONEHS? Well here you go.
     */
    public final Setting<PonyLevel> ponyLevel = value("ponylevel", PonyLevel.PONIES);
    private final Setting<Float> scaleFactor = value("globalScaleFactor", 0.9F);

    public final Setting<Boolean> sizes =      value("settings", "sizes", true);
    public final Setting<Boolean> snuzzles =   value("settings", "snuzzles", true);
    public final Setting<Boolean> fillycam =   value("settings", "fillycam", true);
    private final Setting<Boolean> showscale = value("settings", "showscale", true);
    public final Setting<Boolean> fpsmagic =   value("settings", "fpsmagic", true);
    public final Setting<Boolean> tpsmagic =   value("settings", "tpsmagic", true);
    public final Setting<Boolean> ponyskulls = value("settings", "ponyskulls", true);
    public final Setting<Boolean> frustrum =   value("settings", "frustrum", true);

    /**
     * Debug override for pony sizes.
     */
    public final Setting<Sizes> sizeOverride = value("sizeOverride", Sizes.UNSET);

    public final Setting<Boolean> flappyElytras = value("customisation", "flappyElytras", false);
    public final Setting<Boolean> noFun = value("customisation", "noFun", false);

    public PonyConfig(Path path) {
        super(path);
    }

    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    public PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : ponyLevel.get();
    }

    public float setGlobalScaleFactor(float f) {

        if (f < 0.15F) {
            f = 0.1F;
        }
        if (f > 2.95) {
            f = 3;
        }
        if (f > 1.97 && f < 2.03) {
            f = 2;
        }
        if (f > 0.97 && f < 1.03) {
            f = 1;
        }
        if (f > 0.87 && f < 0.93) {
            f = 0.9F;
        }

        f = Math.round(MathHelper.clamp(f, 0.1F, 3) * 100F) / 100F;

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
