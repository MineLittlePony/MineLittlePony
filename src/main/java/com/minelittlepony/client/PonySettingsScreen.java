package com.minelittlepony.client;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.render.MobRenderers;
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

/**
 * In-Game options menu.
 *
 */
public class PonySettingsScreen extends GameGui {
    private static final String OPTIONS_PREFIX = "minelp.options.";
    private static final String PONY_LEVEL = OPTIONS_PREFIX + "ponylevel";
    private static final String MOB_PREFIX = "minelp.mobs.";

    public static final Component SCALE_MEGA = Component.translatable("minelp.debug.scale.meg");
    public static final Component SCALE_MAX = Component.translatable("minelp.debug.scale.max");
    public static final Component SCALE_MID = Component.translatable("minelp.debug.scale.mid");
    public static final Component SCALE_SHOW = Component.translatable("minelp.debug.scale.sa");
    public static final Component SCALE_MIN = Component.translatable("minelp.debug.scale.min");

    public static HorseButtonFactory buttonFactory = (_, _, row, RIGHT, content) -> {
        content.addButton(new Button(RIGHT, row += 20, 150, 20))
            .setEnabled(false)
            .getStyle()
                .setTooltip(Tooltip.of("minelp.options.skins.hdskins.disabled", 200))
                .setText("minelp.options.skins.hdskins.open");
    };

    private final PonyConfig config = PonyConfig.getInstance();

    private final ScrollContainer content = new ScrollContainer();

    private final boolean hiddenOptions;

    public PonySettingsScreen(@Nullable Screen parent) {
        super(Component.literal(OPTIONS_PREFIX + "title"), parent);
        content.margin.top = 30;
        content.margin.bottom = 30;
        content.getContentPadding().top = 10;
        content.getContentPadding().right = 10;
        content.getContentPadding().bottom = 20;
        content.getContentPadding().left = 10;

        hiddenOptions = Minecraft.getInstance().hasControlDown() && Minecraft.getInstance().hasShiftDown();
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

        getChildElements().add(content);

        addButton(new Label(width / 2, 5).setCentered()).getStyle().setText(getTitle().getString());
        addButton(new Button(width / 2 - 100, height - 25))
            .onClick(_ -> finish())
            .getStyle()
                .setText("gui.done");

        content.addButton(new Label(LEFT, row)).getStyle().setText(PONY_LEVEL);

        content.addButton(new EnumSlider<>(LEFT, row += 20, config.ponyLevel.get())
                .onChange(config.ponyLevel::set)
                .setTextFormat(sender -> Component.translatable(PONY_LEVEL + "." + sender.getValue().name().toLowerCase()))
                .setTooltipFormat(sender -> Tooltip.of(PONY_LEVEL + "." + sender.getValue().name().toLowerCase() + ".tooltip", 200)));

        boolean allowCameraChange = minecraft.player == null || minecraft.player.isCreative() || minecraft.player.isSpectator() || minecraft.isSingleplayer();

        if (hiddenOptions && allowCameraChange) {
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.scale");
            content.addButton(new Slider(LEFT, row += 15, 0.1F, 3, config.getGlobalScaleFactor())
                    .onChange(config::setGlobalScaleFactor)
                    .setTextFormat(this::describeCurrentScale));
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.size");
            content.addButton(new EnumSlider<>(LEFT, row += 15, config.sizeOverride.get())
                    .onChange(config.sizeOverride::set));
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.race");
            content.addButton(new EnumSlider<>(LEFT, row += 15, config.raceOverride.get())
                    .onChange(config.raceOverride::set));
            content.addButton(new Label(LEFT, row += 30)).getStyle().setText("minelp.debug.armour");
            content.addButton(new Toggle(LEFT, row += 15, config.disablePonifiedArmour.get())
                    .onChange(config.disablePonifiedArmour::set));
        }

        row += 20;
        content.addButton(new Label(LEFT, row)).getStyle().setText(OPTIONS_PREFIX + "options");

        for (Setting<?> i : config.getCategory("settings").entries()) {
            boolean enabled = i == config.disablebucketfix ? config.fillycam.get() : i != config.fillycam || allowCameraChange;
            Button button = content
                .addButton(new Toggle(LEFT, row += 20, ((Setting<Boolean>)i).get()))
                .onChange(i == config.horsieMode ? (v -> {
                    v = ((Setting<Boolean>)i).set(v);

                    MineLittlePony.getInstance().getRenderDispatcher().initialise(minecraft.getEntityRenderDispatcher(), true);
                    return v;
                }) : i == config.fillycam ? (v -> {
                    v = ((Setting<Boolean>)i).set(v);
                    rebuildWidgets();
                    return v;
                }): (Setting<Boolean>)i)
                .setEnabled(enabled);
            button.getStyle().setText(OPTIONS_PREFIX + i.name().toLowerCase());
            if (!enabled) {
                button.getStyle()
                    .setTooltip(Component.translatable(OPTIONS_PREFIX + "option.disabled"))
                    .setTooltipOffset(0, 0);
            } else if (i == config.disablebucketfix) {
                button.getStyle()
                    .setTooltip(Component.translatable(OPTIONS_PREFIX + i.name().toLowerCase() + ".tooltip"))
                    .setTooltipOffset(0, 0);
            }
        }

        if (hiddenOptions) {
            for (Setting<?> i : config.getCategory("customisation").entries()) {
                if (i.get() instanceof Boolean value) {
                    content.addButton(new Toggle(LEFT, row += 20, value))
                        .onChange((Setting<Boolean>)i)
                        .getStyle().setText(OPTIONS_PREFIX + i.name().toLowerCase());
                }
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
            content.addButton(new Toggle(RIGHT, row += 20, i.option()))
                .onChange(i.option())
                .getStyle().setText(MOB_PREFIX + i.name());
        }

        row += 15;

        content.addButton(new Label(RIGHT, row)).getStyle().setText("minelp.options.skins");
        buttonFactory.renderOption(this, parent, row, RIGHT, content);
    }

    public Component describeCurrentScale(AbstractSlider<Float> sender) {
        float value = sender.getValue();
        if (value >= 3) {
            return SCALE_MEGA;
        }
        if (value == 2) {
            return SCALE_MAX;
        }
        if (value == 1) {
            return SCALE_MID;
        }
        if (value == 0.9F) {
            return SCALE_SHOW;
        }
        if (value <= 0.1F) {
            return SCALE_MIN;
        }

        value *= 100F;
        value = Math.round(value);
        value /= 100F;

        return Component.translatable("minelp.debug.scale.value", value);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float tickDelta) {
        super.extractRenderState(context, mouseX, mouseY, tickDelta);
        content.extractRenderState(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public void removed() {
        config.save();
    }

    public interface HorseButtonFactory {
        void renderOption(Screen screen, @Nullable Screen parent, int row, int RIGHT, ScrollContainer content);
    }
}
