package com.minelittlepony.client.render.entity.state;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

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
    public void updateState(ItemModelManager resolver, LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
        smallArms = ((AbstractClientPlayerEntity)entity).getSkinTextures().model() == SkinTextures.Model.SLIM;
        super.updateState(resolver, entity, model, pony, mode);
        yOffset = 0;
        if (entity.hasVehicle()) {
            Vec3d attachment = entity.getDimensions(entity.getPose()).attachments().getPointNullable(EntityAttachmentType.VEHICLE, 0, 0);
            if (attachment != null) {
                yOffset += attachment.getY() * (1 - attributes.size.eyeHeightFactor());
            }
        }
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
