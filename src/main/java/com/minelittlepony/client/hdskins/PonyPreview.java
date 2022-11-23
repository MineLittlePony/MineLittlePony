package com.minelittlepony.client.hdskins;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.dummy.PlayerPreview;
import com.minelittlepony.hdskins.client.dummy.TextureProxy;
import com.minelittlepony.hdskins.profile.SkinType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class PonyPreview extends PlayerPreview {
    public static final Identifier NO_SKIN_STEVE_PONY = new Identifier("minelittlepony", "textures/mob/noskin.png");
    public static final Identifier NO_SKIN_ALEX_PONY = new Identifier("minelittlepony", "textures/mob/noskin_alex.png");
    public static final Identifier NO_SKIN_SEAPONY = new Identifier("minelittlepony", "textures/mob/noskin_seapony.png");

    @Override
    protected DummyPlayer createEntity(ClientWorld world, TextureProxy textures) {
        return new DummyPony(world, textures);
    }

    @Override
    public Identifier getBlankSteveSkin(SkinType type) {
        if (type == SkinType.SKIN) {
            return NO_SKIN_STEVE_PONY;
        }
        if (type == MineLPHDSkins.seaponySkinType) {
            return NO_SKIN_SEAPONY;
        }

        return super.getBlankSteveSkin(type);
    }

    @Override
    public Identifier getBlankAlexSkin(SkinType type) {
        if (type == SkinType.SKIN) {
            return NO_SKIN_ALEX_PONY;
        }
        if (type == MineLPHDSkins.seaponySkinType) {
            return NO_SKIN_SEAPONY;
        }

        return super.getBlankAlexSkin(type);
    }

    @Override
    public void renderWorldAndPlayer(Optional<DummyPlayer> thePlayer,
            int frameLeft, int frameRight, int frameBottom, int frameTop,
            float xPos, float yPos, int horizon, int mouseX, int mouseY, int ticks, float partialTick, float scale,
            MatrixStack matrices) {
        super.renderWorldAndPlayer(thePlayer, frameLeft, frameRight, frameBottom, frameTop, xPos, yPos, horizon, mouseX, mouseY, ticks, partialTick, scale, matrices);
        thePlayer.ifPresent(p -> {
            IPonyData data = MineLittlePony.getInstance().getManager().getPony(p).getMetadata();
            int[] index = new int[1];
            data.getTriggerPixels().forEach((key, value) -> {
                drawLegendBlock(matrices, index[0]++, frameLeft, frameTop, mouseX, mouseY, key, value);
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
