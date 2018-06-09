package com.voxelmodpack.hdskins.mod;

import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GLWindow;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.gui.HDSkinsConfigPanel;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IReloadableResourceManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

@ExposableOptions(strategy = ConfigStrategy.Unversioned, filename = "hdskins")
public class LiteModHDSkinsMod implements HDSkinsMod {

    @Expose
    public List<String> skin_servers = SkinServer.defaultServers;
    @Expose
    public boolean experimentalSkinDrop = false;

    @Override
    public String getName() {
        return "HD Skins";
    }

    @Override
    public String getVersion() {
        return "4.0.0";
    }

    @Override
    public void init(File configPath) {

        // register config
        LiteLoader.getInstance().registerExposable(this, null);

        // try it initialize voxelmenu button
        try {
            Class<?> ex = Class.forName("com.thevoxelbox.voxelmenu.GuiMainMenuVoxelBox");
            Method mRegisterCustomScreen = ex.getDeclaredMethod("registerCustomScreen", Class.class, String.class);
            mRegisterCustomScreen.invoke(null, GuiSkins.class, "HD Skins Manager");
        } catch (ClassNotFoundException var4) {
            // voxelmenu's not here, man
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(HDSkinManager.INSTANCE);

        if (experimentalSkinDrop) GLWindow.create();
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
        HDSkinManager.clearSkinCache();
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return HDSkinsConfigPanel.class;
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        ModUtilities.addRenderer(EntityPlayerModel.class, new RenderPlayerModel<>(minecraft.getRenderManager()));

        // register skin servers.
        for (String s : skin_servers) {
            try {
                HDSkinManager.INSTANCE.addSkinServer(SkinServer.from(s));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onViewportResized(ScaledResolution resolution, int displayWidth, int displayHeight) {

    }

    @Override
    public void onFullScreenToggled(boolean fullScreen) {
        if (!fullScreen && GLWindow.current() != null) {
            GLWindow.current().refresh();
        }
    }
}
