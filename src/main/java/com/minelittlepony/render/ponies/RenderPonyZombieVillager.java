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

    public RenderPonyZombieVillager(RenderManager renderManager) {
        super(renderManager, PMAPI.villager);
    }

    @Override
    protected ResourceLocation getTexture(EntityZombieVillager villager) {
        return PROFESSIONS[villager.getProfession()];
    }

    @Override
    protected void applyRotations(EntityZombieVillager villager, float p_77043_2_, float p_77043_3_, float partialTicks) {
        if (villager.isConverting()) {
            p_77043_3_ += (float) (Math.cos(villager.ticksExisted * 3.25D) * Math.PI * 0.25D);
        }

        super.applyRotations(villager, p_77043_2_, p_77043_3_, partialTicks);
    }
}
