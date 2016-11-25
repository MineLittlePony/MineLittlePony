package com.voxelmodpack.hdskins.mod;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.gui.HDSkinsConfigPanel;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

import java.io.File;
import java.lang.reflect.Method;

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

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(HDSkinManager.INSTANCE);
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
    }
}
