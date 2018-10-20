package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.util.render.ITextureSupplier;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVillager extends RenderPonyMob<EntityVillager> {

    private static final ITextureSupplier<Integer> PROFESSIONS = new VillagerProfessionTextureCache(
            "textures/entity/villager/%d_pony.png",
            new ResourceLocation("minelittlepony", "textures/entity/villager/farmer_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/librarian_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/priest_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/smith_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/butcher_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/villager_pony.png")
    );
    private static final ResourceLocation EGG = new ResourceLocation("minelittlepony", "textures/entity/villager/silly_pony.png");
    private static final ResourceLocation EGG_2 = new ResourceLocation("minelittlepony", "textures/entity/villager/tiny_silly_pony.png");

    public RenderPonyVillager(RenderManager manager) {
        super(manager, PMAPI.villager);
    }

    @Override
    public void preRenderCallback(EntityVillager villager, float ticks) {
        super.preRenderCallback(villager, ticks);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTexture(EntityVillager entity) {
        if ("Derpy".equals(entity.getCustomNameTag())) {
            if (entity.isChild()) {
                return EGG_2;
            }
            return EGG;
        }

        return PROFESSIONS.supplyTexture(entity.getProfession());
    }
}
