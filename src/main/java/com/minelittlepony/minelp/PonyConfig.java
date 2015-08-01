package com.minelittlepony.minelp;

import com.minelittlepony.minelp.util.MineLPLogger;
import com.voxelmodpack.common.properties.ModConfig;

public class PonyConfig extends ModConfig {
    @Override
    protected void setDefaults() {
        this.defaults.setProperty("ponylevel", "2");
        this.defaults.setProperty("sizes", "1");
        this.defaults.setProperty("ponyarmor", "1");
        this.defaults.setProperty("snuzzles", "1");
        this.defaults.setProperty("hd", "1");
        this.defaults.setProperty("showscale", "1");
        this.defaults.setProperty("eqg", "0");
        this.defaults.setProperty("villagers", "1");
        this.defaults.setProperty("zombies", "1");
        this.defaults.setProperty("pigzombies", "1");
        this.defaults.setProperty("skeletons", "1");
        this.defaults.setProperty("oldSkinUploaded", "0");
    }

    public PonyConfig() {
        super("Mine Little Pony", "minelittlepony.properties");
    }

    @Override
    public String getOptionDisplayString(String binding) {
        return "";
    }

    public int getIntPropertySafe(String key) {
        return this.getIntPropertySafe(key, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public int getIntPropertySafe(String key, int minValue, int maxValue) {
        int value;
        try {
            value = this.getIntProperty(key);
        } catch (Exception var9) {
            try {
                boolean e2 = this.getBoolProperty(key);
                if (e2) {
                    value = 1;
                } else {
                    value = 0;
                }
            } catch (Exception var8) {
                int defaultValue1 = this.getDefaultIntProperty(key);
                this.setProperty(key, defaultValue1);
                MineLPLogger.error("Invalid value for config key \"%s\", using default value %d",
                        new Object[] { key, Integer.valueOf(defaultValue1) });
                return defaultValue1;
            }
        }

        if (value >= minValue && value <= maxValue) {
            return value;
        } else {
            int defaultValue = value = this.getDefaultIntProperty(key);
            this.setProperty(key, defaultValue);
            MineLPLogger.error(
                    "Invalid value for config key \"%s\", using default value %d. Found %d, expected value between %d and %d.",
                    new Object[] { key, Integer.valueOf(defaultValue), Integer.valueOf(value),
                            Integer.valueOf(minValue), Integer.valueOf(maxValue) });
            return defaultValue;
        }
    }

    public boolean isSet(String key) {
        return this.config.containsKey(key);
    }
}
