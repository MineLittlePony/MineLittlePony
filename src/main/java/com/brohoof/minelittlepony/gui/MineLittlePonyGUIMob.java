package com.brohoof.minelittlepony.gui;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.PonyConfig;
import com.brohoof.minelittlepony.Value;
import com.mumfrey.liteloader.core.LiteLoader;
import com.voxelmodpack.common.properties.VoxelProperty;
import com.voxelmodpack.common.properties.VoxelPropertyLabel;
import com.voxelmodpack.common.properties.gui.GuiVoxelBoxSettingsPanel;

import net.minecraft.client.resources.I18n;

public class MineLittlePonyGUIMob extends GuiVoxelBoxSettingsPanel {

    private static final String pref = "minelp.mobs.";

    private static final String restart1 = "minelp.restart1";
    private static final String restart2 = "minelp.restart2";

    private static final String title = pref + "title";
    private static final String villagers = pref + "villagers";
    private static final String zombies = pref + "zombies";
    private static final String zombiePigmen = pref + "zombiepigmen";
    private static final String skeletons = pref + "skeletons";

    private PonyConfig config;

    public MineLittlePonyGUIMob() {
        this.config = MineLittlePony.getConfig();
        byte col1 = 30;
        this.properties.add(new VoxelPropertyLabel(I18n.format(restart1), PANEL_LEFT + 15, PANEL_TOP + 11, 0xff6666));
        this.properties.add(new VoxelPropertyLabel(I18n.format(restart2), PANEL_LEFT + 15, PANEL_TOP + 23, 0xff6666));
        this.properties.add(check(config.getVillagers(), villagers, PANEL_LEFT + col1, PANEL_TOP + 42));
        this.properties.add(check(config.getZombies(), zombies, PANEL_LEFT + col1, PANEL_TOP + 60));
        this.properties.add(check(config.getPigZombies(), zombiePigmen, PANEL_LEFT + col1, PANEL_TOP + 78));
        this.properties.add(check(config.getSkeletons(), skeletons, PANEL_LEFT + col1, PANEL_TOP + 96));
    }

    private VoxelProperty<?> check(Value<Boolean> config, String text, int xPos, int yPos) {
        return new FakeVoxelPropertyToggleBox(config, text, xPos, yPos);
    }

    @Override
    public String getPanelTitle() {
        return I18n.format(title);
    }

    @Override
    public void onGuiClosed() {
        LiteLoader.getInstance().writeConfig(config);
    }
}
