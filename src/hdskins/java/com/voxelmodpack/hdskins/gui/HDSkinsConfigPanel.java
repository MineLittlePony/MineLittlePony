package com.voxelmodpack.hdskins.gui;

import com.minelittlepony.gui.Button;
import com.minelittlepony.gui.Checkbox;
import com.minelittlepony.gui.SettingsPanel;
import com.mumfrey.liteloader.core.LiteLoader;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.LiteModHDSkins;

public class HDSkinsConfigPanel extends SettingsPanel {
    @Override
    public void initGui() {
        final LiteModHDSkins mod = LiteLoader.getInstance().getMod(LiteModHDSkins.class);

        addButton(new Button(40, 70, 100, 20, "Clear Skin Cache", sender ->{
            HDSkinManager.INSTANCE.clearSkinCache();
        }));
        addButton(new Checkbox(40, 40, "Experimental Skin Drop", mod.experimentalSkinDrop, checked -> {
            System.out.println(checked);
            mod.experimentalSkinDrop = checked;

            LiteLoader.getInstance().writeConfig(mod);

            if (mod.experimentalSkinDrop) {
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
