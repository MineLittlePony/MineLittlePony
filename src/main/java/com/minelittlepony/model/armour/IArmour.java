package com.minelittlepony.model.armour;

import net.minecraft.entity.EquipmentSlot;

import com.minelittlepony.model.IModel;

public interface IArmour {
    /**
     * Called to synchronise this armour's angles with that of another.
     *
     * @param model The other model to mimic
     */
    void synchroniseAngles(IModel model);

    /**
     * Resets the state of this model to all invisible.
     */
    void setInVisible();

    void setVariant(ArmourVariant variant);

    /**
     * Prepares an armour model for rendering, first hiding all the pieces and then incrementally showing them as appropriate.
     *
     * @param slot      The armour slot being rendered
     * @param layer     The layer. INNER/OUTER
     *
     * @return false to skip this render pass.
     */
    default boolean prepareToRender(EquipmentSlot slot, ArmourLayer layer) {
        setInVisible();

        switch (layer) {
            case OUTER:
                switch (slot) {
                    case HEAD:
                        showHelmet();
                        return true;
                    case FEET:
                        showBoots();
                        return true;
                    case CHEST:
                        showSaddle();
                        return true;
                    default:
                        return false;
                }
            case INNER:
                switch (slot) {
                    case LEGS:
                        showLeggings();
                        return true;
                    case CHEST:
                        showChestplate();
                        return true;
                    default:
                        return false;
                }
        }

        return false;
    }

    /**
     * Called to display the model's boots.
     */
    void showBoots();

    /**
     * Called to display the leg part of the model. Legs and boots use the same components just with separated texture files
     *  so it's reasonable that this would also call showBoots()
     */
    void showLeggings();

    /**
     * Shows the chestplate and saddle.
     *
     * @param outside true when being called to render the external cloth layer (saddle), false for the main body piece.
     */
    void showChestplate();

    /**
     * Ponies wear saddles. #dealwithit
     */
    void showSaddle();

    /**
     * Used to make the helmet visible
     */
    void showHelmet();
}
