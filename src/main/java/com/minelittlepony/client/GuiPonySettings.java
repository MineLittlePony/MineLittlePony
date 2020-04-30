package com.minelittlepony.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.EnumSlider;
import com.minelittlepony.common.client.gui.element.Label;
import com.minelittlepony.common.client.gui.element.Slider;
import com.minelittlepony.common.client.gui.element.Toggle;
import com.minelittlepony.common.util.settings.Setting;
import com.minelittlepony.settings.PonyLevel;

import javax.annotation.Nullable;

/**
 * In-Game options menu.
 *
 */
public class GuiPonySettings extends GameGui {

    private static final String OPTIONS_PREFIX = "minelp.options.";

    private static final String PONY_LEVEL = OPTIONS_PREFIX + "ponylevel";

    private static final String MOB_PREFIX = "minelp.mobs.";

    private ClientPonyConfig config;

    private final ScrollContainer content = new ScrollContainer();

    private final boolean hiddenOptions;

    public GuiPonySettings(@Nullable Screen parent) {
        super(new LiteralText(OPTIONS_PREFIX + "title"), parent);

        config = (ClientPonyConfig)MineLittlePony.getInstance().getConfig();

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

    @SuppressWarnings("unchecked")
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
            .onClick(sender -> finish())
            .getStyle()
                .setText("gui.done");

        content.addButton(new Label(LEFT, row)).getStyle().setText(PONY_LEVEL);
        content.addButton(new Slider(LEFT, row += 20, 0, 2, config.ponyLevel.get().ordinal())
                .onChange(v -> {
                    PonyLevel level = PonyLevel.valueFor(v);
                    config.ponyLevel.set(level);
                    return (float)level.ordinal();
                })
                .setFormatter(value -> I18n.translate(PONY_LEVEL + "." + PonyLevel.valueFor(value).name().toLowerCase())));

        if (hiddenOptions) {
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.scale");
            content.addButton(new Slider(LEFT, row += 15, 0.1F, 3, config.getGlobalScaleFactor())
                    .onChange(config::setGlobalScaleFactor)
                    .setFormatter(this::describeCurrentScale));
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.size");
            content.addButton(new EnumSlider<>(LEFT, row += 15, config.sizeOverride.get())
                    .onChange(config.sizeOverride::set));
        }

        row += 20;
        content.addButton(new Label(LEFT, row)).getStyle().setText(OPTIONS_PREFIX + "options");

        for (Setting<?> i : MineLittlePony.getInstance().getConfig().getByCategory("settings")) {
            content.addButton(new Toggle(LEFT, row += 20, ((Setting<Boolean>)i).get()))
                .onChange((Setting<Boolean>)i)
                .getStyle().setText(OPTIONS_PREFIX + i.name().toLowerCase());
        }

        content.addButton(new Label(LEFT, row += 20)).getStyle().setText(OPTIONS_PREFIX + "button");

        content.addButton(new EnumSlider<>(LEFT, row += 20, config.horseButton.get())
                .onChange(config.horseButton::set));

        if (RIGHT != LEFT) {
            row = 0;
        } else {
            row += 15;
        }

        content.addButton(new Label(RIGHT, row)).getStyle().setText(MOB_PREFIX + "title");
        for (MobRenderers i : MobRenderers.REGISTRY.values()) {
            content.addButton(new Toggle(RIGHT, row += 20, i.get()))
                .onChange(i::set)
                .getStyle().setText(MOB_PREFIX + i.name);
        }
    }

    public String describeCurrentScale(float value) {
        if (value >= 3) {
            return "minelp.debug.scale.meg";
        }
        if (value == 2) {
            return "minelp.debug.scale.max";
        }
        if (value == 1) {
            return "minelp.debug.scale.mid";
        }
        if (value == 0.9F) {
            return "minelp.debug.scale.sa";
        }
        if (value <= 0.1F) {
            return "minelp.debug.scale.min";
        }

        value *= 100F;
        value = Math.round(value);
        value /= 100F;

        return I18n.translate("minelp.debug.scale.value", value);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, partialTicks);
        content.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void removed() {
        config.save();
    }
}
