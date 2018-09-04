package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
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
    private static final ResourceLocation EGG = new ResourceLocation("minelittlepony", "textures/entity/villager/silly_pony.png");
    private static final ResourceLocation EGG_2 = new ResourceLocation("minelittlepony", "textures/entity/villager/tiny_silly_pony.png");


    private static final Map<Integer, ResourceLocation> MOD_PROFESSIONS = new HashMap<>();

    public RenderPonyVillager(RenderManager manager) {
        super(manager, PMAPI.villager);
    }

    @Override
    public void preRenderCallback(EntityVillager villager, float ticks) {
        super.preRenderCallback(villager, ticks);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected ResourceLocation getTexture(EntityVillager entity) {
        ResourceLocation texture = getVillagerTexture(entity);

        try {
            Minecraft.getMinecraft().getResourceManager().getResource(texture);
        } catch (IOException e) {
            return PROFESSIONS[5];
        }

        return texture;
    }

    private ResourceLocation getVillagerTexture(EntityVillager entity) {
        if ("Derpy".equals(entity.getCustomNameTag())) {
            if (entity.isChild()) {
                return EGG_2;
            }
            return EGG;
        }

        int profession = entity.getProfession();

        if (profession >= PROFESSIONS.length) {
            return MOD_PROFESSIONS.computeIfAbsent(profession, this::getModProfessionResource);
        }

        return PROFESSIONS[profession];
    }

    protected ResourceLocation getModProfessionResource(int professionId) {
        return new ResourceLocation("minelittlepony", String.format("textures/entity/villager/%d_pony.png", professionId));
    }
}
