package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelSkeletonPony;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.layer.LayerPonyStrayOverlay;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.Identifier;

public class RenderPonySkeleton<Skeleton extends AbstractSkeletonEntity> extends RenderPonyMob<Skeleton, ModelSkeletonPony<Skeleton>> {

    public static final Identifier SKELETON = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = new Identifier("minelittlepony", "textures/entity/skeleton/stray_pony.png");

    public RenderPonySkeleton(EntityRenderDispatcher manager) {
        super(manager, new ModelSkeletonPony<>());
    }

    @Override
    public Identifier findTexture(Skeleton entity) {
        return SKELETON;
    }

    @Override
    protected LayerHeldPonyItem<Skeleton, ModelSkeletonPony<Skeleton>> createItemHoldingLayer() {
        return new LayerHeldPonyItemMagical<>(this);
    }

    public static class Stray extends RenderPonySkeleton<StrayEntity> {

        public Stray(EntityRenderDispatcher manager) {
            super(manager);
            addFeature(new LayerPonyStrayOverlay<>(this));
        }

        @Override
        public Identifier findTexture(StrayEntity entity) {
            return STRAY;
        }
    }

    public static class Wither extends RenderPonySkeleton<WitherSkeletonEntity> {

        public Wither(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public Identifier findTexture(WitherSkeletonEntity entity) {
            return WITHER;
        }

        @Override
        public void scale(WitherSkeletonEntity skeleton, float ticks) {
            super.scale(skeleton, ticks);
            GlStateManager.scalef(1.2F, 1.2F, 1.2F);
        }

    }

}
