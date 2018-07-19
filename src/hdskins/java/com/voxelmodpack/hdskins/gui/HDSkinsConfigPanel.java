package com.voxelmodpack.hdskins.gui;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.LiteModHDSkins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class HDSkinsConfigPanel implements ConfigPanel {

    private GuiButton button;

    private GuiCheckbox checkbox;

    private LiteModHDSkins mod;

    @Override
    public void onPanelShown(ConfigPanelHost host) {
        this.mod = LiteLoader.getInstance().getMod(LiteModHDSkins.class);

        this.button = new GuiButton(0, 40, 70, 100, 20, "Clear Skin Cache");
        this.checkbox = new GuiCheckbox(1, 40, 40, "Experimental Skin Drop");

        this.checkbox.checked = mod.experimentalSkinDrop;
    }

    @Override
    public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        this.button.drawButton(mc, mouseX, mouseY, partialTicks);
        this.checkbox.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
        Minecraft mc = Minecraft.getMinecraft();

        if (button.mousePressed(mc, mouseX, mouseY)) {
            HDSkinManager.INSTANCE.clearSkinCache();
        } else if (checkbox.mousePressed(mc, mouseX, mouseY)) {
            checkbox.checked = !checkbox.checked;
            mod.experimentalSkinDrop = checkbox.checked;

            LiteLoader.getInstance().writeConfig(mod);

            if (mod.experimentalSkinDrop) {
                GLWindow.create();
            } else {
                GLWindow.dispose();
            }
        }
    }

    @Override
    public String getPanelTitle() {
        return "HD Skins Settings";
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    @Override
    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode) {
    }

    @Override
    public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {
    }

    @Override
    public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onPanelHidden() {
    }

    @Override
    public void onPanelResize(ConfigPanelHost host) {
    }

    @Override
    public void onTick(ConfigPanelHost host) {
    }
}
