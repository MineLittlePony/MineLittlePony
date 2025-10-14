package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.feature.ClothingFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.Model.SinglePartModel;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.*;

public class SkeleponyRenderer<T extends AbstractSkeletonEntity, S extends SkeleponyRenderer.State> extends PonyRenderer<T, S, AlicornModel<S>> {
    public static final Identifier SKELETON = MineLittlePony.id("textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = MineLittlePony.id("textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = MineLittlePony.id("textures/entity/skeleton/stray_pony.png");
    public static final Identifier BOGGED = MineLittlePony.id("textures/entity/skeleton/bogged_pony.png");

    public static final Identifier STRAY_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/stray_pony_overlay.png");
    public static final Identifier BOGGED_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/bogged_pony_overlay.png");

    public SkeleponyRenderer(EntityRendererFactory.Context context, Identifier texture, float scale) {
        super(context, ModelType.SKELETON, TextureSupplier.of(texture), scale);
    }

    @SuppressWarnings("unchecked")
    @Override
    public S createRenderState() {
        return (S)new State();
    }

    @Override
    public BipedEntityModel.ArmPose getArmPose(BipedEntityModel.ArmPose initial, T entity, Arm arm) {
        if (arm == entity.getMainArm()) {
            ItemStack mainHand = entity.getMainHandStack();
            if (!mainHand.isEmpty()) {
                return mainHand.getItem() == Items.BOW && entity.isAttacking() ? ArmPose.BOW_AND_ARROW : ArmPose.ITEM;
            }
        }

        return initial;
    }

    public static SkeleponyRenderer<SkeletonEntity, State> skeleton(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, SKELETON, 1);
    }

    public static SkeleponyRenderer<StrayEntity, State> stray(EntityRendererFactory.Context context) {
        return PonyRenderer.appendFeature(new SkeleponyRenderer<StrayEntity, State>(context, STRAY, 1), ctx -> {
            return new ClothingFeature<State, AlicornModel<State>>(ctx, ModelType.SKELETON_CLOTHES, STRAY_SKELETON_OVERLAY);
        });
    }

    public static SkeleponyRenderer<BoggedEntity, BoggedState> bogged(EntityRendererFactory.Context context) {
        return PonyRenderer.appendFeature(PonyRenderer.appendFeature(new SkeleponyRenderer<>(context, BOGGED, 1) {
            @Override
            public BoggedState createRenderState() {
                return new BoggedState();
            }
        }, ctx -> {
            return new ClothingFeature<BoggedState, AlicornModel<BoggedState>>(ctx, ModelType.SKELETON_CLOTHES, BOGGED_SKELETON_OVERLAY);
        }), BoggedMushroomsFeature::new);
    }

    public static SkeleponyRenderer<WitherSkeletonEntity, State> wither(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, WITHER, 1.2F);
    }

    public static class BoggedMushroomsFeature<
        T extends AbstractSkeletonEntity,
        S extends SkeleponyRenderer.State
    > extends FeatureRenderer<BoggedState, AlicornModel<BoggedState>> {
        public static final Identifier MUSHROOMS = MineLittlePony.id("textures/entity/skeleton/bogged_pony_mushrooms.png");

        private final SinglePartModel model = ModelType.BOGGED_MUSHROOMS.createModel();

        public BoggedMushroomsFeature(LivingEntityRenderer<BoggedEntity, BoggedState, AlicornModel<BoggedState>> renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, BoggedState state, float limbAngle, float limbDistance) {
            if (!state.sheared) {
                matrices.push();
                getContextModel().transform(state, BodyPart.HEAD, matrices);
                getContextModel().head.applyTransform(matrices);
                queue.submitModel(model, Unit.INSTANCE, matrices, model.getLayer(MUSHROOMS), light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
                matrices.pop();
            }
        }
    }

    public static class BoggedState extends State {
        public boolean sheared;

        @Override
        public void updateState(ItemModelManager resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            sheared = entity instanceof BoggedEntity bogged && bogged.isSheared();
        }
    }

    public static class State extends PonyRenderState {
        public boolean isAttacking;

        public void updateState(ItemModelManager resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            isAttacking = entity instanceof HostileEntity h && h.isAttacking();
            race = entity.getUuid().getLeastSignificantBits() % 3 == 0 ? Race.EARTH : Race.UNICORN;
        }

        @Override
        protected float getLegOutset() {
            if (attributes.isLyingDown) return 2.6f;
            if (attributes.isCrouching) return 0;
            return 4;
        }
    }
}
