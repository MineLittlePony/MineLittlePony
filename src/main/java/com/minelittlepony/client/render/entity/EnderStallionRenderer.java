package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.EnderStallionModel;
import com.minelittlepony.client.render.entity.feature.GlowingEyesFeature;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class EnderStallionRenderer extends PonyRenderer<EndermanEntity, EnderStallionRenderer.State, EnderStallionModel> {
    public static final Identifier ENDERMAN = MineLittlePony.id("textures/entity/enderman/enderman_pony.png");
    private static final Identifier EYES = MineLittlePony.id("textures/entity/enderman/enderman_pony_eyes.png");

    private final Random rnd = new Random();

    public EnderStallionRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ENDERMAN, TextureSupplier.of(ENDERMAN));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        addPonyFeature(createHeldItemFeature(context));
        addPonyFeature(new StuckArrowsFeatureRenderer<EnderStallionModel>((PonyRenderer)this, context));
        addPonyFeature(new GlowingEyesFeature<EnderStallionRenderer.State, EnderStallionModel>(this, EYES));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(EndermanEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        boolean isAlicorn = entity.getUuid().getLeastSignificantBits() % 3 == 0;
        state.isBoss = !isAlicorn && entity.getUuid().getLeastSignificantBits() % 90 == 0;
        state.race = isAlicorn ? (state.attributes.metadata.race().hasHorn() ? Race.ALICORN : Race.PEGASUS) : state.attributes.metadata.race();
        state.angry = entity.isAngry();
        state.carriedBlock = entity.getCarriedBlock();

        if (state.carriedBlock != null) {
            if (state.mainArm == Arm.RIGHT) {
                state.rightHandStack = state.carriedBlock.getBlock().asItem().getDefaultStack();
            } else {
                state.leftHandStack = state.carriedBlock.getBlock().asItem().getDefaultStack();
            }
        }
        state.attributes.wingsSpread = state.isAttacking;
        state.attributes.wingAngle = MathHelper.sin(state.age) + WingedPonyModel.WINGS_HALF_SPREAD_ANGLE;
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

    public class State extends SkeleponyRenderer.State {
        public boolean angry;
        @Nullable
        public BlockState carriedBlock;
        public boolean isBoss;
    }
}
