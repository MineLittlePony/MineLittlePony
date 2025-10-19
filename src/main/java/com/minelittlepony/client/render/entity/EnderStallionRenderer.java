package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.EnderStallionModel;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class EnderStallionRenderer extends PonyRenderer<EndermanEntity, EnderStallionRenderer.State, EnderStallionModel> {
    public static final Identifier ENDERMAN = MineLittlePony.id("textures/entity/enderman/enderman_pony.png");
    private static final Identifier EYES = MineLittlePony.id("textures/entity/enderman/enderman_pony_eyes.png");

    private final Random rnd = new Random();
    private final ItemModelManager itemModelManager;

    public EnderStallionRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ENDERMAN, TextureSupplier.of(ENDERMAN));
        itemModelManager = context.getItemModelManager();;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        addPonyFeature(createHeldItemFeature(context));
        addPonyFeature(new StuckArrowsFeatureRenderer<EnderStallionModel>((PonyRenderer)this, context));
        addPonyFeature(new GlowingEyesFeature<EnderStallionRenderer.State, EnderStallionModel>(this, EYES));
        addPonyFeature(new PonyBodyPartFeature<>(this, m -> m instanceof ModelWithHorn, m -> ((ModelWithHorn)m).getHorn()));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void render(State entity, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        if (entity.angry) {
            matrices.translate(rnd.nextGaussian() / 50, 0, rnd.nextGaussian() / 50);
        }

        super.render(entity, matrices, queue, camera);
    }

    public class State extends SkeleponyRenderer.State {
        public boolean angry;
        @Nullable
        public BlockState carriedBlock;
        public boolean isBoss;

        @Override
        public void updateState(ItemModelManager resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            carriedBlock = entity instanceof EndermanEntity man ? man.getCarriedBlock() : null;
            super.updateState(resolver, entity, models, pony, mode);
            isAttacking = entity instanceof HostileEntity h && h.isAttacking();
            angry = entity instanceof EndermanEntity man && man.isAngry();
            attributes.wingsSpread = isAttacking || hurt;
            attributes.wingAngle = (isAttacking ? -0.6F : MathHelper.sin(age)) + ModelWithWings.WINGS_HALF_SPREAD_ANGLE;
        }

        @Override
        protected void updateHeldItems(ItemModelManager resolver, LivingEntity entity) {
            if (carriedBlock != null) {
                if (mainArm == Arm.RIGHT) {
                    itemModelManager.updateForLivingEntity(rightHandItemState, carriedBlock.getBlock().asItem().getDefaultStack(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
                    rightHeldItem.updateItemRenderState(this, itemModelManager, carriedBlock.getBlock().asItem().getDefaultStack(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
                } else {
                    itemModelManager.updateForLivingEntity(leftHandItemState, carriedBlock.getBlock().asItem().getDefaultStack(), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
                    leftHeldItem.updateItemRenderState(this, itemModelManager, carriedBlock.getBlock().asItem().getDefaultStack(), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
                }
            } else {
                rightHandItemState.clear();
                leftHandItemState.clear();
            }
        }

        @Override
        protected Race computeRace(@Nullable LivingEntity entity, Pony pony) {
            boolean isAlicorn = entity.getUuid().getLeastSignificantBits() % 3 == 0;
            isBoss = !isAlicorn && entity.getUuid().getLeastSignificantBits() % 90 == 0;
            return isAlicorn ? (pony.race().hasHorn() ? Race.ALICORN : Race.PEGASUS) : pony.race();
        }
    }
}
