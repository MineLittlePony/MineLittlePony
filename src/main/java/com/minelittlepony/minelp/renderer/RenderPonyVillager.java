package com.minelittlepony.minelp.renderer;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.minelp.Pony;
import com.minelittlepony.minelp.PonyManager;
import com.minelittlepony.minelp.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVillager extends RenderPonyMob<EntityVillager> {

    public RenderPonyVillager(RenderManager rm) {
        super(rm, PMAPI.newPonyAdv);
    }

    @Override
    protected void preRenderCallback(EntityVillager villager, float partialTicks) {
        if (villager.getGrowingAge() < 0) {
            this.mobModel.size = 0;
            this.shadowSize = 0.25F;
        } else {
            this.mobModel.size = 1;
            if (PonyManager.getInstance().getShowScale() == 1) {
                this.shadowSize = 0.4F;
            } else {
                this.shadowSize = 0.5F;
            }
        }

        GL11.glScalef(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVillager villager) {
        Pony aVillagerPony = PonyManager.getInstance().getPonyFromResourceRegistry(villager);
        return aVillagerPony.getTextureResourceLocation();
    }
}
