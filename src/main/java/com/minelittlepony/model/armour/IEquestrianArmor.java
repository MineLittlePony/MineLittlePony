package com.minelittlepony.model.armour;

import net.minecraft.inventory.EntityEquipmentSlot;

public interface IEquestrianArmor {
    ModelPonyArmor getArmorForSlot(EntityEquipmentSlot slot);
}
