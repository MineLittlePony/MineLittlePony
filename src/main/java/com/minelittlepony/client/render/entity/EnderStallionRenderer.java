package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.EnderStallionModel;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.common.util.Untyped;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class EnderStallionRenderer extends PonyRenderer<EnderMan, EnderStallionRenderer.State, EnderStallionModel> {
    public static final Identifier ENDERMAN = MineLittlePony.id("textures/entity/enderman/enderman_pony.png");
    private static final Identifier EYES = MineLittlePony.id("textures/entity/enderman/enderman_pony_eyes.png");

    private final Random rnd = new Random();

    public EnderStallionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.ENDERMAN, TextureSupplier.of(ENDERMAN));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void addFeatures(EntityRendererProvider.Context context) {
        addPonyFeature(createHeldItemFeature(context));
        addPonyFeature(new ArrowLayer<>(Untyped.cast(this), context));
        addLayer(new GlowingEyesFeature<>(this, EYES));
        addLayer(new PonyBodyPartFeature<>(this, m -> ((ModelWithHorn)m).getHorn(), m -> m instanceof ModelWithHorn));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void submit(State entity, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (entity.angry) {
            matrices.translate(rnd.nextGaussian() / 50, 0, rnd.nextGaussian() / 50);
        }

        super.submit(entity, matrices, queue, camera);
    }

    public class State extends SkeleponyRenderer.State {
        public boolean angry;
        @Nullable
        public BlockState carriedBlock;
        public boolean isBoss;

        @Override
        public void updateState(ItemModelResolver resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            carriedBlock = entity instanceof EnderMan man ? man.getCarriedBlock() : null;
            super.updateState(resolver, entity, models, pony, mode);
            angry = entity instanceof EnderMan man && man.isAngry();
            attributes.wingsSpread = isAttacking || hasRedOverlay;
            attributes.wingAngle = (isAttacking ? -0.6F : Mth.sin(ageInTicks)) + ModelWithWings.WINGS_HALF_SPREAD_ANGLE;
        }

        @Override
        protected void updateHeldItems(ItemModelResolver resolver, LivingEntity entity) {
            if (carriedBlock != null) {
                if (mainArm == HumanoidArm.RIGHT) {
                    resolver.updateForLiving(rightHandItemState, carriedBlock.getBlock().asItem().getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
                    rightHeldItem.updateItemRenderState(this, resolver, carriedBlock.getBlock().asItem().getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
                } else {
                    resolver.updateForLiving(leftHandItemState, carriedBlock.getBlock().asItem().getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
                    leftHeldItem.updateItemRenderState(this, resolver, carriedBlock.getBlock().asItem().getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
                }
            } else {
                rightHandItemState.clear();
                leftHandItemState.clear();
            }
        }

        @Override
        protected Race computeRace(@Nullable LivingEntity entity, Pony pony) {
            boolean isAlicorn = entity.getUUID().getLeastSignificantBits() % 3 == 0;
            isBoss = !isAlicorn && entity.getUUID().getLeastSignificantBits() % 90 == 0;
            return isAlicorn ? (pony.race().hasHorn() ? Race.ALICORN : Race.PEGASUS) : pony.race();
        }
    }
}
