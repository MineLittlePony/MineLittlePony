package com.minelittlepony.client.render.entity;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.common.util.Untyped;
import com.minelittlepony.util.MathUtil;

import java.util.function.Predicate;

public class AquaticPlayerPonyRenderer<Player extends Avatar & ClientAvatarEntity> extends FormChangingPlayerPonyRenderer<Player> {

    public AquaticPlayerPonyRenderer(
            EntityRendererProvider.Context context,
            boolean slim, Identifier alternateFormSkinId, Predicate<Player> formModifierPredicate) {
        super(context, slim, alternateFormSkinId, formModifierPredicate);
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new State();
    }

    class State extends PlayerPonyRenderState {
        @Override
        public void updateState(ItemModelResolver resolver, LivingEntity entity, Models<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, model, pony, mode);
            Identifier skinOverride = getSkinOverride(Untyped.cast(entity));
            yOffset += skinOverride != null ? (0.6 + (isCrouching ? 0.125 : 0)) : 0;
            pose = Pose.STANDING;
            isCrouching = false;
            attributes.isCrouching = false;
            if (!isPreviewModel) {
                float state = skinOverride != null ? 100 : 0;
                float interpolated = attributes.getMainInterpolator().interpolate("seapony_state", state, 5);

                if (!MathUtil.compareFloats(interpolated, state)) {
                    double x = entity.level().getRandom().triangle(entity.getX(), 1);
                    double y = entity.level().getRandom().triangle(entity.getY() + entity.getBbHeight() * 0.5F, 1);
                    double z = entity.level().getRandom().triangle(entity.getZ(), 1);

                    entity.level().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
                }

                if (!isPreviewModel && skinOverride != null && entity.getDeltaMovement().length() > 0.1F) {
                    double x = entity.level().getRandom().triangle(entity.getX(), 1);
                    double y = entity.level().getRandom().triangle(entity.getY(), 1);
                    double z = entity.level().getRandom().triangle(entity.getZ(), 1);
                    entity.level().addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
                }
            }
        }
    }
}
