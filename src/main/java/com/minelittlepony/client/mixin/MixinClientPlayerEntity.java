package com.minelittlepony.client.mixin;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePony;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    public MixinClientPlayerEntity() { super(null, null); }

    @Override
    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        float value = super.getActiveEyeHeight(pose, dimensions);

        IPony pony = MineLittlePony.getInstance().getManager().getPony(this);

        if (!pony.getRace(false).isHuman()) {
            value *= pony.getMetadata().getSize().getEyeHeightFactor();
        }

        return value;
    }
}
