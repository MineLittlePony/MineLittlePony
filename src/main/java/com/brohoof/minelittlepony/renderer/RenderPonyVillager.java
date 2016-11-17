package com.brohoof.minelittlepony.renderer;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.model.pony.ModelVillagerPony;

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
    protected ResourceLocation getEntityTexture(EntityVillager villager) {
        return getTexture(getTextureForVillager(villager));
    }

    private ResourceLocation getTextureForVillager(EntityVillager villager) {
        switch (villager.getProfession()) {
        case 0:
            return FARMER;
        case 1:
            return LIBRARIAN;
        case 2:
            return PRIEST;
        case 3:
            return SMITH;
        case 4:
            return BUTCHER;
        case 5:
        default:
            return GENERIC;
        }
    }
}
