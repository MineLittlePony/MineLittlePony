package com.minelittlepony.client.model.armour;

import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.item.ItemStack;

public interface ArmourTextureLookup {
    ArmourTexture getTexture(ItemStack stack, EquipmentModel.LayerType layerType, EquipmentModel.Layer layer);
}
