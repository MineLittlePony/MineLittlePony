package com.voxelmodpack.common.status;

import java.io.FileWriter;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.common.properties.ModConfig;

/**
 * @author anangrybeaver
 */
public class StatusMessageConfig extends ModConfig {
    public StatusMessageConfig() {
        super("StatusMessage", "statusmessage.properties");
    }

    @Override
    protected void createConfig() {
        try {
            this.config.setProperty("showStatuses", "true");
            this.config.store(new FileWriter(this.propertiesFile), null);
        } catch (Exception e) {
            LiteLoaderLogger.warning("%s> ERROR: %s", this.modName, e.toString());
        }
    }

    @Override
    public String getOptionDisplayString(String optionName) {
        return null;
    }

    @Override
    public void toggleOption(String optionName) {
        StatusMessageManager.getInstance().toggleOption(optionName);
    }

    @Override
    protected void setDefaults() {
        this.defaults.setProperty("showStatuses", "true");
    }
}