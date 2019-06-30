package com.minelittlepony.client.mixin;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.IPony;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    public MixinClientPlayerEntity(ClientWorld clientWorld_1, GameProfile gameProfile_1) {
        super(clientWorld_1, gameProfile_1);
    }

    @Override
    public float getActiveEyeHeight(EntityPose entityPose_1, EntityDimensions entitySize_1) {
        float value = super.getActiveEyeHeight(entityPose_1, entitySize_1);

        IPony pony = MineLittlePony.getInstance().getManager().getPony(this);

        if (!pony.getRace(false).isHuman()) {
            value *= pony.getMetadata().getSize().getEyeHeightFactor();
        }

        return value;
    }
}
