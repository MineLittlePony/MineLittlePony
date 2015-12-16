package com.brohoof.minelittlepony.model.pony;

import com.brohoof.minelittlepony.model.AbstractPonyModel;

import net.minecraft.client.model.ModelRenderer;

public class ModelHumanPlayer extends AbstractPonyModel {

    public ModelRenderer bipedEars;
    public ModelRenderer cloak;

    @Override
    protected boolean doCancelRender() {
        return true;
    }

}
