package com.minelittlepony.client.model.armour;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.ItemStack;

public interface ArmourTextureLookup {
    ArmourTexture getTexture(ItemStack stack, EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer);
}
