package com.minelittlepony.client.gui.hdskins;

import org.apache.logging.log4j.LogManager;

public final class IndirectHDSkins {
    public static void initialize() {
        try {
            Class.forName("com.minelittlepony.client.hdskins.MineLPHDSkins").getConstructor().newInstance();
        } catch (Exception e) {
            LogManager.getLogger().warn("Failed to initialize hooks for hdskins", e);
        }
    }
}
