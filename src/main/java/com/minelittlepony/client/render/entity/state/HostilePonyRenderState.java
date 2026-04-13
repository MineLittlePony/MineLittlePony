package com.minelittlepony.client.render.entity.state;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.pony.Pony;

public class HostilePonyRenderState extends PonyRenderState {
    public boolean aggressive;

    public void updateState(ItemModelResolver resolver, @Nullable LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
        super.updateState(resolver, entity, models, pony, mode);
        this.aggressive = entity instanceof Monster m && m.isAggressive();
    }
}
