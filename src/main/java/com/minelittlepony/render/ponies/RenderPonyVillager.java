package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVillager extends RenderPonyMob<EntityVillager> {

    private static final ResourceLocation[] PROFESSIONS = {
            new ResourceLocation("minelittlepony", "textures/entity/villager/farmer_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/librarian_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/priest_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/smith_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/butcher_pony.png"),
            new ResourceLocation("minelittlepony", "textures/entity/villager/villager_pony.png")
    };

    public RenderPonyVillager(RenderManager rm) {
        super(rm, PMAPI.villager);
    }

    @Override
    protected void preRenderCallback(EntityVillager villager, float partialTicks) {
        super.preRenderCallback(villager, partialTicks);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected ResourceLocation getTexture(EntityVillager entity) {
        return PROFESSIONS[entity.getProfession()];
    }
}
