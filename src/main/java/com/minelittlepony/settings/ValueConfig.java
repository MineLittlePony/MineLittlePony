package com.minelittlepony.settings;

import com.google.gson.GsonBuilder;
import com.mumfrey.liteloader.modconfig.AdvancedExposable;

import java.io.File;

/**
 * A sensible config container that actually lets us programatically index values by a key.
 *
 * Reflection because Mumfrey pls.
 *
 */
// Mumfrey pls.
public class ValueConfig implements AdvancedExposable {
    @Override
    public void setupGsonSerialiser(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(Value.class, new ValueSerializer());
    }

    @Override
    public File getConfigFile(File configFile, File configFileLocation, String defaultFileName) {
        return null;
    }
}
