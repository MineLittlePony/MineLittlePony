package com.brohoof.minelittlepony.gui;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.PonyConfig;
import com.brohoof.minelittlepony.PonyLevel;
import com.brohoof.minelittlepony.Value;
import com.mumfrey.liteloader.core.LiteLoader;
import com.voxelmodpack.common.properties.VoxelProperty;
import com.voxelmodpack.common.properties.VoxelPropertyLabel;
import com.voxelmodpack.common.properties.gui.GuiVoxelBoxSettingsPanel;

import net.minecraft.client.resources.I18n;

public class MineLittlePonyGUI extends GuiVoxelBoxSettingsPanel {

    private static final String _PREFIX = "minelp.options.";
    private static final String TITLE = _PREFIX + "title";
    private static final String PONY_LEVEL = _PREFIX + "ponylevel";
    private static final String OPTIONS = _PREFIX + "options";
    private static final String HD = _PREFIX + "hd";
    private static final String SIZES = _PREFIX + "sizes";
    private static final String SNUZZLES = _PREFIX + "snuzzles";
    private static final String SHOW_SCALE = _PREFIX + "showscale";

    private PonyConfig config;

    public MineLittlePonyGUI() {
        this.config = MineLittlePony.getConfig();
        final byte col1 = 30;
        int row = PANEL_TOP;
        this.properties.add(new VoxelPropertyEnum<PonyLevel>(config.getPonyLevel(), PONY_LEVEL, PONY_LEVEL + ".",
                PANEL_LEFT, row += 24, PonyLevel.class));
        this.properties.add(new VoxelPropertyLabel(I18n.format(OPTIONS), PANEL_LEFT + 15, row += 45));
        this.properties.add(check(config.getHd(), HD, PANEL_LEFT + col1, row += 15));
        this.properties.add(check(config.getSizes(), SIZES, PANEL_LEFT + col1, row += 15));
        this.properties.add(check(config.getSnuzzles(), SNUZZLES, PANEL_LEFT + col1, row += 15));
        this.properties.add(check(config.getShowScale(), SHOW_SCALE, PANEL_LEFT + col1, row += 15));
    }

    private VoxelProperty<?> check(Value<Boolean> config, String text, int xPos, int yPos) {
        return new FakeVoxelPropertyToggleBox(config, text, xPos, yPos);
    }

    @Override
    public String getPanelTitle() {
        return I18n.format(TITLE);
    }

    @Override
    public void onGuiClosed() {
        LiteLoader.getInstance().writeConfig(config);
    }
}
