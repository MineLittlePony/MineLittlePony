package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.util.MathUtil;
import java.util.function.Predicate;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;

public class AquaticPlayerPonyRenderer extends FormChangingPlayerPonyRenderer {

    public AquaticPlayerPonyRenderer(EntityRendererFactory.Context context, boolean slim,
                                     Identifier alternateFormSkinId,
                                     Predicate<AbstractClientPlayerEntity> formModifierPredicate) {
        super(context, slim, alternateFormSkinId, formModifierPredicate);
    }

    @Override
    public PlayerEntityRenderState createRenderState() {
        return new State();
    }

    class State extends PlayerPonyRenderState {
        @Override
        public void updateState(ItemModelManager resolver, LivingEntity entity, PonyModel<?> model, Pony pony,
                                ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, model, pony, mode);

            Identifier skinOverride = null;
            if (entity instanceof AbstractClientPlayerEntity player) {
                skinOverride = getSkinOverride(player);
            }

            yOffset += skinOverride != null ? (0.6 + (isInSneakingPose ? 0.125 : 0)) : 0;
            pose = EntityPose.STANDING;
            isInSneakingPose = false;
            attributes.isCrouching = false;

            if (!isPreviewModel) {
                float state = skinOverride != null ? 100 : 0;
                float interpolated = attributes.getMainInterpolator().interpolate("seapony_state", state, 5);

                if (!MathUtil.compareFloats(interpolated, state)) {
                    var world = entity.getWorld();
                    var random = world.getRandom();
                    double x = random.nextTriangular(entity.getX(), 1);
                    double y = random.nextTriangular(entity.getY() + entity.getHeight() * 0.5F, 1);
                    double z = random.nextTriangular(entity.getZ(), 1);
                    world.addParticleClient(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
                }

                if (skinOverride != null && entity.getVelocity().length() > 0.1F) {
                    var world = entity.getWorld();
                    var random = world.getRandom();
                    double x = random.nextTriangular(entity.getX(), 1);
                    double y = random.nextTriangular(entity.getY(), 1);
                    double z = random.nextTriangular(entity.getZ(), 1);
                    world.addParticleClient(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
                }
            }
        }
    }
}
