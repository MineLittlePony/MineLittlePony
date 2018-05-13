package com.minelittlepony.gui;

import java.io.IOException;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.pony.data.PonyLevel;
import com.mumfrey.liteloader.core.LiteLoader;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * In-Game options menu.
 *
 */
public class GuiPonySettings extends GuiScreen {

    private static final String OPTIONS_PREFIX = "minelp.options.";

    private static final String PONY_LEVEL = OPTIONS_PREFIX + "ponylevel";

    private static final String MOB_PREFIX = "minelp.mobs.";

    private PonyConfig config;

    public GuiPonySettings() {
        config = MineLittlePony.getConfig();
    }

    @Override
    public void initGui() {
        final int LEFT = width / 10;
        final int RIGHT = mustScroll() ? LEFT : width - width / 3 - 16;

        int row = mustScroll() ? 0 : 32;

        if (!mustScroll()) {
            addButton(new Label(width / 2, 12, getTitle(), -1, true));
        }

        addButton(new Label(LEFT, row += 15, PONY_LEVEL, -1));
        addButton(new Slider(LEFT, row += 15, 0, 2, config.getPonyLevel().ordinal(), (int id, String name, float value) -> {
            return I18n.format(PONY_LEVEL + "." + PonyLevel.valueFor(value).name().toLowerCase());
        }, v -> {
            PonyLevel level = PonyLevel.valueFor(v);
            config.setPonyLevel(level);
            return (float)level.ordinal();
        }));

        row += 15;
        addButton(new Label(LEFT, row += 15, OPTIONS_PREFIX + "options", -1));
        addButton(new Checkbox(LEFT, row += 15, OPTIONS_PREFIX + "hd",        config.hd,        v -> config.hd = v));
        addButton(new Checkbox(LEFT, row += 15, OPTIONS_PREFIX + "snuzzles",  config.snuzzles,  v -> config.snuzzles = v));
        addButton(new Checkbox(LEFT, row += 15, OPTIONS_PREFIX + "sizes",     config.sizes,     v -> config.sizes = v));
        addButton(new Checkbox(LEFT, row += 15, OPTIONS_PREFIX + "showscale", config.showscale, v -> config.showscale = v));

        if (mustScroll()) {
            row += 15;
        } else {
            row = 32;
        }

        addButton(new Label(RIGHT, row += 15, MOB_PREFIX + "title", -1));
        addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + "villagers",    config.villagers,  v -> config.villagers = v));
        addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + "zombies",      config.zombies,    v -> config.zombies = v));
        addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + "zombiepigmen", config.pigzombies, v -> config.pigzombies = v));
        addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + "skeletons",    config.skeletons,  v -> config.skeletons = v));
        addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + "illagers",     config.illagers,   v -> config.illagers = v));
        addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + "guardians",     config.guardians,   v -> config.guardians = v));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof IActionable) {
            ((IActionable)button).perform();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        LiteLoader.getInstance().writeConfig(config);
        MineLittlePony.getInstance().getRenderManager().initializeMobRenderers(mc.getRenderManager(), config);
    }

    protected String getTitle() {
        return OPTIONS_PREFIX + "title";
    }

    protected boolean mustScroll() {
        return false;
    }
}
