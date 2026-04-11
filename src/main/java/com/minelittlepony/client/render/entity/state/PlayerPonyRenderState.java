package com.minelittlepony.client.render.entity.state;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.phys.Vec3;

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
    public void updateState(ItemModelResolver resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
        smallArms = ((ClientAvatarEntity)entity).getSkin().model() == PlayerModelType.SLIM;

        PonyForm f = entity instanceof Player player ? PonyForm.of(player) : null;
        form = f == null ? PonyForm.DEFAULT : f.id();

        super.updateState(resolver, entity, models, pony, mode);
        yOffset = 0;
        if (entity.isPassenger()) {
            Vec3 attachment = entity.getDimensions(entity.getPose()).attachments().getNullable(EntityAttachment.VEHICLE, 0, 0);
            if (attachment != null) {
                yOffset += attachment.y() * (1 - attributes.size.eyeHeightFactor());
            }
        }
        wearabledTextures.clear();
        for (Wearable wearable : Wearable.REGISTRY.values()) {
            if (isWearing(wearable)) {
                SkinsProxy.getInstance().getSkin(wearable.getId(), (Avatar)entity).ifPresent(skin -> {
                    wearabledTextures.put(wearable, skin);
                });
            }
        }
    }
}
