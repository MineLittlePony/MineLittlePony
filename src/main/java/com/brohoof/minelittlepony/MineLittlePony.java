package com.brohoof.minelittlepony;

import net.minecraft.client.Minecraft;

public class MineLittlePony {

    private static MineLittlePony instance;

    private PonyConfig config;
    private PonyManager ponyManager;
    private ProxyContainer proxy;

    private MineLittlePony() {

        this.config = new PonyConfig();
        this.ponyManager = new PonyManager(config);
        this.proxy = new ProxyContainer();
    }

    public static MineLittlePony getInstance() {
        if (instance == null)
            instance = new MineLittlePony();
        return instance;
    }

    public PonyManager getManager() {
        return this.ponyManager;
    }

    public static ProxyContainer getProxy() {
        return getInstance().proxy;
    }

    public static PonyConfig getConfig() {
        return getInstance().config;
    }

    public static String getSPUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }
}
