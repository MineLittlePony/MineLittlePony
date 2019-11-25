package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ModelPillagerPony;
import com.minelittlepony.client.render.entity.feature.LayerHeldItemIllager;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItem;

public class RenderPonyPillager extends RenderPonyMob<PillagerEntity, ModelPillagerPony<PillagerEntity>> {

    private static final Identifier TEXTURES = new Identifier("minelittlepony", "textures/entity/illager/pillager_pony.png");

    public RenderPonyPillager(EntityRenderDispatcher manager) {
        super(manager, ModelType.PILLAGER);
    }

    @Override
    public Identifier findTexture(PillagerEntity entity) {
        return TEXTURES;
    }

    @Override
    protected LayerHeldPonyItem<PillagerEntity, ModelPillagerPony<PillagerEntity>> createItemHoldingLayer() {
        return new LayerHeldItemIllager<>(this);
    }
}
