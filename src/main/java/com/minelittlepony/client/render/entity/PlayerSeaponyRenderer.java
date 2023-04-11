package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.PonyPosture;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.*;
import com.minelittlepony.util.MathUtil;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;

public class PlayerSeaponyRenderer extends PlayerPonyRenderer {
    public static final Identifier SKIN_TYPE_ID = new Identifier("minelp", "seapony");

    private final ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> wetPony;
    private final ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> dryPony;

    public PlayerSeaponyRenderer(EntityRendererFactory.Context context, boolean slim,
            PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> wetModel,
            PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> dryModel) {
        super(context, slim, wetModel);

        dryPony = dryModel.<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>>create(slim);
        wetPony = getInternalRenderer().getModelWrapper();
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        if (PonyPosture.isPartiallySubmerged(player)) {
            return SkinsProxy.instance.getSkin(SKIN_TYPE_ID, player).orElseGet(() -> super.getTexture(player));
        }
        return super.getTexture(player);
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int light) {
        IPony pony = getEntityPony(player);
        boolean wet =
                (pony.race() == Race.SEAPONY || SkinsProxy.instance.getSkin(SKIN_TYPE_ID, player).isPresent())
                && PonyPosture.isPartiallySubmerged(player);

        model = manager.setModel(wet ? wetPony : dryPony).body();

        float state = wet ? 100 : 0;
        float interpolated = pony.metadata().getInterpolator(player.getUuid()).interpolate("seapony_state", state, 5);

        if (!MathUtil.compareFloats(interpolated, state)) {
            double x = player.getX() + (player.getEntityWorld().getRandom().nextFloat() * 2) - 1;
            double y = player.getY() + (player.getEntityWorld().getRandom().nextFloat() * 2);
            double z = player.getZ() + (player.getEntityWorld().getRandom().nextFloat() * 2) - 1;

            player.getEntityWorld().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        super.render(player, entityYaw, tickDelta, stack, renderContext, light);
    }
}
