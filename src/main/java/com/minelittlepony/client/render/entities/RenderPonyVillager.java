package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelVillagerPony;
import com.minelittlepony.client.render.layer.LayerVillagerClothing;
import com.minelittlepony.util.resources.FormattedTextureSupplier;
import com.minelittlepony.util.resources.ITextureSupplier;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

public class RenderPonyVillager extends RenderPonyMob.Caster<VillagerEntity, ModelVillagerPony<VillagerEntity>> {

    private static final ITextureSupplier<String> FORMATTER = new FormattedTextureSupplier("minelittlepony", "textures/entity/villager/%s.png");

    private static final ITextureSupplier<VillagerEntity> PROFESSIONS = new VillagerProfessionTextureCache<>(FORMATTER);

    public RenderPonyVillager(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, new ModelVillagerPony<>());
    }

    @Override
    protected void addLayers() {
        super.addLayers();
        addFeature(new LayerVillagerClothing<>(this, "villager"));
    }

    @Override
    public void scale(VillagerEntity villager, float ticks) {
        super.scale(villager, ticks);
        GlStateManager.scalef(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    @Override
    public Identifier findTexture(VillagerEntity entity) {
        return PROFESSIONS.supplyTexture(entity);
    }
}
