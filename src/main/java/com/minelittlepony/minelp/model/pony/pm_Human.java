package com.minelittlepony.minelp.model.pony;

import com.minelittlepony.minelp.model.ModelPony;

import net.minecraft.client.model.ModelRenderer;

public class pm_Human extends ModelPony {

    public ModelRenderer bipedEars;
    public ModelRenderer cloak;

    public pm_Human(String texture) {
        super(texture);
    }

    @Override
    protected boolean doCancelRender() {
        return true;
    }

}
