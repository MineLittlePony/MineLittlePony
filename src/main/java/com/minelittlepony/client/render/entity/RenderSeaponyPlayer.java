package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.particle.ParticleTypes;

public class RenderSeaponyPlayer extends RenderPonyPlayer {

    protected final ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> seapony;
    protected final ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> normalPony;

    public RenderSeaponyPlayer(EntityRenderDispatcher manager, boolean slim, ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> key) {
        super(manager, slim, key);

        seapony = ModelType.<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>>getPlayerModel(Race.UNICORN).getKey(slim);
        normalPony = key;
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayerEntity player) {
        IPony pony = super.getEntityPony(player);

        boolean wet = pony.isPartiallySubmerged(player);

        model = renderPony.setPonyModel(wet ? seapony : normalPony).getBody();

        float state = wet ? 100 : 0;
        float interpolated = pony.getMetadata().getInterpolator(player.getUuid()).interpolate("seapony_state", state, 5);

        if (!MathUtil.compareFloats(interpolated, state)) {
            double x = player.getX() + (player.getEntityWorld().getRandom().nextFloat() * 2) - 1;
            double y = player.getY() + (player.getEntityWorld().getRandom().nextFloat() * 2);
            double z = player.getZ() + (player.getEntityWorld().getRandom().nextFloat() * 2) - 1;

            player.getEntityWorld().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        return pony;
    }
}
