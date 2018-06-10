package com.minelittlepony;

import com.minelittlepony.gui.PonySettingsPanel;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;

import net.minecraft.client.Minecraft;

import java.io.File;

public class LiteModMineLittlePony implements InitCompleteListener, Configurable {

    private MineLittlePony mlp;

    @Override
    public String getName() {
        return MineLittlePony.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return MineLittlePony.MOD_VERSION;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public void init(File configPath) {
        mlp = new MineLittlePony();
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        mlp.postInit(minecraft);
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return PonySettingsPanel.class;
    }
}
