package com.minelittlepony.renderer;

import com.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyZombieVillager extends RenderPonyMob<EntityZombieVillager> {

    private static final ResourceLocation GENERIC = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_villager_pony.png");
    private static final ResourceLocation FARMER = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_farmer_pony.png");
    private static final ResourceLocation LIBRARIAN = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_librarian_pony.png");
    private static final ResourceLocation PRIEST = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_priest_pony.png");
    private static final ResourceLocation SMITH = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_smith_pony.png");
    private static final ResourceLocation BUTCHER = new ResourceLocation("minelittlepony", "textures/entity/zombie_villager/zombie_butcher_pony.png");

    public RenderPonyZombieVillager(RenderManager renderManager) {
        super(renderManager, PMAPI.villager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityZombieVillager villager) {
        return getTexture(getTextureForVillager(villager));
    }

    private ResourceLocation getTextureForVillager(EntityZombieVillager villager) {
        switch (villager.func_190736_dl()) {
        case 0:
            return FARMER; // applejack
        case 1:
            return LIBRARIAN; // twilight sparkle
        case 2:
            return PRIEST; // fluttershy
        case 3:
            return SMITH; // rarity
        case 4:
            return BUTCHER; // rainbow dash
        case 5:
        default:
            return GENERIC; // pinkie pie
        }
    }

    @Override
    protected void rotateCorpse(EntityZombieVillager villager, float p_77043_2_, float p_77043_3_, float partialTicks) {
        if (villager.isConverting()) {
            p_77043_3_ += (float) (Math.cos(villager.ticksExisted * 3.25D) * Math.PI * 0.25D);
        }

        super.rotateCorpse(villager, p_77043_2_, p_77043_3_, partialTicks);
    }
}
