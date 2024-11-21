package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;

import java.util.function.Predicate;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class FormChangingPlayerPonyRenderer extends PlayerPonyRenderer {
    private final Identifier alternateFormSkinId;
    private final Predicate<AbstractClientPlayerEntity> formModifierPredicate;

    public FormChangingPlayerPonyRenderer(EntityRendererFactory.Context context,
            boolean slim, Identifier alternateFormSkinId, Predicate<AbstractClientPlayerEntity> formModifierPredicate) {
        super(context, slim);
        this.alternateFormSkinId = alternateFormSkinId;
        this.formModifierPredicate = formModifierPredicate;
    }

    public PlayerEntityRenderState createRenderState() {
        return new State();
    }

    @Override
    public Identifier getTexture(PlayerEntityRenderState state) {
        if (((State)state).skinOverride != null) {
            return ((State)state).skinOverride;
        }
        return super.getTexture(state);
    }

    protected class State extends PlayerPonyRenderState {
        @Nullable
        public Identifier skinOverride;

        @Override
        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            skinOverride = formModifierPredicate.test((AbstractClientPlayerEntity)entity)
                    ? getSkinOverride((AbstractClientPlayerEntity)entity)
                    : null;
        }

        protected Identifier getSkinOverride(AbstractClientPlayerEntity player) {
            return SkinsProxy.getInstance().getSkin(alternateFormSkinId, player).orElse(null);
        }
    }
}
