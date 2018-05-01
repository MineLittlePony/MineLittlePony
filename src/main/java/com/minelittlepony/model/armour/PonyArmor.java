package com.minelittlepony.model.armour;

import com.minelittlepony.model.capabilities.IModelWrapper;
import com.minelittlepony.pony.data.IPonyData;

import net.minecraft.inventory.EntityEquipmentSlot;

public class PonyArmor implements IModelWrapper {

    public final ModelPonyArmor chestplate;
    public final ModelPonyArmor leggings;

    public PonyArmor(ModelPonyArmor chest, ModelPonyArmor body) {
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

    public ModelPonyArmor getArmorForSlot(EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.LEGS) {
            return leggings;
        }

        return chestplate;
    }
}
