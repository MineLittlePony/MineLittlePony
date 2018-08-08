package com.minelittlepony.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.PonyConfig.PonySettings;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.render.ponies.MobRenderers;
import com.mumfrey.liteloader.core.LiteLoader;

/**
 * In-Game options menu.
 *
 */
public class GuiPonySettings extends SettingsPanel {

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
            return format(PONY_LEVEL + "." + PonyLevel.valueFor(value).name().toLowerCase());
        }, v -> {
            PonyLevel level = PonyLevel.valueFor(v);
            config.setPonyLevel(level);
            return (float)level.ordinal();
        }));

        row += 15;
        addButton(new Label(LEFT, row += 15, OPTIONS_PREFIX + "options", -1));
        for (PonySettings i : PonySettings.values()) {
            addButton(new Checkbox(LEFT, row += 15, OPTIONS_PREFIX + i.name().toLowerCase(), i.get(), i));
        }

        if (mustScroll()) {
            row += 15;
        } else {
            row = 32;
        }

        addButton(new Label(RIGHT, row += 15, MOB_PREFIX + "title", -1));
        for (MobRenderers i : MobRenderers.values()) {
            addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + i.name().toLowerCase(), i.get(), i));
        }
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawContents(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        LiteLoader.getInstance().writeConfig(config);
    }

    protected String getTitle() {
        return OPTIONS_PREFIX + "title";
    }
}
