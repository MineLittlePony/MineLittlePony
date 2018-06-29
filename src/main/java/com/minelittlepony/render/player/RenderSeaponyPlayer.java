package com.minelittlepony.render.player;

import com.minelittlepony.model.ModelWrapper;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderSeaponyPlayer extends RenderPonyPlayer {

    protected ModelWrapper seapony;
    protected ModelWrapper normalPony;

    public RenderSeaponyPlayer(RenderManager manager, boolean useSmallArms, ModelWrapper model, ModelWrapper alternate) {
        super(manager, useSmallArms, model);

        seapony = alternate;
        normalPony = model;
    }

    protected void updatePony(AbstractClientPlayer player) {
        super.updatePony(player);

        boolean wet = pony.isFullySubmerged(player);

        setPonyModel(wet ? seapony : normalPony);
    }

}
