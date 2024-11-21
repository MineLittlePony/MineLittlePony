package com.minelittlepony.client.render.entity;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.SeaponyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
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

    public SeaponyRenderer(EntityRendererFactory.Context context, TextureSupplier<State> texture, float scale) {
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
        state.cameraPosVec = getScaledCameraPosVec(entity, tickDelta, state.getScaleFactor());
        Entity cameraBeamTarget = getBeamTarget(entity);
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
            renderBeam(
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

    private static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d vec3d, float beamTicks, float f, float g) {
        float h = (float)(vec3d.length() + 1.0);
        vec3d = vec3d.normalize();
        float i = (float)Math.acos(vec3d.y);
        float j = (float) (Math.PI / 2) - (float)Math.atan2(vec3d.z, vec3d.x);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * (180.0F / (float)Math.PI)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * (180.0F / (float)Math.PI)));
        float k = beamTicks * 0.05F * -1.5F;
        float l = f * f;
        int m = 64 + (int)(l * 191.0F);
        int n = 32 + (int)(l * 191.0F);
        int o = 128 - (int)(l * 64.0F);
        float p = 0.2F;
        float q = 0.282F;
        float r = MathHelper.cos(k + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
        float s = MathHelper.sin(k + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
        float t = MathHelper.cos(k + (float) (Math.PI / 4)) * 0.282F;
        float u = MathHelper.sin(k + (float) (Math.PI / 4)) * 0.282F;
        float v = MathHelper.cos(k + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
        float w = MathHelper.sin(k + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
        float x = MathHelper.cos(k + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
        float y = MathHelper.sin(k + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
        float z = MathHelper.cos(k + (float) Math.PI) * 0.2F;
        float aa = MathHelper.sin(k + (float) Math.PI) * 0.2F;
        float ab = MathHelper.cos(k + 0.0F) * 0.2F;
        float ac = MathHelper.sin(k + 0.0F) * 0.2F;
        float ad = MathHelper.cos(k + (float) (Math.PI / 2)) * 0.2F;
        float ae = MathHelper.sin(k + (float) (Math.PI / 2)) * 0.2F;
        float af = MathHelper.cos(k + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
        float ag = MathHelper.sin(k + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
        float ai = 0.0F;
        float aj = 0.4999F;
        float ak = -1.0F + g;
        float al = ak + h * 2.5F;
        MatrixStack.Entry entry = matrices.peek();
        vertex(vertexConsumer, entry, z, h, aa, m, n, o, 0.4999F, al);
        vertex(vertexConsumer, entry, z, 0.0F, aa, m, n, o, 0.4999F, ak);
        vertex(vertexConsumer, entry, ab, 0.0F, ac, m, n, o, 0.0F, ak);
        vertex(vertexConsumer, entry, ab, h, ac, m, n, o, 0.0F, al);
        vertex(vertexConsumer, entry, ad, h, ae, m, n, o, 0.4999F, al);
        vertex(vertexConsumer, entry, ad, 0.0F, ae, m, n, o, 0.4999F, ak);
        vertex(vertexConsumer, entry, af, 0.0F, ag, m, n, o, 0.0F, ak);
        vertex(vertexConsumer, entry, af, h, ag, m, n, o, 0.0F, al);
        float am = MathHelper.floor(beamTicks) % 2 == 0 ? 0.5F : 0.0F;
        vertex(vertexConsumer, entry, r, h, s, m, n, o, 0.5F, am + 0.5F);
        vertex(vertexConsumer, entry, t, h, u, m, n, o, 1.0F, am + 0.5F);
        vertex(vertexConsumer, entry, x, h, y, m, n, o, 1.0F, am);
        vertex(vertexConsumer, entry, v, h, w, m, n, o, 0.5F, am);
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, float z, int red, int green, int blue, float u, float v) {
        vertexConsumer.vertex(matrix, x, y, z)
            .color(red, green, blue, 255)
            .texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
            .normal(matrix, 0.0F, 1.0F, 0.0F);
    }

    @Nullable
    private static Entity getBeamTarget(GuardianEntity guardian) {
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        return guardian.hasBeamTarget() ? guardian.getBeamTarget() : entity;
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
