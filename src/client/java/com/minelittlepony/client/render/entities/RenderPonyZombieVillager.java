package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelZombieVillagerPony;
import com.minelittlepony.util.resources.FormattedTextureSupplier;
import com.minelittlepony.util.resources.ITextureSupplier;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;

public class RenderPonyZombieVillager extends RenderPonyMob.Caster<ZombieVillagerEntity, ModelZombieVillagerPony> {

    private static final ITextureSupplier<String> FORMATTER = new FormattedTextureSupplier("minelittlepony", "textures/entity/zombie_villager/zombie_%s_pony.png");

    private static final Identifier DEFAULT = FORMATTER.supplyTexture("villager");
    private static final Identifier EGG = FORMATTER.supplyTexture("silly");
    private static final Identifier EGG_2 = FORMATTER.supplyTexture("tiny_silly");

    private static final ITextureSupplier<VillagerData> PROFESSIONS = new VillagerProfessionTextureCache(FORMATTER, DEFAULT);

    public RenderPonyZombieVillager(EntityRenderDispatcher manager) {
        super(manager, new ModelZombieVillagerPony());
    }

    @Override
    public Identifier findTexture(ZombieVillagerEntity entity) {
        String name = entity.getCustomName().getString();
        if ("Derpy".equals(name) || (entity.isBaby() && "Dinky".equals(name))) {
            if (entity.isBaby()) {
                return EGG_2;
            }
            return EGG;
        }

        return PROFESSIONS.supplyTexture(entity.getVillagerData());
    }

    @Override
    protected void setupTransforms(ZombieVillagerEntity entity, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.age * 3.25D) * (Math.PI / 4));
        }

        super.setupTransforms(entity, move, rotationYaw, ticks);
    }
}
