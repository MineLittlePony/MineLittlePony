package com.minelittlepony.client.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.client.render.entities.MobRenderers;
import com.minelittlepony.settings.PonyConfig;

public class ClientPonyConfig extends PonyConfig {

    public ClientPonyConfig() {
        initWith(MobRenderers.values());
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
