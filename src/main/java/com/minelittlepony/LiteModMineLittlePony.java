package com.minelittlepony;

import java.io.File;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

import net.minecraft.client.Minecraft;

public class LiteModMineLittlePony implements Tickable, InitCompleteListener {

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
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

    @Override
    public void init(File configPath) {
        this.mlp = new MineLittlePony();
        this.mlp.init();

    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        this.mlp.postInit(minecraft);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        this.mlp.onTick(minecraft, inGame);
    }

}
