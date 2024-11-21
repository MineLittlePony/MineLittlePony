package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.SaddleableRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.DefaultPonySkinHelper;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SpikeModel;

import java.util.UUID;

public class StriderRenderer extends MobEntityRenderer<StriderEntity, StriderRenderer.State, SpikeModel> {
    public static final Identifier DRAGON_PONIES = MineLittlePony.id("textures/entity/strider/pony");
    public static final Identifier COLD_DRAGON_PONIES = MineLittlePony.id("textures/entity/strider/cold_pony");

    private static final Identifier SADDLE = MineLittlePony.id("textures/entity/strider/strider_saddle_pony.png");

    @SuppressWarnings({"unchecked", "rawtypes"})
    public StriderRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.STRIDER.createModel(), 0.5F);
        addFeature(new SaddleFeatureRenderer(this, ModelType.STRIDER_SADDLE.createModel(), SADDLE));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(StriderEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.uuid = entity.getUuid();
        state.cold = entity.isCold();
        state.saddled = entity.isSaddled();
        state.flailAmount = 1 + (float)MathHelper.clamp(entity.getVelocity().y * 10, 0, 7);
    }

    @Override
    public Identifier getTexture(State state) {
        return MineLittlePony.getInstance().getVariatedTextures().get(state.cold ? COLD_DRAGON_PONIES : DRAGON_PONIES, state.uuid).orElse(DefaultPonySkinHelper.STEVE);
    }

    @Override
    protected void scale(State state, MatrixStack stack) {
        float scale = 0.9375F;
        if (state.baby) {
            scale *= 0.5F;
            shadowRadius = 0.25F;
        } else {
            shadowRadius = 0.5F;
        }

        stack.scale(scale, scale, scale);
    }

    @Override
    protected boolean isShaking(State state) {
        return state.cold;
    }

    public static class State extends BipedEntityRenderState implements SaddleableRenderState {
        public UUID uuid;
        public boolean cold;
        public boolean saddled;
        public float flailAmount;

        @Override
        public boolean isSaddled() {
            return saddled;
        }
    }
}
