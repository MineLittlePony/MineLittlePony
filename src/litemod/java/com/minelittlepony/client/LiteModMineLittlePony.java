package com.minelittlepony.client;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.common.client.gui.GuiLiteHost;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.client.overlays.IMinecraft;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.ModUtilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Timer;

import java.io.File;

public class LiteModMineLittlePony implements IModUtilities, InitCompleteListener, Tickable, Configurable {

    private final MineLPClient mlp = new MineLPClient(this);

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
        mlp.init(new Config());

        LiteLoader.getInput().registerKeyBinding(MineLPClient.SETTINGS_GUI);
        LiteLoader.getInstance().registerExposable(config, null);
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        mlp.postInit(minecraft);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        mlp.onTick(minecraft, inGame);
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return Panel.class;
    }

    @Override
    public <T extends TileEntity> void addRenderer(Class<T> type, TileEntityRenderer<T> renderer) {
        ModUtilities.addRenderer(type, renderer);
    }

    @Override
    public <T extends Entity> void addRenderer(Class<T> type, Render<T> renderer) {
        ModUtilities.addRenderer(type, renderer);
    }

    @Override
    public boolean hasFml() {
        return ModUtilities.fmlIsPresent();
    }

    @Override
    public float getRenderPartialTicks() {
        return ((IMinecraft)Minecraft.getInstance()).getTimer().renderPartialTicks;
    }

    public static class Panel extends GuiLiteHost {
        public Panel() {
            super(new GuiPonySettings());
        }
    }

    @ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
    class Config extends ClientPonyConfig implements Exposable {
        @Override
        public void save() {
            LiteLoader.getInstance().writeConfig(this);
        }
    }
}
