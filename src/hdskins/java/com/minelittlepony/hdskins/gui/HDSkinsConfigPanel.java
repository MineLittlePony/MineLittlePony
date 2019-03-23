package com.minelittlepony.hdskins.gui;

import com.minelittlepony.common.client.gui.Checkbox;
import com.minelittlepony.common.client.gui.GuiHost;
import com.minelittlepony.common.client.gui.IGuiGuest;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.upload.GLWindow;

public class HDSkinsConfigPanel implements IGuiGuest {
    @Override
    public void initGui(GuiHost host) {
        final HDSkins mod = HDSkins.getInstance();

        host.addButton(new Checkbox(40, 40, "hdskins.options.skindrops", mod.experimentalSkinDrop, checked -> {
            mod.experimentalSkinDrop = checked;

            mod.saveConfig();

            if (checked) {
                GLWindow.create();
            } else {
                GLWindow.dispose();
            }

            return checked;
        })).setTooltip(host.formatMultiLine("hdskins.warning.experimental", 250));
    }

    @Override
    public String getTitle() {
        return "HD Skins Settings";
    }
}
