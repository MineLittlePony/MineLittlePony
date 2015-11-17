 package com.brohoof.minelittlepony.renderer;

import org.lwjgl.opengl.GL11;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.Pony;
import com.brohoof.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVillager extends RenderPonyMob<EntityVillager> {

    public RenderPonyVillager(RenderManager rm) {
        super(rm, PMAPI.newPonyAdv_32);
    }

    @Override
    protected void preRenderCallback(EntityVillager villager, float partialTicks) {
        if (villager.getGrowingAge() < 0) {
            this.mobModel.size = 0;
            this.shadowSize = 0.25F;
        } else {
            this.mobModel.size = 1;
            if (MineLittlePony.getConfig().getShowScale().get()) {
                this.shadowSize = 0.4F;
            } else {
                this.shadowSize = 0.5F;
            }
        }

        GL11.glScalef(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVillager villager) {
        Pony aVillagerPony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(villager);
        return aVillagerPony.getTextureResourceLocation();
    }
}
