package com.voxelmodpack.hdskins.gui;

import com.minelittlepony.gui.Button;
import com.minelittlepony.gui.Checkbox;
import com.minelittlepony.gui.SettingsPanel;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.LiteModHDSkins;

public class HDSkinsConfigPanel extends SettingsPanel {
    @Override
    public void initGui() {
        final LiteModHDSkins mod = LiteModHDSkins.instance();

        addButton(new Button(40, 70, 100, 20, "hdskins.options.cache", sender -> {
            HDSkinManager.INSTANCE.clearSkinCache();
        }));
        addButton(new Checkbox(40, 40, "hdskins.options.skindrops", mod.experimentalSkinDrop, checked -> {
            mod.experimentalSkinDrop = checked;

            mod.writeConfig();

            if (checked) {
                GLWindow.create();
            } else {
                GLWindow.dispose();
            }

            return checked;
        }));
    }

    @Override
    protected String getTitle() {
        return "HD Skins Settings";
    }
}
