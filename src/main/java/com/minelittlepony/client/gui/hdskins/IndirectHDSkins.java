package com.minelittlepony.client.gui.hdskins;

public final class IndirectHDSkins {
    public static void initialize() {
        try {
            Class<?> mlphd = Class.forName("com.minelittlepony.client.hdskins.MineLPHDSkins");
            mlphd.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
