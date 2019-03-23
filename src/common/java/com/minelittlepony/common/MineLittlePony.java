package com.minelittlepony.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.common.pony.IPonyManager;
import com.minelittlepony.common.settings.PonyConfig;

public abstract class MineLittlePony {

    private static MineLittlePony instance;

    public static final Logger logger = LogManager.getLogger("MineLittlePony");

    public static final String MOD_NAME = "Mine Little Pony";
    public static final String MOD_VERSION = "@VERSION@";

    protected MineLittlePony() {
        instance = this;
    }

    /**
     * Gets the global MineLP instance.
     */
    public static MineLittlePony getInstance() {
        return instance;
    }

    /**
     * Gets the global MineLP client configuration.
     */
    public abstract PonyConfig getConfig();

    /**
     * Gets the static pony manager instance.
     */
    public abstract IPonyManager getManager();

    /**
     * Gets the global revision number, used for reloading models on demand.
     */
    public abstract int getModelRevisionNumber();
}

