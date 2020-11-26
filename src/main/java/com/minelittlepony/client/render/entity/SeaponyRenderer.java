package com.minelittlepony.client.render.entity;

import javax.annotation.Nonnull;

import com.minelittlepony.client.mixin.IResizeable;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.GuardianPonyModel;
import com.minelittlepony.client.render.entity.PonyRenderer.Proxy;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;
import com.minelittlepony.client.render.entity.feature.GlowingItemFeature;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

public class SeaponyRenderer extends GuardianEntityRenderer {

    public static final Identifier SEAPONY = new Identifier("minelittlepony", "textures/entity/seapony.png");

    private final Proxy<GuardianEntity, GuardianPonyModel> ponyRenderer;

    public SeaponyRenderer(EntityRendererFactory.Context context) {
        super(context);

        features.clear();
        ponyRenderer = new Proxy<GuardianEntity, GuardianPonyModel>(features, context, ModelType.GUARDIAN) {
            @Override
            public Identifier findTexture(GuardianEntity entity) {
                return SEAPONY;
            }

            @Override
            protected HeldItemFeature<GuardianEntity, GuardianPonyModel> createItemHoldingLayer() {
                return new GlowingItemFeature<>(this);
            }
        };
        model = ponyRenderer.getModel();
    }

    @Override
    @Nonnull
    public final Identifier getTexture(GuardianEntity entity) {
        return ponyRenderer.getTextureFor(entity);
    }

    @Override
    protected void scale(GuardianEntity entity, MatrixStack stack, float ticks) {
        ponyRenderer.scale(entity, stack, ticks);
    }

    @Override
    public void render(GuardianEntity entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        IResizeable resize = (IResizeable)entity;
        EntityDimensions origin = resize.getCurrentSize();

        // aligns the beam to their horns
        resize.setCurrentSize(EntityDimensions.changing(origin.width, entity instanceof ElderGuardianEntity ? 6 : 3));

        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);

        // The beams in RenderGuardian leave lighting disabled, so we need to change it back. #MojangPls
        RenderSystem.enableLighting();
        resize.setCurrentSize(origin);
    }

    public static class Elder extends SeaponyRenderer {

        public Elder(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        protected void scale(GuardianEntity entity, MatrixStack stack, float ticks) {
            super.scale(entity, stack, ticks);
            stack.scale(2.35F, 2.35F, 2.35F);
        }
    }
}
