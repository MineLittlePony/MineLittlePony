package com.minelittlepony.api.events;

import com.minelittlepony.model.capabilities.IModelArmor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;

public interface IPreArmorEventHandler {
    <T extends ModelBiped & IModelArmor>
    boolean preRenderPonyArmor(T armour, Entity entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armourSlot);

    <T extends ModelBiped & IModelArmor>
    void postRenderPonyArmor(T armour, Entity entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armourSlot);
}
