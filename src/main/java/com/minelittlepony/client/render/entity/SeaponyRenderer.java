package com.minelittlepony.client.render.entity;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.SeaponyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

public class SeaponyRenderer extends PonyRenderer<GuardianEntity, SeaponyRenderer.State, SeaponyModel<SeaponyRenderer.State>> {
    public static final Identifier SEAPONY = MineLittlePony.id("textures/entity/guardian/blueball.png");
    private static final Identifier SEAPONY_TEXTURES = MineLittlePony.id("textures/entity/guardian");
    public static final Identifier ELDER_SEAPONY = MineLittlePony.id("textures/entity/elder_guardian/blueball.png");
    private static final Identifier ELDER_SEAPONY_TEXTURES = MineLittlePony.id("textures/entity/elder_guardian");

    private static final Identifier EXPLOSION_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/guardian_beam.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

    public SeaponyRenderer(EntityRendererFactory.Context context, TextureSupplier<GuardianEntity> texture, float scale) {
        super(context, ModelType.GUARDIAN, texture, scale);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public static SeaponyRenderer guardian(EntityRendererFactory.Context context) {
        return new SeaponyRenderer(context, TextureSupplier.ofVariations(SEAPONY_TEXTURES, TextureSupplier.of(SEAPONY)), 1);
    }

    public static SeaponyRenderer elder(EntityRendererFactory.Context context) {
        return new SeaponyRenderer(context, TextureSupplier.ofVariations(ELDER_SEAPONY_TEXTURES, TextureSupplier.of(ELDER_SEAPONY)), ElderGuardianEntity.SCALE);
    }

    @Override
    public void updateRenderState(GuardianEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.spikesExtension = entity.getSpikesExtension(tickDelta);
        state.tailAngle = entity.getTailAngle(tickDelta);
        state.cameraPosVec = getScaledCameraPosVec(entity, tickDelta, state.size.scaleFactor());
        Entity cameraBeamTarget = GuardianEntityRenderer.getBeamTarget(entity);
        state.rotationVec = cameraBeamTarget != null ? entity.getRotationVec(tickDelta) : null;
        state.lookAtPos = cameraBeamTarget != null ? cameraBeamTarget.getCameraPosVec(tickDelta) : null;

        LivingEntity beamTarget = entity.getBeamTarget();
        if (beamTarget != null) {
            state.beamProgress = entity.getBeamProgress(tickDelta);
            state.beamTicks = entity.getBeamTicks() + tickDelta;
            state.beamTargetPos = fromLerpedPosition(beamTarget, (double)beamTarget.getHeight() * 0.5, tickDelta);
        } else {
            state.beamTargetPos = null;
        }
    }

    @Override
    public void render(State state, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        super.render(state, matrices, vertices, light);
        Vec3d vec3d = state.beamTargetPos;
        if (vec3d != null) {
            float f = state.beamTicks * 0.5F % 1.0F;
            matrices.push();
            matrices.translate(0.0F, state.standingEyeHeight, 0.0F);
            GuardianEntityRenderer.renderBeam(
                matrices,
                vertices.getBuffer(LAYER),
                vec3d.subtract(state.cameraPosVec),
                state.beamTicks,
                state.beamProgress,
                f
            );
            matrices.pop();
        }
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = MathHelper.lerp((double)delta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)delta, entity.lastRenderY, entity.getY()) + yOffset;
        double f = MathHelper.lerp((double)delta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(d, e, f);
    }

    public static Vec3d getScaledCameraPosVec(GuardianEntity entity, float tickDelta, float scale) {
        double d = MathHelper.lerp((double)tickDelta, entity.prevX, entity.getX());
        double e = MathHelper.lerp((double)tickDelta, entity.prevY, entity.getY()) + ((double)entity.getStandingEyeHeight() * scale);
        double f = MathHelper.lerp((double)tickDelta, entity.prevZ, entity.getZ());
        return new Vec3d(d, e, f);
    }

    public static class State extends PonyRenderState {
        public float spikesExtension;
        public float tailAngle;
        public Vec3d cameraPosVec = Vec3d.ZERO;
        @Nullable
        public Vec3d rotationVec;
        @Nullable
        public Vec3d lookAtPos;
        @Nullable
        public Vec3d beamTargetPos;
        public float beamTicks;
        public float beamProgress;
    }
}
