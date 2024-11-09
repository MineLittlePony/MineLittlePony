package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.EnderStallionModel;
import com.minelittlepony.client.render.entity.feature.GlowingEyesFeature;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.SkeletonPonyRenderState;
import com.minelittlepony.client.render.entity.feature.GlowingEyesFeature.IGlowingRenderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class EnderStallionRenderer extends PonyRenderer<EndermanEntity, EnderStallionRenderer.State, EnderStallionModel> implements IGlowingRenderer {
    public static final Identifier ENDERMAN = MineLittlePony.id("textures/entity/enderman/enderman_pony.png");
    private static final Identifier EYES = MineLittlePony.id("textures/entity/enderman/enderman_pony_eyes.png");

    private final Random rnd = new Random();

    public EnderStallionRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ENDERMAN, TextureSupplier.of(ENDERMAN));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        addPonyFeature(createHeldItemFeature(context));
        addPonyFeature(new StuckArrowsFeatureRenderer<EnderStallionModel>((PonyRenderer)this, context));
        addPonyFeature(new GlowingEyesFeature<EnderStallionRenderer.State, EnderStallionModel>(this));
    }

    @Override
    protected HeldItemFeature<State, EnderStallionModel> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new HeldItemFeature<State, EnderStallionModel>(this, context.getItemRenderer());
    }

    @Override
    public void render(State entity, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        if (entity.angry) {
            matrices.translate(rnd.nextGaussian() / 50, 0, rnd.nextGaussian() / 50);
        }

        super.render(entity, matrices, vertices, light);
    }

    @Override
    public Identifier getEyeTexture() {
        return EYES;
    }

    public class State extends SkeletonPonyRenderState {
        public boolean angry;
        @Nullable
        public BlockState carriedBlock;

        public boolean isAlicorn;
        public boolean isBoss;

        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            isUnicorn = true;
            isAlicorn = entity.getUuid().getLeastSignificantBits() % 3 == 0;
            isBoss = !isAlicorn && entity.getUuid().getLeastSignificantBits() % 90 == 0;

            angry = ((EndermanEntity)entity).isAngry();
            carriedBlock = ((EndermanEntity)entity).getCarriedBlock();

            if (carriedBlock != null) {
                if (mainArm == Arm.RIGHT) {
                    rightHandStack = carriedBlock.getBlock().asItem().getDefaultStack();
                } else {
                    leftHandStack = carriedBlock.getBlock().asItem().getDefaultStack();
                }
            }
        }

        @Override
        public Race getRace() {
            return isAlicorn ? (super.getRace().hasHorn() ? Race.ALICORN : Race.PEGASUS) : super.getRace();
        }
    }
}
