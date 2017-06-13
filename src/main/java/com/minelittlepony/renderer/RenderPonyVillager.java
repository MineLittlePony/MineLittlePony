package com.minelittlepony.renderer;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.pony.ModelVillagerPony;
import com.minelittlepony.util.Villagers;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVillager extends RenderPonyMob<EntityVillager> {

    private static final ResourceLocation GENERIC = new ResourceLocation("minelittlepony", "textures/entity/villager/villager_pony.png");
    private static final ResourceLocation FARMER = new ResourceLocation("minelittlepony", "textures/entity/villager/farmer_pony.png");
    private static final ResourceLocation LIBRARIAN = new ResourceLocation("minelittlepony", "textures/entity/villager/librarian_pony.png");
    private static final ResourceLocation PRIEST = new ResourceLocation("minelittlepony", "textures/entity/villager/priest_pony.png");
    private static final ResourceLocation SMITH = new ResourceLocation("minelittlepony", "textures/entity/villager/smith_pony.png");
    private static final ResourceLocation BUTCHER = new ResourceLocation("minelittlepony", "textures/entity/villager/butcher_pony.png");

    public RenderPonyVillager(RenderManager rm) {
        super(rm, PMAPI.villager);
    }

    @Override
    protected void preRenderCallback(EntityVillager villager, float partialTicks) {
        if (villager.getGrowingAge() < 0) {
            this.shadowSize = 0.25F;
        } else {
            if (MineLittlePony.getConfig().showscale) {
                this.shadowSize = 0.4F;
            } else {
                this.shadowSize = 0.5F;
            }
        }
        ((ModelVillagerPony) this.mainModel).profession = villager.getProfession();

        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected ResourceLocation getTexture(EntityVillager villager) {
        return getTextureForVillager(villager.getProfession());
    }

    private ResourceLocation getTextureForVillager(int profession) {
        switch (profession) {
            case Villagers.FARMER:
                return FARMER;
            case Villagers.LIBRARIAN:
                return LIBRARIAN;
            case Villagers.PRIEST:
                return PRIEST;
            case Villagers.BLACKSMITH:
                return SMITH;
            case Villagers.BUTCHER:
                return BUTCHER;
            case Villagers.GENERIC:
            default:
                return GENERIC;
        }
    }
}
