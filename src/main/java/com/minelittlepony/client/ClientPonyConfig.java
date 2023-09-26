package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.model.armour.ArmourTextureResolver;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.common.client.gui.VisibilityMode;
import com.minelittlepony.common.util.settings.Setting;

import java.nio.file.Path;

class ClientPonyConfig extends PonyConfig {
    /**
     * Visibility mode for the horse button.
     */
    public final Setting<VisibilityMode> horseButton = value("horseButton", VisibilityMode.AUTO)
            .addComment("Whether to show the mine little pony settings button on the main menu")
            .addComment("AUTO (default) - only show when HDSkins is not installed")
            .addComment("ON - always show")
            .addComment("OFF - never show");

    public ClientPonyConfig(Path path) {
        super(path);
        MobRenderers.REGISTRY.values().forEach(r -> value("entities", r.name, true));
        disablePonifiedArmour.onChanged(t -> ArmourTextureResolver.INSTANCE.invalidate());
    }

    @Override
    public void save() {
        super.save();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.calculateDimensions();
        }
    }
}