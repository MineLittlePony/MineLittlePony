package com.minelittlepony.hdskins.litemod;

import com.google.gson.GsonBuilder;
import com.minelittlepony.common.client.gui.GuiLiteHost;
import com.minelittlepony.hdskins.HDSkinManager;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.gui.HDSkinsConfigPanel;
import com.minelittlepony.hdskins.server.SkinServer;
import com.minelittlepony.hdskins.server.SkinServerSerializer;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.ViewportListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.AdvancedExposable;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.ModUtilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import java.io.File;
import java.util.function.Function;

@ExposableOptions(strategy = ConfigStrategy.Unversioned, filename = "hdskins")
public class LiteModHDSkins extends HDSkins implements InitCompleteListener, ViewportListener, Configurable, AdvancedExposable {

    @Override
    public String getName() {
        return HDSkins.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return HDSkins.VERSION;
    }

    @Override
    public void saveConfig() {
        LiteLoader.getInstance().writeConfig(this);
    }

    @Override
    public void init(File configPath) {

        // register config
        LiteLoader.getInstance().registerExposable(this, null);
        super.init();
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
        HDSkinManager.INSTANCE.clearSkinCache();
    }

    @Override
    public void setupGsonSerialiser(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(SkinServer.class, new SkinServerSerializer());
    }

    @Override
    public File getConfigFile(File configFile, File configFileLocation, String defaultFileName) {
        return null;
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return Panel.class;
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        initComplete();
    }

    @Override
    public void onViewportResized(ScaledResolution resolution, int displayWidth, int displayHeight) {

    }

    @Override
    public void onFullScreenToggled(boolean fullScreen) {
        super.onToggledFullScreen(fullScreen);
    }

    @Override
    protected <T extends Entity> void addRenderer(Class<T> type, Function<RenderManager, Render<T>> renderer) {
        ModUtilities.addRenderer(type, renderer.apply(Minecraft.getMinecraft().getRenderManager()));
    }

    @Override
    public File getAssetsDirectory() {
        return LiteLoader.getAssetsDirectory();
    }

    public static class Panel extends GuiLiteHost {
        public Panel() {
            super(new HDSkinsConfigPanel());
        }
    }
}
