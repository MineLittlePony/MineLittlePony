package com.minelittlepony.api.model;

public enum RenderPass {
    GUI,
    WORLD,
    HUD;

    private static RenderPass CURRENT = WORLD;

    public static RenderPass getCurrent() {
        return CURRENT;
    }

    public static void swap(RenderPass pass) {
        CURRENT = pass == null ? WORLD : pass;
    }
}
