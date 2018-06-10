package com.minelittlepony.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;
import javax.annotation.Nullable;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MLPLoadingPlugin implements IFMLLoadingPlugin {

    public MLPLoadingPlugin() {
        MixinBootstrap.init();
        Mixins.addConfigurations("minelp.mixin.json", "hdskins.mixin.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
