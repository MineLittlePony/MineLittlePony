package com.minelittlepony.client.render.entities.villager;

import com.minelittlepony.client.model.entities.ModelZombieVillagerPony;
import com.minelittlepony.util.resources.ITextureSupplier;

import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.ZombieVillagerEntity;

public class RenderPonyZombieVillager extends AbstractVillagerRenderer<ZombieVillagerEntity, ModelZombieVillagerPony> {

    private static final String TYPE = "zombie_villager";
    private static final ITextureSupplier<String> FORMATTER = ITextureSupplier.formatted("minelittlepony", "textures/entity/zombie_villager/zombie_%s.png");

    public RenderPonyZombieVillager(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, new ModelZombieVillagerPony(), TYPE, FORMATTER);
    }

    @Override
    protected void setupTransforms(ZombieVillagerEntity entity, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }

        super.setupTransforms(entity, move, rotationYaw, ticks);
    }
}
