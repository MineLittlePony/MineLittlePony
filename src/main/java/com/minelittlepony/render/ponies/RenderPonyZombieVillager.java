package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.util.render.ITextureSupplier;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombieVillager extends RenderPonyMob<EntityZombieVillager> {

    private static final ITextureSupplier<Integer> PROFESSIONS = new VillagerProfessionTextureCache(
            "textures/entity/zombie_villager/zombie_%d_pony.png",
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_farmer_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_librarian_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_priest_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_smith_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_butcher_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_villager_pony.png")
    );
    private static final ResourceLocation EGG = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_silly_pony.png");
    private static final ResourceLocation EGG_2 = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_tiny_silly_pony.png");

    public RenderPonyZombieVillager(RenderManager manager) {
        super(manager, PMAPI.zombieVillager);
    }

    @Override
    public ResourceLocation getTexture(EntityZombieVillager entity) {
        String name = entity.getCustomNameTag();
        if ("Derpy".equals(name) || (entity.isChild() && "Dinky".equals(name))) {
            if (entity.isChild()) {
                return EGG_2;
            }
            return EGG;
        }

        return PROFESSIONS.supplyTexture(entity.getProfession());
    }

    @Override
    protected void applyRotations(EntityZombieVillager entity, float move, float rotationYaw, float ticks) {
        if (entity.isConverting()) {
            rotationYaw += (float) (Math.cos(entity.ticksExisted * 3.25D) * (Math.PI / 4));
        }

        super.applyRotations(entity, move, rotationYaw, ticks);
    }
}
