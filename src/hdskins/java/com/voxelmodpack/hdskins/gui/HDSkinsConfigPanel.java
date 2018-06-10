package com.voxelmodpack.hdskins.gui;

import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiConfig;

public class HDSkinsConfigPanel extends GuiConfig {

    private GuiButton button;

    public HDSkinsConfigPanel() {
        super(null, "hdskins", "HD Skins");
    }

    @Override
    public void initGui() {
        this.addButton(new GuiButton(0, 40, 10, 100, 20, "Clear Skin Cache"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        if (button.id == 0) {
            HDSkinManager.clearSkinCache();
        }
    }
}
