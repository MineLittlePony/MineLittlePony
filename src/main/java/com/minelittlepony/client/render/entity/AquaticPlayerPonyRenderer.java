package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.PreviewModel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.util.MathUtil;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;

public class AquaticPlayerPonyRenderer extends FormChangingPlayerPonyRenderer {

    public AquaticPlayerPonyRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, slim, DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, PonyPosture::isSeaponyModifier);
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int light) {
        super.render(player, entityYaw, tickDelta, stack, renderContext, light);

        if (!(player instanceof PreviewModel) && transformed && player.getVelocity().length() > 0.1F) {
            double x = player.getEntityWorld().getRandom().nextTriangular(player.getX(), 1);
            double y = player.getEntityWorld().getRandom().nextTriangular(player.getY(), 1);
            double z = player.getEntityWorld().getRandom().nextTriangular(player.getZ(), 1);
            player.getEntityWorld().addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
        }
    }

    @Override
    protected Race getPlayerRace(AbstractClientPlayerEntity entity, Pony pony) {
        Race race = super.getPlayerRace(entity, pony);
        return PonyPosture.isSeaponyModifier(entity) ? Race.SEAPONY : race == Race.SEAPONY ? Race.UNICORN : race;
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity player, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        if (PonyPosture.isSeaponyModifier(player)) {
            stack.translate(0, 0.6, 0);
            if (player.isInSneakingPose()) {
                stack.translate(0, 0.125, 0);
            }
        }
        super.setupTransforms(player, stack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    protected void updateForm(AbstractClientPlayerEntity player) {
        super.updateForm(player);
        if (!(player instanceof PreviewModel)) {
            float state = transformed ? 100 : 0;
            float interpolated = getInternalRenderer().getModels().body().getAttributes().getMainInterpolator().interpolate("seapony_state", state, 5);

            if (!MathUtil.compareFloats(interpolated, state)) {
                double x = player.getEntityWorld().getRandom().nextTriangular(player.getX(), 1);
                double y = player.getEntityWorld().getRandom().nextTriangular(player.getY() + player.getHeight() * 0.5F, 1);
                double z = player.getEntityWorld().getRandom().nextTriangular(player.getZ(), 1);

                player.getEntityWorld().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
            }
        }
    }
}
