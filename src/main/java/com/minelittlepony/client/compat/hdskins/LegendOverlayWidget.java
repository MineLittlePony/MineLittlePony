package com.minelittlepony.client.compat.hdskins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import org.joml.Quaternionf;

import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.TValue;
import com.minelittlepony.common.client.gui.ITextContext;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.hdskins.client.gui.Carousel;
import com.minelittlepony.hdskins.client.gui.PlayerBodyWidget;

import java.util.List;
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
    public void render(DrawContext context, Bounds bounds, int mouseX, int mouseY, Quaternionf rotation) {
        PonyData data = Pony.getManager().getPony(player.get().playerState.skinTextures.body().texturePath()).metadata();
        int[] index = new int[1];
        data.attributes().forEach((key, value) -> {
            context.getMatrices().pushMatrix();
            int i = index[0]++;
            int x = frame.left;
            int y = frame.top + (i * 10 + 20);
            context.getMatrices().translate(x, y);
            drawLegendBlock(context, 0, 0, 0, mouseX - x, mouseY - y, key, value);
            context.getMatrices().popMatrix();
        });
    }

    private void drawLegendBlock(DrawContext context, int index, int x, int y, int mouseX, int mouseY, String key, TValue<?> value) {
        context.fill(0, 0, LEGEND_BLOCK_BOUNDS.width, LEGEND_BLOCK_BOUNDS.height, 0xFF003333);
        context.fill(1, 1, LEGEND_BLOCK_BOUNDS.width - 1, LEGEND_BLOCK_BOUNDS.height - 1, value.colorCode() | 0xFF000000);

        char symbol = value.name().charAt(0);
        if (symbol == '[') {
            symbol = key.charAt(0);
        }

        context.drawTextWithShadow(getFont(), Text.literal(String.valueOf(symbol).toUpperCase()), 2, 1, 0xFFFFFFFF);

        if (LEGEND_BLOCK_BOUNDS.contains(mouseX, mouseY)) {
            List<Text> lines = value.getOptions().stream().map(option -> {
                boolean selected = value.matches(option);
                return Text.literal((selected ? "* " : "  ") + option.name()).styled(s -> {
                    int color = option.getChannelAdjustedColorCode();
                    return (color == 0 ? s : s.withColor(color)).withItalic(selected);
                });
            }).collect(Collectors.toList());

            lines.add(0, Text.of(key.toUpperCase() + ": " + value.getHexValue()));
            if (lines.size() == 1) {
                lines.add(Text.literal(value.name()).styled(s -> {
                    int color = value.getChannelAdjustedColorCode();
                    return color == 0 ? s : s.withColor(value.colorCode());
                }));
            }
            context.drawTooltip(getFont(), lines, 2, 10);
        }
    }
}
