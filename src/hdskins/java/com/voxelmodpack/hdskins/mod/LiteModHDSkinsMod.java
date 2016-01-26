package com.voxelmodpack.hdskins.mod;

import java.io.File;
import java.lang.reflect.Method;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.gui.HDSkinsConfigPanel;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;
import com.voxelmodpack.voxelmenu.IPanoramaRenderer;

import net.minecraft.client.Minecraft;

public class LiteModHDSkinsMod implements HDSkinsMod {
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
        try {
            Class<?> ex = Class.forName("com.thevoxelbox.voxelmenu.GuiMainMenuVoxelBox");
            Method mRegisterCustomScreen = ex.getDeclaredMethod("registerCustomScreen", Class.class, String.class);
            mRegisterCustomScreen.invoke(null, GuiSkins.class, "HD Skins Manager");
        } catch (ClassNotFoundException var4) {
            // voxelmenu's not here, man
        } catch (Exception var5) {
            var5.printStackTrace();
        }

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
        ModUtilities.addRenderer(EntityPlayerModel.class, new RenderPlayerModel(minecraft.getRenderManager()));
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {}

    public static IPanoramaRenderer getPanoramaRenderer(IPanoramaRenderer fallbackRenderer) {
        try {
            Class<?> ex = Class.forName("com.thevoxelbox.voxelmenu.VoxelMenuModCore");
            Method mGetPanoramaRenderer = ex.getDeclaredMethod("getPanoramaRenderer");
            IPanoramaRenderer panoramaRenderer = (IPanoramaRenderer) mGetPanoramaRenderer.invoke(null);
            if (panoramaRenderer != null) {
                return panoramaRenderer;
            }
        } catch (ClassNotFoundException var4) {

        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return fallbackRenderer;
    }
}
