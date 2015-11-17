package com.brohoof.minelittlepony.gui;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.PonyConfig;
import com.brohoof.minelittlepony.PonyLevel;
import com.voxelmodpack.common.properties.VoxelProperty;
import com.voxelmodpack.common.properties.VoxelPropertyLabel;
import com.voxelmodpack.common.properties.gui.GuiVoxelBoxSettingsPanel;

import net.minecraft.client.resources.I18n;

public class MineLittlePonyGUI extends GuiVoxelBoxSettingsPanel {

    private static final String pref = "minelp.options.";

    private final String title = I18n.format(pref + "title");
    private final String ponylevel = I18n.format(pref + "ponylevel");
    private final String options = I18n.format(pref + "options");
    private final String hd = I18n.format(pref + "hd");
    private final String sizes = I18n.format(pref + "sizes");
    private final String ponyarmor = I18n.format(pref + "ponyarmor");
    private final String snuzzles = I18n.format(pref + "snuzzles");
    private final String showscale = I18n.format(pref + "showscale");

    public MineLittlePonyGUI() {
        this.config = MineLittlePony.getConfig();
        byte col1 = 30;
        this.properties.add(new VoxelPropertyIntSlider(this.config, "ponylevel", ponylevel, PANEL_LEFT, PANEL_TOP + 24));
        this.properties.add(new VoxelPropertyLabel(options, PANEL_LEFT + 15, PANEL_TOP + 58));
        this.properties.add(check("hd", hd, PANEL_LEFT + col1, PANEL_TOP + 72));
        this.properties.add(check("sizes", sizes, PANEL_LEFT + col1, PANEL_TOP + 90));
        this.properties.add(check("ponyarmor", ponyarmor, PANEL_LEFT + col1, PANEL_TOP + 108));
        this.properties.add(check("snuzzles", snuzzles, PANEL_LEFT + col1, PANEL_TOP + 126));
        this.properties.add(check("showscale", showscale, PANEL_LEFT + col1, PANEL_TOP + 144));
    }

    private VoxelProperty<?> check(String binding, String text, int xPos, int yPos) {
        return new FakeVoxelPropertyCheckBox(this.config, binding, text, xPos, yPos);
    }

    @Override
    public String getPanelTitle() {
        return title;
    }
}
