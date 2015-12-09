package com.brohoof.minelittlepony.model.pony;

import com.brohoof.minelittlepony.model.ModelPony;

import net.minecraft.client.model.ModelRenderer;

public class pm_Human extends ModelPony {

    public ModelRenderer bipedEars;
    public ModelRenderer cloak;

    @Override
    protected boolean doCancelRender() {
        return true;
    }

}
