package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ModelPillagerPony;
import com.minelittlepony.client.render.entity.feature.IllagerHeldItemFeature;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

public class PillagerRenderer extends PonyRenderer<PillagerEntity, ModelPillagerPony<PillagerEntity>> {

    private static final Identifier TEXTURES = new Identifier("minelittlepony", "textures/entity/illager/pillager_pony.png");

    public PillagerRenderer(EntityRenderDispatcher manager) {
        super(manager, ModelType.PILLAGER);
    }

    @Override
    public Identifier findTexture(PillagerEntity entity) {
        return TEXTURES;
    }

    @Override
    protected HeldItemFeature<PillagerEntity, ModelPillagerPony<PillagerEntity>> createItemHoldingLayer() {
        return new IllagerHeldItemFeature<>(this);
    }
}
