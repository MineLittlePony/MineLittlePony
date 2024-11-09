package com.minelittlepony.client.render.entity.state;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;

public class SkeletonPonyRenderState extends PonyRenderState {
    public boolean isUnicorn;
    public boolean isWithered;
    public boolean isAttacking;

    public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
        isUnicorn = entity.getUuid().getLeastSignificantBits() % 3 != 0;
        isWithered = entity instanceof WitherSkeletonEntity;
        isAttacking = entity instanceof HostileEntity h && h.isAttacking();
    }

    @Override
    public Race getRace() {
        return isUnicorn ? super.getRace() : Race.EARTH;
    }

    @Override
    protected float getLegOutset() {
        if (attributes.isLyingDown) return 2.6f;
        if (attributes.isCrouching) return 0;
        return 4;
    }
}
