package com.minelittlepony.client.render.entity.state;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Wearable;

import java.util.HashMap;
import java.util.Map;

public class PlayerPonyRenderState extends PonyRenderState {
    public final Map<Wearable, Identifier> wearabledTextures = new HashMap<>();
    public boolean isPreviewModel;
    public double yOffset;
    public Identifier form = PonyForm.DEFAULT;

    @Override
    public void updateState(ItemModelManager resolver, LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
        smallArms = ((AbstractClientPlayerEntity)entity).getSkinTextures().model() == SkinTextures.Model.SLIM;

        PonyForm f = entity instanceof PlayerEntity player ? PonyForm.of(player) : null;
        form = f == null ? PonyForm.DEFAULT : f.id();

        super.updateState(resolver, entity, model, pony, mode);
        yOffset = 0;
        if (entity.hasVehicle()) {
            Vec3d attachment = entity.getDimensions(entity.getPose()).attachments().getPointNullable(EntityAttachmentType.VEHICLE, 0, 0);
            if (attachment != null) {
                yOffset += attachment.getY() * (1 - attributes.size.eyeHeightFactor());
            }
        }
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
