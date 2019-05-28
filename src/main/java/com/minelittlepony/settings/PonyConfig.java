package com.minelittlepony.settings;

import net.minecraft.util.math.MathHelper;

import com.google.gson.annotations.Expose;

/**
 * Storage container for MineLP client settings.
 */
public abstract class PonyConfig extends SensibleJsonConfig {

    @Expose private PonyLevel ponylevel = PonyLevel.PONIES;

    @Expose boolean sizes = true;
    @Expose boolean snuzzles = true;
    @Expose boolean hd = true;
    @Expose boolean showscale = true;
    @Expose boolean fpsmagic = true;
    @Expose boolean ponyskulls = true;
    @Expose boolean frustrum = true;

    @Expose boolean villagers = true;
    @Expose boolean zombies = true;
    @Expose boolean pigzombies = true;
    @Expose boolean skeletons = true;
    @Expose boolean illagers = true;
    @Expose boolean guardians = true;
    @Expose boolean endermen = true;

    @Expose private float globalScaleFactor = 0.9F;

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
        this.ponylevel = ponylevel;
    }

    public void setGlobalScaleFactor(float f) {
        globalScaleFactor = Math.round(MathHelper.clamp(f, 0.1F, 3) * 100) / 100F;
        showscale = globalScaleFactor != 1;
    }

    /**
     * Gets the universal scale factor used to determine how tall ponies are.
     */
    public float getGlobalScaleFactor() {
        return showscale ? globalScaleFactor : 1;
    }
}
