package com.brohoof.minelittlepony.model.pony;

import com.brohoof.minelittlepony.model.AbstractPonyModel;

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
