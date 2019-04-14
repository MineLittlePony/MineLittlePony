package com.minelittlepony.model;

public interface ICapitated<ModelRenderer> {
    /**
     * Gets the head of this capitated object.
     */
    ModelRenderer getHead();

    /**
     * Returns true if we're wearing any unconventional headgear (ie. a Pumpkin)
     */
    boolean hasHeadGear();

}
