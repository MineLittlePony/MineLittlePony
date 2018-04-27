package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombieVillager extends RenderPonyMob<EntityZombieVillager> {

    private static final ResourceLocation[] PROFESSIONS = {
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_farmer_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_librarian_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_priest_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_smith_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_butcher_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_villager_pony.png")
    };

    public RenderPonyZombieVillager(RenderManager manager) {
        super(manager, PMAPI.villager);
    }

    @Override
    protected ResourceLocation getTexture(EntityZombieVillager entity) {
        return PROFESSIONS[entity.getProfession() % PROFESSIONS.length];
    }

    @Override
    protected void applyRotations(EntityZombieVillager entity, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.ticksExisted * 3.25D) * (Math.PI / 4));
        }

        super.applyRotations(entity, move, rotationYaw, ticks);
    }
}
