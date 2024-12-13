package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.DefaultPonySkinHelper;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ParaspriteModel;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.UUID;

public class VexRenderer extends MobEntityRenderer<VexEntity, VexRenderer.State, ParaspriteModel> {
    public static final Identifier PARASPRITE_PONIES = MineLittlePony.id("textures/entity/illager/vex_pony");

    public VexRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.VEX.createModel(), 0.3F);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(VexEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.uuid = entity.getUuid();
        state.bodyPitch = MathHelper.clamp((float)entity.getVelocity().horizontalLength() / 10F, 0, 0.1F);
        state.jawOpenAmount = Interpolator.linear(state.uuid).interpolate("jawOpen", entity.isCharging() ? 1 : 0, 10);
        state.wingRoll = 1 + (MathHelper.cos(state.age) / 3F) + 0.3F;
        state.wingYaw = 1 - (MathHelper.sin(state.age) / 2F);
        state.innerWingRoll = 0.5F + (-MathHelper.sin(state.age + MathHelper.PI / 4F) / 2F) - 0.3F;
        state.innerWingPitch = 0.5F - (MathHelper.cos(state.age + MathHelper.PI / 4F) / 3F) + 0.3F;
        if (entity.hasPassengers()) {
            state.yawDegrees = 0;
            state.pitch = 0;
        }
    }

    @Override
    protected void scale(State entity, MatrixStack matrices) {
        matrices.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    public Identifier getTexture(State state) {
        return MineLittlePony.getInstance().getVariatedTextures().get(PARASPRITE_PONIES, state.uuid).orElse(DefaultPonySkinHelper.STEVE);
    }

    public static class State extends LivingEntityRenderState {
        public UUID uuid;
        public float bodyPitch;
        public float jawOpenAmount;
        public float wingRoll;
        public float wingYaw;
        public float innerWingRoll;
        public float innerWingPitch;
    }
}
