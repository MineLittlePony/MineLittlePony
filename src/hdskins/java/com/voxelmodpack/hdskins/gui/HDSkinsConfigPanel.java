package com.voxelmodpack.hdskins.gui;

import com.minelittlepony.gui.Checkbox;
import com.minelittlepony.gui.SettingsPanel;
import com.voxelmodpack.hdskins.LiteModHDSkins;
import com.voxelmodpack.hdskins.upload.GLWindow;

public class HDSkinsConfigPanel extends SettingsPanel {
    @Override
    public void initGui() {
        final LiteModHDSkins mod = LiteModHDSkins.instance();

        addButton(new Checkbox(40, 40, "hdskins.options.skindrops", mod.experimentalSkinDrop, checked -> {
            mod.experimentalSkinDrop = checked;

            mod.writeConfig();

            if (checked) {
                GLWindow.create();
            } else {
                GLWindow.dispose();
            }

            return checked;
        })).setTooltip(formatMultiLine("hdskins.warning.experimental", 250));
    }

    @Override
    protected String getTitle() {
        return "HD Skins Settings";
    }
}
