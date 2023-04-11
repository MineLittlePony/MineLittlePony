package com.minelittlepony.client.hdskins;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.TriggerPixel;
import com.minelittlepony.api.pony.meta.Wearable;
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
    @Override
    protected DummyPlayer createEntity(ClientWorld world, PlayerSkins<?> textures) {
        return new DummyPony(world, textures);
    }

    @Override
    public Identifier getDefaultSkin(SkinType type, boolean slim) {
        if (type == MineLPHDSkins.seaponySkinType) {
            return DefaultSkinGenerator.generateGreyScale(SeaponyRenderer.TEXTURE, SeaponyRenderer.TEXTURE, getExclusion());
        }

        Wearable wearable = MineLPHDSkins.wearableTypes.getOrDefault(type, Wearable.NONE);

        if (wearable != Wearable.NONE) {
            return DefaultSkinGenerator.generateGreyScale(wearable.getDefaultTexture(), wearable.getDefaultTexture(), getExclusion());
        }

        Identifier skin = getBlankSkin(type, slim);
        return DefaultSkinGenerator.generateGreyScale(type == SkinType.SKIN ? DefaultPonySkinHelper.getPonySkin(profile.getId(), slim) : skin, skin, getExclusion());
    }

    @Override
    protected TextureLoader.Exclusion getExclusion() {
        return TriggerPixel::isTriggerPixelCoord;
    }

    @Override
    public void renderWorldAndPlayer(Optional<DummyPlayer> thePlayer,
            Bounds frame,
            int horizon, int mouseX, int mouseY, int ticks, float partialTick, float scale,
            MatrixStack matrices, @Nullable Consumer<DummyPlayer> postAction) {
        super.renderWorldAndPlayer(thePlayer, frame, horizon, mouseX, mouseY, ticks, partialTick, scale, matrices, postAction);
        thePlayer.ifPresent(p -> {
            IPonyData data = IPony.getManager().getPony(p).metadata();
            int[] index = new int[1];
            data.getTriggerPixels().forEach((key, value) -> {
                drawLegendBlock(matrices, index[0]++, frame.left, frame.top, mouseX, mouseY, key, value);
            });
        });
    }

    private void drawLegendBlock(MatrixStack matrices, int index, int x, int y, int mouseX, int mouseY, String key, TriggerPixelType<?> value) {
        int size = 10;
        int yPos = y + index * size + 20;
        fill(matrices,
                x,        yPos,
                x + size, yPos + size,
                0xFF003333
        );
        fill(matrices,
                x + 1,        yPos + 1,
                x - 1 + size, yPos - 1 + size,
                value.getColorCode() | 0xFF000000
        );

        char symbol = value.name().charAt(0);
        if (symbol == '[') {
            symbol = key.charAt(0);
        }

        minecraft.textRenderer.drawWithShadow(matrices,
               Text.of(String.valueOf(symbol).toUpperCase()),
               x + 2,
               yPos + 1,
               0xFFFFFFFF
        );

        if (mouseX > x && mouseX < (x + size) && mouseY > yPos && mouseY < (yPos + size)) {

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

            minecraft.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }
}
