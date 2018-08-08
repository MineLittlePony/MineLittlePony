package com.voxelmodpack.hdskins;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.ViewportListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.AdvancedExposable;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GLWindow;
import com.voxelmodpack.hdskins.gui.HDSkinsConfigPanel;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;
import com.voxelmodpack.hdskins.skins.SkinServer;
import com.voxelmodpack.hdskins.skins.SkinServerSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IReloadableResourceManager;

import java.io.File;
import java.util.List;

@ExposableOptions(strategy = ConfigStrategy.Unversioned, filename = "hdskins")
public class LiteModHDSkins implements InitCompleteListener, ViewportListener, Configurable, AdvancedExposable {

    private static LiteModHDSkins instance;

    public static LiteModHDSkins instance() {
        return instance;
    }

    @Expose
    public List<SkinServer> skin_servers = SkinServer.defaultServers;

    @Expose
    public boolean experimentalSkinDrop = false;

    @Expose
    public String lastChosenFile = "";

    public LiteModHDSkins() {
        instance = this;
    }

    @Override
    public String getName() {
        return "HD Skins";
    }

    @Override
    public String getVersion() {
        return "4.0.0";
    }

    public void writeConfig() {
        LiteLoader.getInstance().writeConfig(this);
    }

    @Override
    public void init(File configPath) {

        // register config
        LiteLoader.getInstance().registerExposable(this, null);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(HDSkinManager.INSTANCE);
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
        return HDSkinsConfigPanel.class;
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        ModUtilities.addRenderer(EntityPlayerModel.class, new RenderPlayerModel<>(minecraft.getRenderManager()));

        // register skin servers.
        skin_servers.forEach(HDSkinManager.INSTANCE::addSkinServer);

        if (experimentalSkinDrop) {
            GLWindow.create();
        }
    }

    @Override
    public void onViewportResized(ScaledResolution resolution, int displayWidth, int displayHeight) {

    }

    @Override
    public void onFullScreenToggled(boolean fullScreen) {
        GLWindow.current().refresh(fullScreen);
    }
}
