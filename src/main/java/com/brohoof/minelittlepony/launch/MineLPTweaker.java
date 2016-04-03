package com.brohoof.minelittlepony.launch;

import java.io.File;
import java.util.List;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class MineLPTweaker implements ITweaker {

    private static final String CONFIGS[] = { "mixin.hdskins.json", "mixin.minelp.json" };

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        for (String config : CONFIGS) {
            MixinEnvironment.getDefaultEnvironment().addConfiguration(config);
        }
    }

    //@formatter:off
    @Override public void acceptOptions(List<String> args, File gameDir, final File assetsDir, String profile) {}
    @Override public String getLaunchTarget() { return null; }
    @Override public String[] getLaunchArguments() { return new String[0]; }
    //@formatter:on
}
