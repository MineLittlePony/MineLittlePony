package com.minelittlepony.model.capabilities;

import javax.annotation.Nullable;

import net.minecraft.client.model.ModelRenderer;

public interface ICapitated {
    /**
     * Gets the head of this capitated object.
     */
    ModelRenderer getHead();

    /**
     * Returns true if we're wearing any uconventional headgear (ie. a Pumpkin)
     */
    boolean hasHeadGear();

}
