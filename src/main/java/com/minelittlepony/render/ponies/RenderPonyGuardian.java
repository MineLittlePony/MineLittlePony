package com.minelittlepony.render.ponies;

import javax.annotation.Nonnull;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;

public class RenderPonyGuardian extends RenderGuardian {

    public static final ResourceLocation SEAPONY = new ResourceLocation("minelittlepony", "textures/entity/seapony.png");

    private RenderPonyMob.Proxy<EntityGuardian> ponyRenderer;

    public RenderPonyGuardian(RenderManager manager) {
        super(manager);
        mainModel = PMAPI.seapony.getModel();

        ponyRenderer = new RenderPonyMob.Proxy<EntityGuardian>(manager, PMAPI.seapony) {
            @Override
            protected ResourceLocation getTexture(EntityGuardian entity) {
                return RenderPonyGuardian.this.getTexture(entity);
            }
        };
    }

    @Override
    @Nonnull
    protected final ResourceLocation getEntityTexture(EntityGuardian entity) {
        return ponyRenderer.getTextureFor(entity);
    }

    @Override
    protected void preRenderCallback(EntityGuardian entity, float ticks) {
        ponyRenderer.preRenderCallback(entity, ticks);
    }

    public void doRender(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float origin = entity.height;

        // aligns the beam to their horns
        entity.height = entity instanceof EntityElderGuardian ? 6 : 3;
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        // The beams in RenderGuardian leave lighting disabled, so we need to change it back. #MojangPls
        GlStateManager.enableLighting();
        entity.height = origin;
    }

    protected ResourceLocation getTexture(EntityGuardian entity) {
        return SEAPONY;
    }

    public static class Elder extends RenderPonyGuardian {

        public Elder(RenderManager manager) {
            super(manager);
        }

        @Override
        protected void preRenderCallback(EntityGuardian entity, float ticks) {
            super.preRenderCallback(entity, ticks);
            GlStateManager.scale(2.35F, 2.35F, 2.35F);
        }
    }
}
