package com.minelittlepony.client.render.entity.state;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.pony.Pony;

public class HostilePonyRenderState extends PonyRenderState {
    public boolean aggressive;

    public void updateState(ItemModelManager resolver, @Nullable LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
        super.updateState(resolver, entity, models, pony, mode);
        this.aggressive = entity instanceof HostileEntity m && m.isAttacking();
    }
}
