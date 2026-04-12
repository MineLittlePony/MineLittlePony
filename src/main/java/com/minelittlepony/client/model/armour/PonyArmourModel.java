package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PonyArmourModel<T extends PonyRenderState> extends AbstractPonyModel<T> {
    public PonyArmourModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected boolean canAnimateArms(T state) {
        return !state.hasMagicGlow();
    }
}
