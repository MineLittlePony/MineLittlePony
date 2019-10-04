package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelVillagerPony;
import com.minelittlepony.client.render.entities.villager.AbstractVillagerRenderer;
import com.minelittlepony.util.resources.ITextureSupplier;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.passive.VillagerEntity;

public class RenderPonyVillager extends AbstractVillagerRenderer<VillagerEntity, ModelVillagerPony<VillagerEntity>> {

    private static final String TYPE = "villager";
    private static final ITextureSupplier<String> FORMATTER = ITextureSupplier.formatted("minelittlepony", "textures/entity/villager/%s.png");

    public RenderPonyVillager(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, new ModelVillagerPony<>(), TYPE, FORMATTER);
    }

    @Override
    public void scale(VillagerEntity villager, float ticks) {
        super.scale(villager, ticks);
        GlStateManager.scalef(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

}
