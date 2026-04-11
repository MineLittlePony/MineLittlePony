package com.minelittlepony.client.render;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.MineLittlePony;

import java.util.Map;

public class PonyEntityRenderersDebugEntry implements DebugScreenEntry {
    public static final Identifier ID = MineLittlePony.id("entity_renderers");

    @Override
    public void display(DebugScreenDisplayer lines, Level world, LevelChunk clientChunk, LevelChunk chunk) {

        LocalPlayer player = Minecraft.getInstance().player;

        lines.addToGroup(ID, ChatFormatting.YELLOW + "Mine Little Pony (" + FabricLoader.getInstance().getModContainer("minelp").get().getMetadata().getVersion() + ")");
        lines.addToGroup(ID, " HDSkins Present: " + boolString(FabricLoader.getInstance().getModContainer("hdskins").isPresent()));
        lines.addToGroup(ID, " Pony Level: " + ChatFormatting.YELLOW + PonyConfig.getInstance().ponyLevel.get());
        lines.addToGroup(ID, " Show Scale: "
                + boolString(PonyConfig.getInstance().showscale.get())
                + (PonyConfig.getInstance().showscale.get() ? " (" + PonyConfig.getInstance().getGlobalScaleFactor() + ")" : ""));
        if (player == null) {
            lines.addToGroup(ID, " Filly Cam: " + boolString(PonyConfig.getInstance().fillycam.get()));
        } else {
            Pony playerPony = MineLittlePony.getInstance().getManager().getPony(player);
            float cameraDistance = player.getScale() * (float)player.getAttributeValue(Attributes.CAMERA_DISTANCE) * playerPony.size().eyeDistanceFactor();

            lines.addToGroup(ID, " Filly Cam: " + boolString(PonyConfig.getInstance().fillycam.get())
                    + ", EH/F: " + ChatFormatting.YELLOW + player.getEyeHeight() + "/" + playerPony.size().eyeHeightFactor() + ChatFormatting.RESET
                    + ", ED/F: " + ChatFormatting.YELLOW + cameraDistance + "/" + playerPony.size().eyeDistanceFactor());
            lines.addToGroup(ID, "");
            lines.addToGroup(ID, ChatFormatting.UNDERLINE + "Current Player Skin: ");
            lines.addToGroup(ID, playerPony.metadata().attributes().entrySet().stream().map(entry -> {
                return entry.getKey() + "=" + ChatFormatting.AQUA + entry.getValue().name() + ChatFormatting.RESET + " (" + ChatFormatting.YELLOW + '#' + Integer.toString(entry.getValue().colorCode(), 16) + ChatFormatting.RESET + ")";
            }).toList());
        }
        lines.addToGroup(ID, "");
        lines.addToGroup(ID, ChatFormatting.UNDERLINE + "MineLP Debug Options: ");
        PonyConfig.getInstance().getCategory("debug").forEach(entry -> {
            lines.addToGroup(ID, "debug/" + entry.getKey() + ": " + ChatFormatting.AQUA + String.valueOf(entry.getValue().get()));
        });
        MobRenderers.REGISTRY.entrySet().stream()
                .filter(e -> e.getValue().option().get())
                .map(Map.Entry::getKey);

    }

    private String boolString(boolean on) {
        return (on ? ChatFormatting.GREEN : ChatFormatting.RED) + String.valueOf(on) + ChatFormatting.RESET;
    }
}
