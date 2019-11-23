package com.minelittlepony.client.render.tileentities.skull;

import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.ModelDeadMau5Ears;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class PlayerSkullRenderer extends PonySkull {

    private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

    @Override
    public boolean canRender(PonyConfig config) {
        return config.ponyLevel.get() != PonyLevel.HUMANS;
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        deadMau5.setVisible(profile != null && "deadmau5".equals(profile.getName()));

        if (profile != null) {
            Identifier skin = SkinsProxy.instance.getSkinTexture(profile);

            if (skin != null) {
                return skin;
            }

            return DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(profile));
        }

        return DefaultSkinHelper.getTexture();
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        deadMau5.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }
}
