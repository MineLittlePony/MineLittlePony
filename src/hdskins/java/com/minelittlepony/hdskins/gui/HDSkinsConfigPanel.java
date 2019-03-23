package com.minelittlepony.hdskins.gui;

import com.minelittlepony.common.gui.Checkbox;
import com.minelittlepony.common.gui.SettingsPanel;
import com.minelittlepony.hdskins.LiteModHDSkins;
import com.minelittlepony.hdskins.upload.GLWindow;

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
