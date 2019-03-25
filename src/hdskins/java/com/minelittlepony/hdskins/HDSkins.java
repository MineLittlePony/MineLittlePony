package com.minelittlepony.hdskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;

import com.google.gson.annotations.Expose;
import com.minelittlepony.hdskins.gui.EntityPlayerModel;
import com.minelittlepony.hdskins.gui.RenderPlayerModel;
import com.minelittlepony.hdskins.server.SkinServer;
import com.minelittlepony.hdskins.upload.GLWindow;

import java.io.File;
import java.util.List;
import java.util.function.Function;

public abstract class HDSkins {
    public static final String MOD_NAME = "HD Skins";
    public static final String VERSION = "4.0.0";

    private static HDSkins instance;

    public static HDSkins getInstance() {
        return instance;
    }

    public HDSkins() {
        instance = this;
    }

    @Expose
    public List<SkinServer> skin_servers = SkinServer.defaultServers;

    @Expose
    public boolean experimentalSkinDrop = false;

    @Expose
    public String lastChosenFile = "";

    public void init() {
        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        irrm.addReloadListener(HDSkinManager.INSTANCE);
    }

    public abstract File getAssetsDirectory();

    public abstract void saveConfig();

    protected abstract <T extends Entity> void addRenderer(Class<T> type, Function<RenderManager, Render<T>> renderer);

    public void initComplete() {
        addRenderer(EntityPlayerModel.class, RenderPlayerModel::new);

        // register skin servers.
        skin_servers.forEach(HDSkinManager.INSTANCE::addSkinServer);

        if (experimentalSkinDrop) {
            GLWindow.create();
        }
    }

    public void onToggledFullScreen(boolean fullScreen) {
        GLWindow.current().refresh(fullScreen);
    }

}
