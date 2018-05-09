package com.minelittlepony;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;

import java.io.File;

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
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        mlp.onTick(minecraft, inGame);
    }
}
