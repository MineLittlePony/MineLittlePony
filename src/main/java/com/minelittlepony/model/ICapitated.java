package com.minelittlepony.model;

import javax.annotation.Nullable;

public interface ICapitated<ModelRenderer> {
    /**
     * Gets the head of this capitated object.
     */
    ModelRenderer getHead();

    /**
     * Gets the main body
     */
    @Nullable
    ModelRenderer getBody();

    /**
     * Returns true if we're wearing any unconventional headgear (ie. a Pumpkin)
     */
    boolean hasHeadGear();

}
