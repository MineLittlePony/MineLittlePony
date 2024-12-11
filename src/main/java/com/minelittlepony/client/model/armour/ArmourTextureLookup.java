package com.minelittlepony.client.model.armour;

import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;

public interface ArmourTextureLookup {
    ArmourTexture getTexture(ItemStack stack, EquipmentModel.LayerType layerType, EquipmentModel.Layer layer);
}
