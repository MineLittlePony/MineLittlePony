package com.minelittlepony.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.Tooltip;
import com.minelittlepony.common.client.gui.element.AbstractSlider;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.EnumSlider;
import com.minelittlepony.common.client.gui.element.Label;
import com.minelittlepony.common.client.gui.element.Slider;
import com.minelittlepony.common.client.gui.element.Toggle;
import com.minelittlepony.common.util.settings.Setting;

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
        content.padding.left = 10;

        hiddenOptions = Screen.hasControlDown() && Screen.hasShiftDown();
    }

    @Override
    protected void init() {
        content.init(this::rebuildContent);
    }

    @SuppressWarnings("unchecked")
    private void rebuildContent() {

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

        content.addButton(new EnumSlider<>(LEFT, row += 20, config.ponyLevel.get())
                .onChange(config.ponyLevel::set)
                .setTextFormat(sender -> new TranslatableText(PONY_LEVEL + "." + sender.getValue().name().toLowerCase()))
                .setTooltipFormat(sender -> Tooltip.of(PONY_LEVEL + "." + sender.getValue().name().toLowerCase() + ".tooltip", 200)));

        boolean allowCameraChange = client.player == null || client.player.isCreative() || client.player.isSpectator() || client.isInSingleplayer();

        if (hiddenOptions && allowCameraChange) {
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.scale");
            content.addButton(new Slider(LEFT, row += 15, 0.1F, 3, config.getGlobalScaleFactor())
                    .onChange(config::setGlobalScaleFactor)
                    .setTextFormat(this::describeCurrentScale));
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.size");
            content.addButton(new EnumSlider<>(LEFT, row += 15, config.sizeOverride.get())
                    .onChange(config.sizeOverride::set));
        }

        row += 20;
        content.addButton(new Label(LEFT, row)).getStyle().setText(OPTIONS_PREFIX + "options");

        for (Setting<?> i : config.getByCategory("settings")) {
            boolean enabled = i != config.fillycam || allowCameraChange;
            Button button = content
                .addButton(new Toggle(LEFT, row += 20, ((Setting<Boolean>)i).get()))
                .onChange((Setting<Boolean>)i)
                .setEnabled(enabled);
            button.getStyle().setText(OPTIONS_PREFIX + i.name().toLowerCase());
            if (!enabled) {
                button.getStyle()
                    .setTooltip(new TranslatableText(OPTIONS_PREFIX + "option.disabled"))
                    .setTooltipOffset(0, 0);
            }
        }

        content.addButton(new Label(LEFT, row += 20)).getStyle().setText(OPTIONS_PREFIX + "button");
        content.addButton(new EnumSlider<>(LEFT, row += 20, config.horseButton.get())
                .onChange(config.horseButton::set)
                .setTooltipFormat(sender -> Tooltip.of(OPTIONS_PREFIX + "button." + sender.getValue().name().toLowerCase(), 200)));

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

        row += 15;

        content.addButton(new Label(RIGHT, row)).getStyle().setText("minelp.options.skins");
        SkinsProxy.instance.renderOption(this, row, RIGHT, content);
    }

    public TranslatableText describeCurrentScale(AbstractSlider<Float> sender) {
        float value = sender.getValue();
        if (value >= 3) {
            return new TranslatableText("minelp.debug.scale.meg");
        }
        if (value == 2) {
            return new TranslatableText("minelp.debug.scale.max");
        }
        if (value == 1) {
            return new TranslatableText("minelp.debug.scale.mid");
        }
        if (value == 0.9F) {
            return new TranslatableText("minelp.debug.scale.sa");
        }
        if (value <= 0.1F) {
            return new TranslatableText("minelp.debug.scale.min");
        }

        value *= 100F;
        value = Math.round(value);
        value /= 100F;

        return new TranslatableText("minelp.debug.scale.value", value);
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
