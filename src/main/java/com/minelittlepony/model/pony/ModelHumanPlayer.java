package com.minelittlepony.model.pony;

import com.minelittlepony.model.AbstractPonyModel;

import net.minecraft.client.model.ModelRenderer;

public class ModelHumanPlayer extends AbstractPonyModel {

    public ModelRenderer bipedEars;
    public ModelRenderer cloak;

    public ModelHumanPlayer(boolean smallArms) {
        super(smallArms);
    }

    @Override
    protected boolean doCancelRender() {
        return true;
    }

}
