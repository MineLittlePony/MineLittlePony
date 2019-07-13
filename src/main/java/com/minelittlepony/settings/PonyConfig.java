package com.minelittlepony.settings;

import com.minelittlepony.common.client.gui.VisibilityMode;
import com.minelittlepony.common.config.JsonConfig;
import com.minelittlepony.common.config.Setting;
import com.minelittlepony.common.config.Value;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.pony.meta.Size;

/**
 * Storage container for MineLP client settings.
 */
public class PonyConfig implements JsonConfig {

    public static final PonyConfig INSTANCE = new PonyConfig();

    private PonyConfig() {
    }

    /**
     * Sets the pony level. Want MOAR PONEHS? Well here you go.
     */
    public final Value<PonyLevel> ponyLevel = new Value<>(PonyLevel.PONIES);
    @Setting(name = "globalScaleFactor")
    private final Value<Float> scaleFactor = new Value<>(0.9F);

    @Setting(category = "settings")
    public final Value<Boolean> sizes = new Value<>(false);
    @Setting(category = "settings")
    public final Value<Boolean> snuzzles = new Value<>(false);
    @Setting(category = "settings")
    public final Value<Boolean> fillycam = new Value<>(false);
    @Setting(category = "settings")
    private final Value<Boolean> showscale = new Value<>(false);
    @Setting(category = "settings")
    public final Value<Boolean> fpsmagic = new Value<>(false);
    @Setting(category = "settings")
    public final Value<Boolean> ponyskulls = new Value<>(false);
    @Setting(category = "settings")
    public final Value<Boolean> frustrum = new Value<>(false);

    /**
     * Debug override for pony sizes.
     */
    public final Value<Size> sizeOverride = new Value<>(Size.UNSET);

    /**
     * Visibility mode for the horse button.
     */
    public final Value<VisibilityMode> horseButton = new Value<>(VisibilityMode.AUTO);

    @Setting(category = "entities")
    public final Value<Boolean> villagers = new Value<>(true);
    @Setting(category = "entities")
    public final Value<Boolean> zombies = new Value<>(true);
    @Setting(category = "entities")
    public final Value<Boolean> pigzombies = new Value<>(true);
    @Setting(category = "entities")
    public final Value<Boolean> skeletons = new Value<>(true);
    @Setting(category = "entities")
    public final Value<Boolean> illagers = new Value<>(true);
    @Setting(category = "entities")
    public final Value<Boolean> guardians = new Value<>(true);
    @Setting(category = "entities")
    public final Value<Boolean> endermen = new Value<>(true);

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
