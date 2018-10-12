package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelBiped;

import com.minelittlepony.model.capabilities.IModelArmor;
import com.minelittlepony.model.capabilities.IModelWrapper;

public interface IEquestrianArmor<V extends ModelBiped & IModelArmor> extends IModelWrapper {
    /**
     * Gets the armour model to render for the given layer.
     */
    V getArmorForLayer(ArmorLayer layer);

    /**
     * The layer used to render a given armour piece.
     */
    enum ArmorLayer {
        /**
         * Fits snugly to the player's model.
         */
        INNER,
        /**
         * Hanging loose and sagging free
         */
        OUTER
    }
}
