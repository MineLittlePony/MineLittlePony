package com.minelittlepony.api.model.armour;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.ItemStack;

import com.minelittlepony.client.model.armour.ArmourTextureResolver;

public interface ArmourTextureLookup {
    ArmourTextureLookup DEFAULT = ArmourTextureResolver.INSTANCE;

    ArmourTexture getTexture(ItemStack stack, EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer);
}
