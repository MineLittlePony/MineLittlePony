package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.PonyPosture;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.*;
import com.minelittlepony.util.MathUtil;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class PlayerSeaponyRenderer extends PlayerPonyRenderer {
    public static final Identifier SKIN_TYPE_ID = new Identifier("minelp", "seapony");

    private final ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> wetPony;
    private final ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> dryPony;

    private boolean wet;

    public PlayerSeaponyRenderer(EntityRendererFactory.Context context, boolean slim,
            PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> wetModel,
            PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> dryModel) {
        super(context, slim, wetModel);

        dryPony = dryModel.<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>>create(slim);
        wetPony = getInternalRenderer().getModelWrapper();
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        if (wet) {
            return SkinsProxy.instance.getSkin(SKIN_TYPE_ID, player).orElseGet(() -> super.getTexture(player));
        }
        return super.getTexture(player);
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int light) {
        updateSeaponyState(player);
        super.render(player, entityYaw, tickDelta, stack, renderContext, light);

        if (wet && player.getVelocity().length() > 0.1F) {
            double x = player.getEntityWorld().getRandom().nextTriangular(player.getX(), 1);
            double y = player.getEntityWorld().getRandom().nextTriangular(player.getY(), 1);
            double z = player.getEntityWorld().getRandom().nextTriangular(player.getZ(), 1);

            player.getEntityWorld().addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
        }
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        if (wet) {
            stack.translate(0, 0.6, 0);
            boolean sneaking = entity.isInSneakingPose();
            float state = sneaking ? 100 : 0;
            float interpolated = getEntityPony(entity).metadata().getInterpolator(entity.getUuid()).interpolate("seapony_splashing", state, 5);

            if (sneaking) {
                stack.translate(0, 0.125, 0);
            }

            if (!MathUtil.compareFloats(interpolated, state)) {
                for (int i = 0; i < 10; i++) {
                    double x = entity.getEntityWorld().getRandom().nextTriangular(entity.getX(), 1);
                    double y = entity.getY() + 0.6;
                    double z = entity.getEntityWorld().getRandom().nextTriangular(entity.getZ(), 1);

                    entity.getEntityWorld().addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
                }
            }
        }
        super.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    protected void renderArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player, Arm side) {
        updateSeaponyState(player);
        super.renderArm(stack, renderContext, lightUv, player, side);
    }

    private void updateSeaponyState(AbstractClientPlayerEntity player) {
        IPony pony = getEntityPony(player);
        wet = PonyPosture.isSeaponyModifier(player);
        model = manager.setModel(wet ? wetPony : dryPony).body();

        float state = wet ? 100 : 0;
        float interpolated = pony.metadata().getInterpolator(player.getUuid()).interpolate("seapony_state", state, 5);

        if (!MathUtil.compareFloats(interpolated, state)) {
            double x = player.getEntityWorld().getRandom().nextTriangular(player.getX(), 1);
            double y = player.getEntityWorld().getRandom().nextTriangular(player.getY() + player.getHeight() * 0.5F, 1);
            double z = player.getEntityWorld().getRandom().nextTriangular(player.getZ(), 1);

            player.getEntityWorld().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }
}
