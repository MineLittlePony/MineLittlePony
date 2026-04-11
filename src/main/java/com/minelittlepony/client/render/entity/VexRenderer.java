package com.minelittlepony.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Vex;

import com.minelittlepony.api.pony.DefaultPonySkinHelper;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ParaspriteModel;
import com.minelittlepony.client.render.entity.state.PonifiedRenderState;
import com.minelittlepony.common.util.animation.Interpolator;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.UUID;

public class VexRenderer extends MobRenderer<Vex, VexRenderer.State, ParaspriteModel> {
    public static final Identifier PARASPRITE_PONIES = MineLittlePony.id("textures/entity/illager/vex_pony");

    public VexRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.VEX.createModel(), 0.3F);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Vex entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.uuid = entity.getUUID();
        state.bodyPitch = Mth.clamp((float)entity.getDeltaMovement().horizontalDistance() * 0.1F, 0, 0.1F);
        state.jawOpenAmount = Interpolator.linear(state.uuid).interpolate("jawOpen", entity.isCharging() ? 1 : 0, 10);
        state.wingRoll = 1 + (Mth.cos(state.ageInTicks) / 3F) + 0.3F;
        state.wingYaw = 1 - (Mth.sin(state.ageInTicks) / 2F);
        state.innerWingRoll = 0.5F + (-Mth.sin(state.ageInTicks + Mth.PI / 4F) / 2F) - 0.3F;
        state.innerWingPitch = 0.5F - (Mth.cos(state.ageInTicks + Mth.PI / 4F) / 3F) + 0.3F;
        if (entity.isVehicle()) {
            state.yRot = 0;
            state.xRot = 0;
        }
    }

    @Override
    protected void scale(State entity, PoseStack matrices) {
        matrices.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    public Identifier getTextureLocation(State state) {
        return MineLittlePony.getInstance().getVariatedTextures().get(PARASPRITE_PONIES, state.uuid).orElse(DefaultPonySkinHelper.STEVE);
    }

    public static class State extends LivingEntityRenderState implements PonifiedRenderState {
        public UUID uuid;
        public float bodyPitch;
        public float jawOpenAmount;
        public float wingRoll;
        public float wingYaw;
        public float innerWingRoll;
        public float innerWingPitch;
    }
}
