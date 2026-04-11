package com.minelittlepony.client.render.entity;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.SeaponyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public class SeaponyRenderer extends PonyRenderer<Guardian, SeaponyRenderer.State, SeaponyModel<SeaponyRenderer.State>> {
    public static final Identifier SEAPONY = MineLittlePony.id("textures/entity/guardian/blueball.png");
    private static final Identifier SEAPONY_TEXTURES = MineLittlePony.id("textures/entity/guardian");
    public static final Identifier ELDER_SEAPONY = MineLittlePony.id("textures/entity/elder_guardian/blueball.png");
    private static final Identifier ELDER_SEAPONY_TEXTURES = MineLittlePony.id("textures/entity/elder_guardian");

    public SeaponyRenderer(EntityRendererProvider.Context context, TextureSupplier<Guardian> texture, float scale) {
        super(context, ModelType.GUARDIAN, texture, scale);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public static SeaponyRenderer guardian(EntityRendererProvider.Context context) {
        return new SeaponyRenderer(context, TextureSupplier.ofVariations(SEAPONY_TEXTURES, TextureSupplier.of(SEAPONY)), 1);
    }

    public static SeaponyRenderer elder(EntityRendererProvider.Context context) {
        return new SeaponyRenderer(context, TextureSupplier.ofVariations(ELDER_SEAPONY_TEXTURES, TextureSupplier.of(ELDER_SEAPONY)), ElderGuardian.ELDER_SIZE_SCALE);
    }

    @Override
    public void extractRenderState(Guardian entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.spikesExtension = entity.getSpikesAnimation(tickDelta);
        state.tailAngle = entity.getTailAnimation(tickDelta);
        state.cameraPosVec = getScaledCameraPosVec(entity, tickDelta, state.attributes.size.scaleFactor());
        Entity cameraBeamTarget = GuardianRenderer.getEntityToLookAt(entity);
        state.rotationVec = cameraBeamTarget != null ? entity.getViewVector(tickDelta) : null;
        state.lookAtPos = cameraBeamTarget != null ? cameraBeamTarget.getEyePosition(tickDelta) : null;

        LivingEntity beamTarget = entity.getActiveAttackTarget();
        if (beamTarget != null) {
            state.beamProgress = entity.getAttackAnimationScale(tickDelta);
            state.beamTicks = entity.getClientSideAttackTime() + tickDelta;
            state.beamTargetPos = fromLerpedPosition(beamTarget, (double)beamTarget.getBbHeight() * 0.5, tickDelta);
        } else {
            state.beamTargetPos = null;
        }
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        super.submit(state, matrices, queue, camera);
        Vec3 vec3d = state.beamTargetPos;
        if (vec3d != null) {
            float f = state.beamTicks * 0.5F % 1.0F;
            matrices.pushPose();
            matrices.translate(0.0F, state.eyeHeight, 0.0F);
            GuardianRenderer.renderBeam(
                matrices,
                queue,
                vec3d.subtract(state.cameraPosVec),
                state.beamTicks,
                state.beamProgress,
                f
            );
            matrices.popPose();
        }
    }

    private Vec3 fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = Mth.lerp((double)delta, entity.xOld, entity.getX());
        double e = Mth.lerp((double)delta, entity.yOld, entity.getY()) + yOffset;
        double f = Mth.lerp((double)delta, entity.zOld, entity.getZ());
        return new Vec3(d, e, f);
    }

    public static Vec3 getScaledCameraPosVec(Guardian entity, float tickDelta, float scale) {
        double d = Mth.lerp((double)tickDelta, entity.xOld, entity.getX());
        double e = Mth.lerp((double)tickDelta, entity.yOld, entity.getY()) + ((double)entity.getEyeHeight() * scale);
        double f = Mth.lerp((double)tickDelta, entity.zOld, entity.getZ());
        return new Vec3(d, e, f);
    }

    public static class State extends PonyRenderState {
        public float spikesExtension;
        public float tailAngle;
        public Vec3 cameraPosVec = Vec3.ZERO;
        @Nullable
        public Vec3 rotationVec;
        @Nullable
        public Vec3 lookAtPos;
        @Nullable
        public Vec3 beamTargetPos;
        public float beamTicks;
        public float beamProgress;
    }
}
