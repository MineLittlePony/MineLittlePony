package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelZombieVillagerPony;
import com.minelittlepony.util.resources.FormattedTextureSupplier;
import com.minelittlepony.util.resources.ITextureSupplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

public class RenderPonyZombieVillager extends RenderPonyMob.Caster<ZombieVillagerEntity, ModelZombieVillagerPony> {

    private static final ITextureSupplier<String> FORMATTER = new FormattedTextureSupplier("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");

    private static final ITextureSupplier<ZombieVillagerEntity> PROFESSIONS = new VillagerProfessionTextureCache<>(FORMATTER);

    public RenderPonyZombieVillager(EntityRenderDispatcher manager) {
        super(manager, new ModelZombieVillagerPony());
    }

    @Override
    protected void addLayers() {
        ReloadableResourceManager resManager = (ReloadableResourceManager)MinecraftClient.getInstance().getResourceManager();

        addFeature(new VillagerClothingFeatureRenderer<>(this, resManager, "zombie_villager"));
    }

    @Override
    public void bindTexture(Identifier texture) {

        if (!"minelittlepony".contentEquals(texture.getNamespace())) {
            texture = new Identifier("minelittlepony", texture.getPath());
        }

        super.bindTexture(texture);
    }


    @Override
    public Identifier findTexture(ZombieVillagerEntity entity) {
        return PROFESSIONS.supplyTexture(entity);
    }

    @Override
    protected void setupTransforms(ZombieVillagerEntity entity, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }

        super.setupTransforms(entity, move, rotationYaw, ticks);
    }
}
