package com.minelittlepony;

import com.minelittlepony.gui.GuiPonySettings;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

import net.minecraft.client.Minecraft;

import java.io.File;

public class LiteModMineLittlePony implements InitCompleteListener, Tickable, Configurable {

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
        Config config = new Config();

        MineLittlePony.getInstance().init(config);

        LiteLoader.getInput().registerKeyBinding(MineLittlePony.SETTINGS_GUI);
        LiteLoader.getInstance().registerExposable(config, null);
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        MineLittlePony.getInstance().postInit(minecraft);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        MineLittlePony.getInstance().onTick(minecraft, inGame);
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return GuiPonySettings.class;
    }

    @ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
    class Config extends PonyConfig implements Exposable {
        @Override
        public void save() {
            LiteLoader.getInstance().writeConfig(this);
        }
    }
}
