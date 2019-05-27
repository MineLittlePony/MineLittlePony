package com.minelittlepony.client.render.entities;

import javax.annotation.Nonnull;

import com.minelittlepony.client.mixin.IResizeable;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelGuardianPony;
import com.minelittlepony.client.render.RenderPonyMob.Proxy;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

public class RenderPonyGuardian extends GuardianEntityRenderer {

    public static final Identifier SEAPONY = new Identifier("minelittlepony", "textures/entity/seapony.png");

    private final Proxy<GuardianEntity, ModelGuardianPony> ponyRenderer;

    public RenderPonyGuardian(EntityRenderDispatcher manager) {
        super(manager);

        features.clear();
        ponyRenderer = new Proxy<GuardianEntity, ModelGuardianPony>(features, manager, new ModelWrapper<>(new ModelGuardianPony())) {
            @Override
            public Identifier findTexture(GuardianEntity entity) {
                return SEAPONY;
            }

            @Override
            protected LayerHeldPonyItem<GuardianEntity, ModelGuardianPony> createItemHoldingLayer() {
                return new LayerHeldPonyItemMagical<>(this);
            }
        };
        model = ponyRenderer.getModel();
    }

    @Override
    @Nonnull
    protected final Identifier getTexture(GuardianEntity entity) {
        return ponyRenderer.getTextureFor(entity);
    }

    @Override
    protected void scale(GuardianEntity entity, float ticks) {
        ponyRenderer.scale(entity, ticks);
    }

    @Override
    public void render(GuardianEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        IResizeable resize = (IResizeable)entity;
        EntitySize origin = resize.getCurrentSize();

        // aligns the beam to their horns
        resize.setCurrentSize(EntitySize.resizeable(origin.width, entity instanceof ElderGuardianEntity ? 6 : 3));

        super.render(entity, x, y, z, entityYaw, partialTicks);

        // The beams in RenderGuardian leave lighting disabled, so we need to change it back. #MojangPls
        GlStateManager.enableLighting();
        resize.setCurrentSize(origin);
    }

    public static class Elder extends RenderPonyGuardian {

        public Elder(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        protected void scale(GuardianEntity entity, float ticks) {
            super.scale(entity, ticks);
            GlStateManager.scalef(2.35F, 2.35F, 2.35F);
        }
    }
}
