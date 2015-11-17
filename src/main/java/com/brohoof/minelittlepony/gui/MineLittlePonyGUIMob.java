package com.brohoof.minelittlepony.gui;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.PonyConfig;
import com.voxelmodpack.common.properties.VoxelProperty;
import com.voxelmodpack.common.properties.VoxelPropertyLabel;
import com.voxelmodpack.common.properties.gui.GuiVoxelBoxSettingsPanel;

import net.minecraft.client.resources.I18n;

public class MineLittlePonyGUIMob extends GuiVoxelBoxSettingsPanel {

    private static final String pref = "minelp.mobs.";

    private final String restart1 = I18n.format("minelp.restart1");
    private final String restart2 = I18n.format("minelp.restart2");

    private final String title = I18n.format(pref + "title");
    private final String villagers = I18n.format(pref + "villagers");
    private final String zombies = I18n.format(pref + "zombies");
    private final String zombiePigmen = I18n.format(pref + "zombiepigmen");
    private final String skeletons = I18n.format(pref + "skeletons");

    public MineLittlePonyGUIMob() {
        this.config = MineLittlePony.getConfig();
        byte col1 = 30;
        this.properties.add(new VoxelPropertyLabel(restart1, PANEL_LEFT + 15, PANEL_TOP + 11, 0xff6666));
        this.properties.add(new VoxelPropertyLabel(restart2, PANEL_LEFT + 15, PANEL_TOP + 23, 0xff6666));
        this.properties.add(check("villagers", villagers, PANEL_LEFT + col1, PANEL_TOP + 42));
        this.properties.add(check("zombies", zombies, PANEL_LEFT + col1, PANEL_TOP + 60));
        this.properties.add(check("pigzombies", zombiePigmen, PANEL_LEFT + col1, PANEL_TOP + 78));
        this.properties.add(check("skeletons", skeletons, PANEL_LEFT + col1, PANEL_TOP + 96));
    }

    private VoxelProperty<?> check(String binding, String text, int xPos, int yPos) {
        return new FakeVoxelPropertyCheckBox(this.config, binding, text, xPos, yPos);
    }

    @Override
    public String getPanelTitle() {
        return title;
    }
}
