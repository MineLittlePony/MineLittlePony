package com.minelittlepony.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entities.MobRenderers;
import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.Label;
import com.minelittlepony.common.client.gui.element.Slider;
import com.minelittlepony.common.client.gui.element.Toggle;
import com.minelittlepony.pony.meta.Size;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import com.minelittlepony.settings.PonySettings;

/**
 * In-Game options menu.
 *
 */
public class GuiPonySettings extends GameGui {

    private static final String OPTIONS_PREFIX = "minelp.options.";

    private static final String PONY_LEVEL = OPTIONS_PREFIX + "ponylevel";

    private static final String MOB_PREFIX = "minelp.mobs.";

    private PonyConfig config;

    private final ScrollContainer content = new ScrollContainer();

    private final boolean hiddenOptions;

    public GuiPonySettings() {
        super(new LiteralText(OPTIONS_PREFIX + "title"));

        config = MineLittlePony.getInstance().getConfig();

        content.margin.top = 30;
        content.margin.bottom = 30;
        content.padding.top = 10;
        content.padding.right = 10;
        content.padding.bottom = 20;

        hiddenOptions = Screen.hasControlDown() && Screen.hasShiftDown();
    }

    @Override
    protected void init() {
        content.init(this::rebuildContent);
    }

    private void rebuildContent() {
        content.padding.left = 10;

        int LEFT = content.width / 2 - 210;
        int RIGHT = content.width / 2 + 10;

        if (LEFT < 0) {
            LEFT = content.width / 2 - 100;
            RIGHT = LEFT;
        }

        int row = 0;

        children().add(content);

        addButton(new Label(width / 2, 5).setCentered()).getStyle().setText(getTitle().getString());
        addButton(new Button(width / 2 - 100, height - 25))
            .onClick(sender -> onClose())
            .getStyle()
                .setText("gui.done");

        content.addButton(new Label(LEFT, row)).getStyle().setText(PONY_LEVEL);
        content.addButton(new Slider(LEFT, row += 20, 0, 2, config.getPonyLevel().ordinal())
                .onChange(v -> {
                    PonyLevel level = PonyLevel.valueFor(v);
                    config.setPonyLevel(level);
                    return (float)level.ordinal();
                })
                .setFormatter(value -> I18n.translate(PONY_LEVEL + "." + PonyLevel.valueFor(value).name().toLowerCase())));

        if (hiddenOptions) {
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.scale");
            content.addButton(new Slider(LEFT, row += 15, 0.1F, 3, config.getGlobalScaleFactor())
                    .onChange(config::setGlobalScaleFactor)
                    .setFormatter(value -> I18n.translate("minelp.debug.scale.value", I18n.translate(describeCurrentScale(value)))));
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.size");
            content.addButton(new Slider(LEFT, row += 15, 0, Size.REGISTRY.length - 1, config.getOverrideSize().ordinal())
                    .onChange(config::setSizeOverride)
                    .setFormatter(value -> value < Size.REGISTRY.length ? Size.REGISTRY[(int)(float)value].name() : "Unset"));
        }

        row += 20;
        content.addButton(new Label(LEFT, row)).getStyle().setText(OPTIONS_PREFIX + "options");
        for (PonySettings i : PonySettings.values()) {
            content.addButton(new Toggle(LEFT, row += 20, i.get()))
                .onChange(i)
                .getStyle().setText(OPTIONS_PREFIX + i.name().toLowerCase());
        }

        if (RIGHT != LEFT) {
            row = 0;
        } else {
            row += 15;
        }

        content.addButton(new Label(RIGHT, row)).getStyle().setText(MOB_PREFIX + "title");
        for (MobRenderers i : MobRenderers.registry) {
            content.addButton(new Toggle(RIGHT, row += 20, i.get()))
                .onChange(i)
                .getStyle().setText(MOB_PREFIX + i.name().toLowerCase());
        }
    }

    public String describeCurrentScale(float value) {
        if (value >= 3) {
            return I18n.translate("minelp.debug.scale.meg");
        }
        if (value == 2) {
            return I18n.translate("minelp.debug.scale.max");
        }
        if (value == 1) {
            return I18n.translate("minelp.debug.scale.mid");
        }
        if (value == 0.9F) {
            return I18n.translate("minelp.debug.scale.sa");
        }
        if (value <= 0.1F) {
            return I18n.translate("minelp.debug.scale.min");
        }
        return String.format("%f", value);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        content.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        super.onClose();
        config.save();
    }
}
