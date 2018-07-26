package com.minelittlepony.render.player;

import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.pony.data.Pony;

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

    @Override
    public Pony getEntityPony(AbstractClientPlayer player) {
        Pony pony = super.getEntityPony(player);

        boolean wet = pony.isFullySubmerged(player);

        mainModel = renderPony.setPonyModel(wet ? seapony : normalPony);

        return pony;
    }

}
