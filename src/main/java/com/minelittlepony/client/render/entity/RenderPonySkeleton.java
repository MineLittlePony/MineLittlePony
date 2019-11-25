package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ModelSkeletonPony;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItem;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.entity.feature.LayerPonyStrayOverlay;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.Identifier;

public class RenderPonySkeleton<Skeleton extends AbstractSkeletonEntity> extends RenderPonyMob<Skeleton, ModelSkeletonPony<Skeleton>> {

    public static final Identifier SKELETON = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = new Identifier("minelittlepony", "textures/entity/skeleton/stray_pony.png");

    public RenderPonySkeleton(EntityRenderDispatcher manager) {
        super(manager, ModelType.SKELETON);
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
        public void scale(WitherSkeletonEntity skeleton, MatrixStack stack, float ticks) {
            super.scale(skeleton, stack, ticks);
            stack.scale(1.2F, 1.2F, 1.2F);
        }

    }

}
