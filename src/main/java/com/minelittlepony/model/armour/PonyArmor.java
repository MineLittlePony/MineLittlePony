package com.minelittlepony.model.armour;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelWrapper;
import com.minelittlepony.pony.data.IPonyData;

import net.minecraft.inventory.EntityEquipmentSlot;

public class PonyArmor implements IModelWrapper {

    public final AbstractPonyModel chestplate;
    public final AbstractPonyModel leggings;

    public PonyArmor(AbstractPonyModel chest, AbstractPonyModel body) {
        chestplate = chest;
        leggings = body;
    }

    public void apply(IPonyData meta) {
        chestplate.metadata = meta;
        leggings.metadata = meta;
    }

    public void init() {
        chestplate.init(0, 1);
        leggings.init(0, 0.5f);
    }

    public AbstractPonyModel getArmorForSlot(EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.LEGS) {
            return leggings;
        }

        return chestplate;
    }
}
