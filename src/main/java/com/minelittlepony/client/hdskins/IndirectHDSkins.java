package com.minelittlepony.client.hdskins;

import org.apache.logging.log4j.LogManager;

public final class IndirectHDSkins {
    public static void initialize() {
        try {
            new MineLPHDSkins();
        } catch (Exception e) {
            LogManager.getLogger().warn("Failed to initialize hooks for hdskins", e);
        }
    }
}
