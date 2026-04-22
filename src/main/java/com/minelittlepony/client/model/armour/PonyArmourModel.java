package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PonyArmourModel<T extends PonyRenderState> extends AbstractPonyModel<T> {
    public PonyArmourModel(ModelPart tree) {
        super(tree, false);
        hat.skipDraw = true;
    }

    @Override
    protected boolean canAnimateArms(T state) {
        return !state.hasMagicGlow() || !PonyConfig.getInstance().tpsmagic.get();
    }
}
