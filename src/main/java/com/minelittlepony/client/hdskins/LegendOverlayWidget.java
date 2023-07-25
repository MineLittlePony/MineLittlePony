package com.minelittlepony.client.hdskins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import com.minelittlepony.api.pony.*;
import com.minelittlepony.common.client.gui.ITextContext;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.hdskins.client.gui.Carousel;
import com.minelittlepony.hdskins.client.gui.player.DummyPlayer;

import java.util.List;
import java.util.stream.Collectors;

class LegendOverlayWidget implements Carousel.Element, ITextContext {
    private static final Bounds LEGEND_BLOCK_BOUNDS = new Bounds(0, 0, 10, 10);

    private final Bounds frame;

    public LegendOverlayWidget(Bounds frame) {
        this.frame = frame;
    }

    @Override
    public void render(DummyPlayer player, MatrixStack matrices, int mouseX, int mouseY) {
        IPonyData data = IPony.getManager().getPony(player).metadata();
        int[] index = new int[1];
        data.getTriggerPixels().forEach((key, value) -> {
            matrices.push();
            int i = index[0]++;
            int x = frame.left;
            int y = frame.top + (i * 10 + 20);
            matrices.translate(x, y, 1);
            drawLegendBlock(matrices, 0, 0, 0, mouseX - x, mouseY - y, key, value);
            matrices.pop();
        });
    }

    private void drawLegendBlock(MatrixStack matrices, int index, int x, int y, int mouseX, int mouseY, String key, TriggerPixelType<?> value) {
        DrawableHelper.fill(matrices, 0, 0, LEGEND_BLOCK_BOUNDS.width, LEGEND_BLOCK_BOUNDS.height, 0xFF003333);
        DrawableHelper.fill(matrices, 1, 1, LEGEND_BLOCK_BOUNDS.width - 1, LEGEND_BLOCK_BOUNDS.height - 1, value.getColorCode() | 0xFF000000);

        char symbol = value.name().charAt(0);
        if (symbol == '[') {
            symbol = key.charAt(0);
        }

        DrawableHelper.drawTextWithShadow(matrices, getFont(), Text.literal(String.valueOf(symbol).toUpperCase()), 2, 1, 0xFFFFFFFF);

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
                    return color == 0 ? s : s.withColor(value.getColorCode());
                }));
            }

            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, lines, 2, 10);
        }
    }
}
