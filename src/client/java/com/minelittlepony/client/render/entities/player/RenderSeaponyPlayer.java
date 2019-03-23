package com.minelittlepony.client.render.entities.player;

import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.common.pony.IPony;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;

public class RenderSeaponyPlayer extends RenderPonyPlayer {

    protected ModelWrapper seapony;
    protected ModelWrapper normalPony;

    public RenderSeaponyPlayer(RenderManager manager, boolean useSmallArms, ModelWrapper model, ModelWrapper alternate) {
        super(manager, useSmallArms, model);

        seapony = alternate;
        normalPony = model;
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayer player) {
        IPony pony = super.getEntityPony(player);

        boolean wet = pony.isPartiallySubmerged(player);

        mainModel = renderPony.setPonyModel(wet ? seapony : normalPony);

        float state = wet ? 100 : 0;
        float interpolated = pony.getMetadata().getInterpolator(player.getUniqueID()).interpolate("seapony_state", state, 5);

        if (!MathUtil.compareFloats(interpolated, state)) {
            double x = player.posX + (player.getEntityWorld().rand.nextFloat() * 2) - 1;
            double y = player.posY + (player.getEntityWorld().rand.nextFloat() * 2);
            double z = player.posZ + (player.getEntityWorld().rand.nextFloat() * 2) - 1;

            player.getEntityWorld().spawnParticle(EnumParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        return pony;
    }
}
