package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.PonyPosture;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.*;
import com.minelittlepony.util.MathUtil;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;

public class PlayerSeaponyRenderer extends PlayerPonyRenderer {
    public static final Identifier SKIN_TYPE_ID = new Identifier("minelp", "seapony");

    private final ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> seapony;
    private final ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> normalPony;

    public PlayerSeaponyRenderer(EntityRendererFactory.Context context, boolean slim,
            PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> key) {
        super(context, slim, key);

        normalPony = ModelType.getPlayerModel(Race.UNICORN).<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>>create(slim);
        seapony = getModelWrapper();
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        return SkinsProxy.instance.getSkin(SKIN_TYPE_ID, player).orElseGet(() -> super.getTexture(player));
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayerEntity player) {
        IPony pony = super.getEntityPony(player);

        boolean wet = PonyPosture.isPartiallySubmerged(player);

        model = manager.setModel(wet ? seapony : normalPony).body();

        float state = wet ? 100 : 0;
        float interpolated = pony.metadata().getInterpolator(player.getUuid()).interpolate("seapony_state", state, 5);

        if (!MathUtil.compareFloats(interpolated, state)) {
            double x = player.getX() + (player.getEntityWorld().getRandom().nextFloat() * 2) - 1;
            double y = player.getY() + (player.getEntityWorld().getRandom().nextFloat() * 2);
            double z = player.getZ() + (player.getEntityWorld().getRandom().nextFloat() * 2) - 1;

            player.getEntityWorld().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        return pony;
    }
}
