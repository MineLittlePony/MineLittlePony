package com.minelittlepony.client.compat.hdskins;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;

import org.joml.Quaternionf;

import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.TValue;
import com.minelittlepony.common.client.gui.ITextContext;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.hdskins.client.gui.Carousel;
import com.minelittlepony.hdskins.client.gui.PlayerBodyWidget;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class LegendOverlayWidget implements Carousel.Element, ITextContext {
    private static final Bounds LEGEND_BLOCK_BOUNDS = new Bounds(0, 0, 10, 10);

    private final Bounds frame;

    private final Supplier<PlayerBodyWidget<?>> player;

    public LegendOverlayWidget(Bounds frame, Supplier<PlayerBodyWidget<?>> player) {
        this.frame = frame;
        this.player = player;
    }

    @Override
    public void tick() {

    }

    @Override
    public void updateState(float xPosition, float yPosition, float mouseX, float mouseY, float tickDelta) {

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, Bounds bounds, int mouseX, int mouseY, Quaternionf rotation) {
        PonyData data = Pony.getManager().getPony(player.get().playerState.skin.body().texturePath()).metadata();
        int[] index = new int[1];
        data.attributes().forEach((key, value) -> {
            context.pose().pushMatrix();
            int i = index[0]++;
            int x = frame.left;
            int y = frame.top + (i * 10 + 20);
            context.pose().translate(x, y);
            drawLegendBlock(context, i, x, y, mouseX - x, mouseY - y, key, value);
            context.pose().popMatrix();
        });
    }

    private void drawLegendBlock(GuiGraphicsExtractor context, int index, int x, int y, int mouseX, int mouseY, String key, TValue<?> value) {
        context.fill(0, 0, LEGEND_BLOCK_BOUNDS.width, LEGEND_BLOCK_BOUNDS.height, 0xFF003333);
        context.fill(1, 1, LEGEND_BLOCK_BOUNDS.width - 1, LEGEND_BLOCK_BOUNDS.height - 1, ARGB.color(1F, value.colorCode()));

        char symbol = value.name().charAt(0);
        if (symbol == '[') {
            symbol = key.charAt(0);
        }

        context.text(getFont(), Component.literal(String.valueOf(symbol).toUpperCase()), 2, 1, CommonColors.WHITE);

        if (LEGEND_BLOCK_BOUNDS.contains(mouseX, mouseY)) {
            List<Component> lines = value.getOptions().stream().map(option -> {
                boolean selected = value.matches(option);
                return Component.literal((selected ? "* " : "  ") + option.name()).withStyle(s -> {
                    int color = option.getChannelAdjustedColorCode();
                    return (color == 0 ? s : s.withColor(color)).withItalic(selected);
                });
            }).collect(Collectors.toList());

            lines.add(0, Component.literal(key.toUpperCase() + ": " + value.getHexValue()));
            if (lines.size() == 1) {
                lines.add(Component.literal(value.name()).withStyle(s -> {
                    int color = value.getChannelAdjustedColorCode();
                    return color == 0 ? s : s.withColor(value.colorCode());
                }));
            }
            context.setTooltipForNextFrame(getFont(), lines, Optional.empty(), x + mouseX + 2, y + mouseY + 10);
        }
    }
}
