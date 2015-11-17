package com.voxelmodpack.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderFriend;
import com.mumfrey.liteloader.launch.ClassPathUtilities;
import com.mumfrey.liteloader.modconfig.ConfigManager;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.launchwrapper.Launch;

public abstract class VoxelCommonLiteMod implements LiteMod, Configurable {
    private String bundledJarName = "voxelcommon-2.4.0.jar";
    private final String voxelCommonClassName = "com.voxelmodpack.common.LiteModVoxelCommon";
    private final String modClassName;
    private LiteMod mod;

    public VoxelCommonLiteMod(String modClassName) {
        this.bundledJarName = LiteLoader.getInstance().getModMetaData(this, "voxelCommonJarName", this.bundledJarName);
        this.modClassName = modClassName;
    }

    @Override
    public void init(File configPath) {
        try {
            this.getClass();
            Class.forName(voxelCommonClassName, false, Launch.class.getClassLoader());
        } catch (Throwable var4) {
            this.getClass();
            if (!this.extractAndInjectMod("VoxelLib", voxelCommonClassName, this.bundledJarName,
                    Files.createTempDir())) {
                return;
            }
        }

        try {
            Class<?> th = Class.forName(this.modClassName);
            this.mod = (LiteMod) th.newInstance();
            this.mod.init(configPath);
            if (this.mod instanceof Configurable && ((Configurable) this.mod).getConfigPanelClass() != null) {
                this.registerConfigurable();
            }

            LiteLoader.getInterfaceManager().registerListener(this.mod);
        } catch (Throwable var3) {
            var3.printStackTrace();
        }
    }

    public void registerConfigurable() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field configManagerField = LiteLoader.class.getDeclaredField("configManager");
        configManagerField.setAccessible(true);
        ConfigManager mgr = (ConfigManager) configManagerField.get(LiteLoader.getInstance());
        mgr.registerMod(this);
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return this.mod != null && this.mod instanceof Configurable ? ((Configurable) this.mod).getConfigPanelClass() : null;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
        if (this.mod != null) {
            this.mod.upgradeSettings(version, configPath, oldConfigPath);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean extractAndInjectMod(String libraryName, String className, String resourceName, File libPath) {
        if ("true".equals(System.getProperty("VoxelCommon.Injected"))) {
            LiteLoaderLogger.warning("%s jar was already injected, skipping injection.", libraryName);
            return true;
        }
        File jarFile = new File(libPath, resourceName);
        if (!jarFile.exists()) {
            LiteLoaderLogger.info("%s jar does not exist, attempting to extract to %s", libraryName, libPath.getAbsolutePath());
            if (!extractFile("/" + resourceName, jarFile)) {
                LiteLoaderLogger.warning("%s jar could not be extracted, %s may not function correctly (or at all)", libraryName, this.getName());
                return false;
            }
        }

        if (jarFile.exists()) {
            LiteLoaderLogger.info("%s jar exists, attempting to inject into classpath", libraryName);

            try {
                ClassPathUtilities.injectIntoClassPath(Launch.classLoader, jarFile.toURI().toURL());
                LiteLoaderFriend.loadMod(libraryName, (Class<? extends LiteMod>) Class.forName(className), jarFile);
            } catch (Exception var7) {
                var7.printStackTrace();
                return false;
            }

            System.setProperty("VoxelCommon.Injected", "true");
            LiteLoaderLogger.info("%s jar was successfully extracted", libraryName);
            return true;
        }
        LiteLoaderLogger.warning("%s jar was not detected, %s may not function correctly (or at all)", libraryName, this.getName());
        return false;
    }

    private static boolean extractFile(String resourceName, File outputFile) {
        try {
            InputStream ex = VoxelCommonLiteMod.class.getResourceAsStream(resourceName);
            ByteSink sink = Files.asByteSink(outputFile);
            sink.writeFrom(ex);
            return true;
        } catch (NullPointerException var4) {
            return false;
        } catch (IOException var5) {
            return false;
        }
    }
}
