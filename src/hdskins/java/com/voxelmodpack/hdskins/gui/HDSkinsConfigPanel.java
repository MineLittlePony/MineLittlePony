package com.voxelmodpack.hdskins.gui;

import com.voxelmodpack.common.properties.VoxelPropertyToggleButton;
import com.voxelmodpack.common.properties.gui.GuiVoxelBoxSettingsPanel;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderBoolean;
import com.voxelmodpack.hdskins.HDSkinManager;

public class HDSkinsConfigPanel extends GuiVoxelBoxSettingsPanel implements IVoxelPropertyProviderBoolean {

    public HDSkinsConfigPanel() {
        this.properties.add(new VoxelPropertyToggleButton(this, "clear", "Clear local skin cache", 72, 8, 120, 70, 16));
    }

    @Override
    public String getPanelTitle() {
        return "HD Skins Settings";
    }

    @Override
    public String getStringProperty(String propertyName) {
        return null;
    }

    @Override
    public String getOptionDisplayString(String propertyName) {
        return "Clear now";
    }

    @Override
    public void toggleOption(String propertyName) {
        HDSkinManager.clearSkinCache();
    }

    @Override
    public String getDefaultPropertyValue(String propertyName) {
        return null;
    }

    @Override
    public void setProperty(String propertyName, boolean value) {}

    @Override
    public boolean getBoolProperty(String propertyName) {
        return true;
    }
}
