package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SkeleponyModel;
import com.minelittlepony.client.render.entity.feature.AbstractClothingFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;

public class SkeleponyRenderer<T extends AbstractSkeletonEntity> extends PonyRenderer<T, SkeleponyRenderer.State, SkeleponyModel<SkeleponyRenderer.State>> {
    public static final Identifier SKELETON = MineLittlePony.id("textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = MineLittlePony.id("textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = MineLittlePony.id("textures/entity/skeleton/stray_pony.png");

    public SkeleponyRenderer(EntityRendererFactory.Context context, Identifier texture, float scale) {
        super(context, ModelType.SKELETON, TextureSupplier.of(texture), scale);
    }

    @Override
    public SkeleponyRenderer.State createRenderState() {
        return new State();
    }

    public static SkeleponyRenderer<SkeletonEntity> skeleton(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, SKELETON, 1);
    }

    public static SkeleponyRenderer<StrayEntity> stray(EntityRendererFactory.Context context) {
        return PonyRenderer.appendFeature(new SkeleponyRenderer<>(context, STRAY, 1), StrayClothingFeature::new);
    }

    public static SkeleponyRenderer<WitherSkeletonEntity> wither(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, WITHER, 1.2F);
    }

    public static class StrayClothingFeature<
        T extends AbstractSkeletonEntity,
        S extends SkeleponyRenderer.State
    > extends AbstractClothingFeature<T, S, SkeleponyModel<S>> {
        public static final Identifier STRAY_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/stray_pony_overlay.png");

        private final SkeleponyModel<S> overlayModel = ModelType.SKELETON_CLOTHES.createModel();

        public StrayClothingFeature(LivingEntityRenderer<T, S, SkeleponyModel<S>> render) {
            super(render);
        }

        @Override
        protected SkeleponyModel<S> getOverlayModel() {
            return overlayModel;
        }

        @Override
        protected Identifier getOverlayTexture() {
            return STRAY_SKELETON_OVERLAY;
        }
    }

    public static class State extends PonyRenderState {
        public boolean isAttacking;

        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            isAttacking = entity instanceof HostileEntity h && h.isAttacking();
            if (entity.getUuid().getLeastSignificantBits() % 3 == 0) {
                race = Race.EARTH;
            }
        }

        @Override
        protected float getLegOutset() {
            if (attributes.isLyingDown) return 2.6f;
            if (attributes.isCrouching) return 0;
            return 4;
        }
    }
}
