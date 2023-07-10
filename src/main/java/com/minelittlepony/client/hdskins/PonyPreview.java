package com.minelittlepony.client.hdskins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.render.entity.SeaponyRenderer;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.hdskins.client.dummy.*;
import com.minelittlepony.hdskins.client.resources.DefaultSkinGenerator;
import com.minelittlepony.hdskins.client.resources.TextureLoader;
import com.minelittlepony.hdskins.profile.SkinType;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class PonyPreview extends PlayerPreview {
    private static final Bounds LEGEND_BLOCK_BOUNDS = new Bounds(0, 0, 10, 10);

    @Override
    protected DummyPlayer createEntity(ClientWorld world, PlayerSkins<?> textures) {
        return new DummyPony(world, textures);
    }

    @Override
    public Identifier getDefaultSkin(SkinType type, boolean slim) {
        if (type == MineLPHDSkins.seaponySkinType) {
            return DefaultSkinGenerator.generateGreyScale(SeaponyRenderer.SEAPONY, SeaponyRenderer.SEAPONY, getExclusion());
        }

        Wearable wearable = MineLPHDSkins.wearableTypes.getOrDefault(type, Wearable.NONE);

        if (wearable != Wearable.NONE) {
            return DefaultSkinGenerator.generateGreyScale(wearable.getDefaultTexture(), wearable.getDefaultTexture(), getExclusion());
        }

        return super.getDefaultSkin(type, slim);
    }

    @Override
    protected TextureLoader.Exclusion getExclusion() {
        return TriggerPixel::isTriggerPixelCoord;
    }

    @Override
    public void renderWorldAndPlayer(Optional<DummyPlayer> thePlayer,
            Bounds frame,
            int horizon, int mouseX, int mouseY, int ticks, float partialTick, float scale,
            DrawContext context, @Nullable Consumer<DummyPlayer> postAction) {
        super.renderWorldAndPlayer(thePlayer, frame, horizon, mouseX, mouseY, ticks, partialTick, scale, context, postAction);
        thePlayer.ifPresent(p -> {
            IPonyData data = IPony.getManager().getPony(p).metadata();
            int[] index = new int[1];
            data.getTriggerPixels().forEach((key, value) -> {
                context.getMatrices().push();
                int i = index[0]++;
                int x = frame.left;
                int y = frame.top + (i * 10 + 20);
                context.getMatrices().translate(x, y, 1);
                drawLegendBlock(context, 0, 0, 0, mouseX - x, mouseY - y, key, value);
                context.getMatrices().pop();
            });
            MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().draw();
        });
    }

    private void drawLegendBlock(DrawContext context, int index, int x, int y, int mouseX, int mouseY, String key, TriggerPixelType<?> value) {
        context.fill(0, 0, LEGEND_BLOCK_BOUNDS.width, LEGEND_BLOCK_BOUNDS.height, 0xFF003333);
        context.fill(1, 1, LEGEND_BLOCK_BOUNDS.width - 1, LEGEND_BLOCK_BOUNDS.height - 1, value.getColorCode() | 0xFF000000);

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
                    return color == 0 ? s : s.withColor(value.getColorCode());
                }));
            }
            context.drawTooltip(getFont(), lines, 2, 10);
        }
    }
}
