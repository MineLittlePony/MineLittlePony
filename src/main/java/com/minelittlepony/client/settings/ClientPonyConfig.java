package com.minelittlepony.client.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.client.render.entities.MobRenderers;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;

public class ClientPonyConfig extends PonyConfig {

    public ClientPonyConfig() {
        initWith(MobRenderers.values());
    }

    @Override
    public void setPonyLevel(PonyLevel ponylevel) {
        // only trigger reloads when the value actually changes
        if (ponylevel != getPonyLevel()) {
            HDSkins.getInstance().parseSkins();
        }

        super.setPonyLevel(ponylevel);
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
