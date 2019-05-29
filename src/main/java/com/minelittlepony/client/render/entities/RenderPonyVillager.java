package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelVillagerPony;
import com.minelittlepony.util.resources.FormattedTextureSupplier;
import com.minelittlepony.util.resources.ITextureSupplier;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;

public class RenderPonyVillager extends RenderPonyMob.Caster<VillagerEntity, ModelVillagerPony<VillagerEntity>> {

    private static final ITextureSupplier<String> FORMATTER = new FormattedTextureSupplier("minelittlepony", "textures/entity/villager/%s_pony.png");

    private static final Identifier DEFAULT = FORMATTER.supplyTexture("villager");
    private static final Identifier EGG = FORMATTER.supplyTexture("silly");
    private static final Identifier EGG_2 = FORMATTER.supplyTexture("tiny_silly");

    private static final ITextureSupplier<VillagerData> PROFESSIONS = new VillagerProfessionTextureCache(FORMATTER, DEFAULT);

    public RenderPonyVillager(EntityRenderDispatcher manager) {
        super(manager, new ModelVillagerPony<>());
    }

    @Override
    public void scale(VillagerEntity villager, float ticks) {
        super.scale(villager, ticks);
        GlStateManager.scalef(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    @Override
    public Identifier findTexture(VillagerEntity entity) {
        if (entity.hasCustomName()) {
            String name = entity.getCustomName().getString();
            if ("Derpy".equals(name) || (entity.isBaby() && "Dinky".equals(name))) {
                if (entity.isBaby()) {
                    return EGG_2;
                }
                return EGG;
            }
        }

        return PROFESSIONS.supplyTexture(entity.getVillagerData());
    }
}
