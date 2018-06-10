package com.minelittlepony.gui;

import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.render.ponies.MobRenderers;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.Setting;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * In-Game options menu.
 *
 */
public class GuiPonySettings extends GuiConfig {

    private static final String OPTIONS_PREFIX = "minelp.options.";

    private static final String PONY_LEVEL = OPTIONS_PREFIX + "ponylevel";

    private static final String MOB_PREFIX = "minelp.mobs.";

    public GuiPonySettings() {
        super(null, "minelittlepony", "Mine Little Pony");
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
        addButton(new Slider(LEFT, row += 15, 0, 2, PonyConfig.getPonyLevel().ordinal(), (int id, String name, float value) -> {
            return I18n.format(PONY_LEVEL + "." + PonyLevel.valueFor((int) value).name().toLowerCase());
        }, v -> {
            PonyLevel level = PonyLevel.valueFor((int) v);
            PonyConfig.setPonyLevel(level);
            return (float)level.ordinal();
        }));

        row += 15;
        addButton(new Label(LEFT, row += 15, OPTIONS_PREFIX + "options", -1));
        for (Setting i : PonyConfig.PonySettings.values()) {
            addButton(new Checkbox(LEFT, row += 15, OPTIONS_PREFIX + i.name().toLowerCase(), i.get(), i::set));
        }

        if (mustScroll()) {
            row += 15;
        } else {
            row = 32;
        }

        addButton(new Label(RIGHT, row += 15, MOB_PREFIX + "title", -1));
        for (MobRenderers i : MobRenderers.values()) {
            addButton(new Checkbox(RIGHT, row += 15, MOB_PREFIX + i.name().toLowerCase(), i.get(), i::set));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof IActionable) {
            ((IActionable)button).perform();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    protected String getTitle() {
        return OPTIONS_PREFIX + "title";
    }

    protected boolean mustScroll() {
        return false;
    }
}
