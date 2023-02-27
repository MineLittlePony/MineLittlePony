package com.minelittlepony.api.model.armour;

import net.minecraft.entity.EquipmentSlot;

import com.minelittlepony.api.model.IModel;

public interface IArmourModel {
    /**
     * Called to synchronise this armour's angles with that of another.
     *
     * @param model The other model to mimic
     */
    void synchroniseAngles(IModel model);

    /**
     * Prepares an armour model for rendering, first hiding all the pieces and then incrementally showing them as appropriate.
     *
     * @param slot      The armour slot being rendered
     * @param layer     The layer. INNER/OUTER
     *
     * @return false to skip this render pass.
     */
    boolean setVisibilities(EquipmentSlot slot, ArmourLayer layer, ArmourVariant variant);
}
