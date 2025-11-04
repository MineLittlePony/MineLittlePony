package com.minelittlepony.client.render;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.MineLittlePony;

import java.util.Map;

public class PonyEntityRenderersDebugEntry implements DebugHudEntry {
    public static final Identifier ID = MineLittlePony.id("entity_renderers");

    @Override
    public void render(DebugHudLines lines, World world, WorldChunk clientChunk, WorldChunk chunk) {

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        lines.addLineToSection(ID, Formatting.YELLOW + "Mine Little Pony (" + FabricLoader.getInstance().getModContainer("minelp").get().getMetadata().getVersion() + ")");
        lines.addLineToSection(ID, " HDSkins Present: " + boolString(FabricLoader.getInstance().getModContainer("hdskins").isPresent()));
        lines.addLineToSection(ID, " Pony Level: " + Formatting.YELLOW + PonyConfig.getInstance().ponyLevel.get());
        lines.addLineToSection(ID, " Show Scale: "
                + boolString(PonyConfig.getInstance().showscale.get())
                + (PonyConfig.getInstance().showscale.get() ? " (" + PonyConfig.getInstance().getGlobalScaleFactor() + ")" : ""));
        if (player == null) {
            lines.addLineToSection(ID, " Filly Cam: " + boolString(PonyConfig.getInstance().fillycam.get()));
        } else {
            Pony playerPony = MineLittlePony.getInstance().getManager().getPony(player);
            float cameraDistance = player.getScale() * (float)player.getAttributeValue(EntityAttributes.CAMERA_DISTANCE) * playerPony.size().eyeDistanceFactor();

            lines.addLineToSection(ID, " Filly Cam: " + boolString(PonyConfig.getInstance().fillycam.get())
                    + ", EH/F: " + Formatting.YELLOW + player.getStandingEyeHeight() + "/" + playerPony.size().eyeHeightFactor() + Formatting.RESET
                    + ", ED/F: " + Formatting.YELLOW + cameraDistance + "/" + playerPony.size().eyeDistanceFactor());
            lines.addLineToSection(ID, "");
            lines.addLineToSection(ID, Formatting.UNDERLINE + "Current Player Skin: ");
            lines.addLinesToSection(ID, playerPony.metadata().attributes().entrySet().stream().map(entry -> {
                return entry.getKey() + "=" + Formatting.AQUA + entry.getValue().name() + Formatting.RESET + " (" + Formatting.YELLOW + '#' + Integer.toString(entry.getValue().colorCode(), 16) + Formatting.RESET + ")";
            }).toList());
        }
        lines.addLineToSection(ID, "");
        lines.addLineToSection(ID, Formatting.UNDERLINE + "MineLP Debug Options: ");
        PonyConfig.getInstance().getCategory("debug").forEach(entry -> {
            lines.addLineToSection(ID, "debug/" + entry.getKey() + ": " + Formatting.AQUA + String.valueOf(entry.getValue().get()));
        });
        MobRenderers.REGISTRY.entrySet().stream()
                .filter(e -> e.getValue().option().get())
                .map(Map.Entry::getKey);

    }

    private String boolString(boolean on) {
        return (on ? Formatting.GREEN : Formatting.RED) + String.valueOf(on) + Formatting.RESET;
    }
}
