package com.minelittlepony.client.render.entity.state;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.SkinsProxy;
import com.minelittlepony.api.pony.meta.Wearable;

import java.util.HashMap;
import java.util.Map;

public class PlayerPonyRenderState extends PonyRenderState {
    public final Map<Wearable, Identifier> wearabledTextures = new HashMap<>();
    public boolean isPreviewModel;
    public double yOffset;

    @Override
    public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
        smallArms = ((ClientPlayerEntity)entity).getSkinTextures().model() == SkinTextures.Model.SLIM;
        super.updateState(entity, model, pony, mode);
        isPreviewModel = entity instanceof PreviewModel;
        wearabledTextures.clear();
        for (Wearable wearable : Wearable.REGISTRY.values()) {
            if (isWearing(wearable)) {
                SkinsProxy.getInstance().getSkin(wearable.getId(), (PlayerEntity)entity).ifPresent(skin -> {
                    wearabledTextures.put(wearable, skin);
                });
            }
        }
    }
}
